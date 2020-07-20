package org.neo4j.gatling.bolt

import akka.actor.ActorRef
import com.typesafe.scalalogging.StrictLogging
import io.gatling.commons.stats.Status
import io.gatling.core.session.{GroupBlock, Session}
import io.gatling.core.stats.StatsEngine
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.stats.writer.{DataWriterMessage, GroupMessage, ResponseMessage, UserEndMessage}

class MockStatsEngine extends StatsEngine with StrictLogging {

  var dataWriterMsg: List[DataWriterMessage] = List()

  override def start(): Unit = {}

  override def stop(replyTo: ActorRef, exception: Option[Exception]): Unit = {}

  override def logUserStart(session: Session): Unit = {}

  override def logUserEnd(userMessage: UserEndMessage): Unit = {}

  override def logResponse(
                            session: Session,
                            requestName: String,
                            startTimestamp: Long,
                            endTimestamp: Long,
                            status: Status,
                            responseCode: Option[String],
                            message: Option[String]
                          ): Unit =
    handle(ResponseMessage(
      session.scenario,
      session.userId,
      session.groupHierarchy,
      requestName,
      startTimestamp,
      endTimestamp,
      status,
      None,
      message
    ))

  override def logGroupEnd(session: Session, group: GroupBlock, exitTimestamp: Long): Unit = {
    handle(GroupMessage(session.scenario, session.userId, group.hierarchy, group.startTimestamp, exitTimestamp, group.cumulatedResponseTime, group.status))
  }

  override def logCrash(session: Session, requestName: String, error: String): Unit = {}

  private def handle(message: DataWriterMessage): Unit = {
    dataWriterMsg = message :: dataWriterMsg
    logger.info(message.toString)
  }

}
