package org.neo4j.gatling.bolt

import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.stats.StatsEngine
import io.gatling.core.util.NameGen
import org.neo4j.driver._
import scala.collection.JavaConverters._
import scala.util.Try

case class CypherAction(driver: Driver, dbName: String, cypher: Expression[String], parameters: Map[String,Expression[AnyRef]], statsEngine: StatsEngine, next: Action) extends ChainableAction with NameGen {

  def log(start: Long, end: Long, tried: Try[_], requestName: Expression[String], session: Session, statsEngine: StatsEngine): Unit = {
    val status = tried match {
      case scala.util.Success(_) => OK
      case scala.util.Failure(_) => KO
    }
    requestName.apply(session).map { resolvedRequestName =>
      statsEngine.logResponse(session, resolvedRequestName, start, end, status, None, Some(tried.toString))
    }
  }

  override def name: String = genName("CypherAction")

  def withSession(block: org.neo4j.driver.Session => Unit) : Unit = {
    var neo4jSession: org.neo4j.driver.Session = null
    try {
      neo4jSession = driver.session(if (dbName == null) SessionConfig.defaultConfig() else SessionConfig.forDatabase(dbName) )
      block(neo4jSession)
    } finally {
      neo4jSession.close()
    }
  }

  def convertToPlainValue(value: Expression[AnyRef], session: Session): AnyRef = {
    value.apply(session).toOption.getOrElse(null)
  }

  override def execute(session: Session): Unit = {
    val start = System.currentTimeMillis()
    val resolvedParams : Map[String, AnyRef] = parameters.mapValues(convertToPlainValue(_, session))

    withSession(neo4jSession => {
      val tried = Try(
        cypher.apply(session).map { resolvedCypher =>
          neo4jSession.run(resolvedCypher, resolvedParams.asJava).consume()
        }
      )
      log(start, System.currentTimeMillis(), tried, cypher, session, statsEngine)
    })

    next ! session
  }


}
