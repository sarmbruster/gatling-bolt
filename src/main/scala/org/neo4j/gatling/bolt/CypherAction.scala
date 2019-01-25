package org.neo4j.gatling.bolt

import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.validation.{Failure, Success}
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.stats.StatsEngine
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.util.NameGen
import org.neo4j.driver.v1
import org.neo4j.driver.v1.Driver

import scala.collection.JavaConverters._
import scala.util.Try

case class CypherAction(driver: Driver, cypher: Expression[String], parameters: Map[String,AnyRef], statsEngine: StatsEngine, next: Action) extends ChainableAction with NameGen {

//  def log(start: Long, end: Long, tried: Try[_], requestName: Expression[String], session: Session, statsEngine: StatsEngine): Unit = {
  def log(start: Long, end: Long, tried: Try[_], requestName: Expression[String], session: Session, statsEngine: StatsEngine): Unit = {
    val status = tried match {
      case scala.util.Success(_) => OK
      case scala.util.Failure(_) => KO
    }
    requestName.apply(session).map { resolvedRequestName =>
      statsEngine.logResponse(session, resolvedRequestName, start, end, status, None, None)
    }
  }

  override def name: String = genName("CypherAction")

  def withSession(block: v1.Session => Unit) : Unit = {
    var neo4jSession: v1.Session = null
    try {
      neo4jSession = driver.session()
      block(neo4jSession)
    } finally {
      neo4jSession.close()
    }
  }

  override def execute(session: Session): Unit = {
    val start = System.currentTimeMillis()
    withSession(neo4jSession => {
      val tried = Try(
        cypher.apply(session).map { resolvedCypher =>
          neo4jSession.run(resolvedCypher, parameters.asJava).consume()
        }
      )
      log(start, System.currentTimeMillis(), tried, cypher, session, statsEngine)
    })


    //println(protocol)
    /*val columnStrings = columns.map(
      t => (
        t.name.name.apply(session),
        t.dataType.dataType.apply(session),
        t.columnConstraint.map(constr => constr.constraint).map(expr => expr.apply(session)).getOrElse(Success(""))))
      .map {
        case (Success(columnName), Success(dataType), Success(constraint)) => s"$columnName $dataType $constraint"
        case _ => throw new IllegalArgumentException
      }.mkString(",")

    val validatedTableName = tableName.apply(session)
    validatedTableName match {
      case Success(name) =>
        val query = s"CREATE TABLE $name($columnStrings)"
        val tried = Try(DB autoCommit { implicit session =>
          SQL(query).execute().apply()
        })
        log(start, nowMillis, tried, cypher, session, statsEngine)

      case Failure(error) => throw new IllegalArgumentException(error)
    }*/
   /* val timing = ResponseTimings(start, nowMillis)
    cypher.apply(session).foreach { resolvedRequestName =>
      statsEngine.logResponse(session, resolvedRequestName, timing, OK, None, None)
    }*/
    next ! session
  }


}
