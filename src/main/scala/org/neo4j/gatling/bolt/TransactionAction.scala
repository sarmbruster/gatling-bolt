package org.neo4j.gatling.bolt

import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.stats.StatsEngine
import io.gatling.core.util.NameGen
import org.neo4j.driver.v1
import org.neo4j.driver.v1.Driver

import scala.collection.JavaConverters._
import scala.util.Try

import org.neo4j.gatling.bolt.builder.Cypher

case class TransactionAction(driver: Driver, statements: Seq[Cypher],
                             statsEngine: StatsEngine, next: Action)
  extends ChainableAction with NameGen {

  def log(start: Long, end: Long, tried: Try[_], requestName: Expression[String], session: Session, statsEngine: StatsEngine): Unit = {
    val status = tried match {
      case scala.util.Success(_) => OK
      case scala.util.Failure(_) => KO
    }
    requestName.apply(session).map { resolvedRequestName =>
      statsEngine.logResponse(session, resolvedRequestName, start, end, status, None, Some(tried.toString))
    }
  }

  override def name: String = genName("TransactionAction")

  def withSession(block: v1.Session => Unit) : Unit = {
    var neo4jSession: v1.Session = null
    try {
      neo4jSession = driver.session()
      block(neo4jSession)
    }
    finally {
      neo4jSession.close()
    }
  }

  def withTransaction(neo4jSession: v1.Session, block: v1.Transaction => Unit) : Unit = {
    var tx: v1.Transaction = null
    try {
      tx = neo4jSession.beginTransaction()
      block(tx)
      tx.success()
    }
    finally {
      tx.close()
    }
  }

  def convertToPlainValue(value: Expression[AnyRef], session: Session): AnyRef = {
    return value.apply(session).toOption.getOrElse(null)
  }

  override def execute(session: Session): Unit = {
    val start = System.currentTimeMillis()

    withSession(neo4jSession => {
      withTransaction(neo4jSession, tx => {
        val tried = Try(
          statements.foreach { stmt =>
            val resolvedParams : Map[String, AnyRef] =
              stmt.parameters.mapValues(convertToPlainValue(_, session))
            stmt.cypher.apply(session).map { resolvedCypher =>
              tx.run(resolvedCypher, resolvedParams.asJava).consume()
            }
          }
        )
        log(start, System.currentTimeMillis(), tried, statements(0).cypher, session, statsEngine)
      })
    })

    next ! session
  }


}
