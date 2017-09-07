package org.neo4j.gatling.bolt

import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression
import io.gatling.core.structure.ScenarioContext
import org.neo4j.gatling.bolt.protocol.BoltProtocol

case class CypherActionBuilder(cypher: Expression[String]) extends ActionBuilder {

  override def build(ctx: ScenarioContext, next: Action): Action = {
    val statsEngine = ctx.coreComponents.statsEngine
    val driver = ctx.protocolComponentsRegistry.components(BoltProtocol.boltProtocolKey).protocol.driver
    CypherAction(driver, cypher, statsEngine, next)
  }

}
