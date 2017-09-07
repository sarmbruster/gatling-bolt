package org.neo4j.gatling.bolt

import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.util.ClockSingleton._
import io.gatling.commons.validation.{Failure, Success}
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.stats.StatsEngine
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.util.NameGen
import org.neo4j.driver.v1
import org.neo4j.driver.v1.Driver

import scala.util.Try

case class CypherAction(driver: Driver, cypher: Expression[String], statsEngine: StatsEngine, next: Action) extends ChainableAction with NameGen {

//  def log(start: Long, end: Long, tried: Try[_], requestName: Expression[String], session: Session, statsEngine: StatsEngine): Unit = {
  def log(start: Long, end: Long, tried: Try[_], requestName: Expression[String], session: Session, statsEngine: StatsEngine): Unit = {
    val timing = ResponseTimings(start, end)
    val status = tried match {
      case scala.util.Success(_) => OK
      case scala.util.Failure(_) => KO
    }
    requestName.apply(session).foreach { resolvedRequestName =>
      statsEngine.logResponse(session, resolvedRequestName, timing, status, None, None)
    }
  }

  override def name: String = genName("CypherAction")

  override def execute(session: Session): Unit = {
    val start = nowMillis

    var neo4jSession: v1.Session = null
    try {
      neo4jSession = driver.session()

      cypher.apply(session).foreach { resolvedCypherString =>

        val tried = Try(
          neo4jSession.run(resolvedCypherString).consume()
        )
        log(start, nowMillis, tried, cypher, session, statsEngine)
      }

    } finally {
      neo4jSession.close()
    }

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
    val timing = ResponseTimings(start,nowMillis)
    cypher.apply(session).foreach { resolvedRequestName =>
      statsEngine.logResponse(session, resolvedRequestName, timing, OK, None, None)
    }
    next ! session
  }


}
