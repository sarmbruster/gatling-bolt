package org.neo4j.gatling.bolt.protocol

case object BoltProtocolBuilderBase {

  def uri(uri: String) = BoltProtocolBuilder(uri)

}

case class BoltProtocolBuilder(uri: String) {
  def build = BoltProtocol(uri)
}
