package org.neo4j.gatling.bolt

import io.gatling.core.session.Expression
import org.neo4j.gatling.bolt.protocol.BoltProtocol

class BoltDsl {

  val bolt = BoltProtocol

//  def jdbc(requestName: Expression[String]) = BoltProtocolBuilderBase(requestName)
  def cypher(cypher: Expression[String], parameters: Map[String, AnyRef] = Map()) = CypherActionBuilder(cypher, parameters)

}
