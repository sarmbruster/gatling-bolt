package org.neo4j.gatling.bolt.protocol

import akka.actor.ActorSystem
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.{CoreComponents, protocol}
import io.gatling.core.protocol.{Protocol, ProtocolKey}
import org.neo4j.driver._

case class BoltProtocol(driver: Driver) extends Protocol {
}

object BoltProtocol {

  val boltProtocolKey: ProtocolKey[BoltProtocol, BoltComponents] = new ProtocolKey[BoltProtocol, BoltComponents] {

    override def protocolClass: Class[protocol.Protocol] = classOf[BoltProtocol].asInstanceOf[Class[io.gatling.core.protocol.Protocol]]

    override def defaultProtocolValue(configuration: GatlingConfiguration): BoltProtocol = {
      return BoltProtocol(GraphDatabase.driver("bolt://localhost:7687"))
    }

    //      throw new IllegalStateException("Can't provide a default value for JdbcProtocol")

    override def newComponents(coreComponents: CoreComponents): BoltProtocol => BoltComponents = {
      protocol => BoltComponents(protocol)
    }

  }
  def apply(driver: Driver): BoltProtocol = new BoltProtocol(driver)

}
