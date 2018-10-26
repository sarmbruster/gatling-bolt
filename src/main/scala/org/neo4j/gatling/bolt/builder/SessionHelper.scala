package org.neo4j.gatling.bolt.builder

import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression
import io.gatling.core.structure.ScenarioContext
import org.neo4j.gatling.bolt.CypherAction
import org.neo4j.gatling.bolt.protocol.BoltProtocol

object SessionHelper {

  def transaction(statements: Cypher*) = Transaction(statements)
  def cypher(cypher: Expression[String], parameters: Map[String,AnyRef] = null) = Cypher(cypher, parameters)

}

trait TransactionOrStatement extends ActionBuilder

case class Transaction(statements: Seq[Cypher]) extends TransactionOrStatement {
  override def build(ctx: ScenarioContext, next: Action) = {
    null
  }
}

case class Cypher(cypher: Expression[String], parameters: Map[String,AnyRef] ) extends TransactionOrStatement {
  override def build(ctx: ScenarioContext, next: Action) = {
    val statsEngine = ctx.coreComponents.statsEngine
    val driver = ctx.protocolComponentsRegistry.components(BoltProtocol.boltProtocolKey).protocol.driver
    CypherAction(driver, cypher, parameters, statsEngine, next)
  }
}
