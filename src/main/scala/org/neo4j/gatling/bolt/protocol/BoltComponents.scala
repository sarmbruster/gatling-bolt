package org.neo4j.gatling.bolt.protocol

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session

case class BoltComponents(protocol: BoltProtocol) extends ProtocolComponents {

  override def onStart: Option[(Session) => Session] = None

  override def onExit: Option[(Session) => Unit] = None

}
