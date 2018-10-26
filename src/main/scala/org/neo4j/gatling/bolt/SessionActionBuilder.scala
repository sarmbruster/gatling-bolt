package org.neo4j.gatling.bolt

import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext
import org.neo4j.driver.v1.AccessMode
import org.neo4j.gatling.bolt.builder.{Transaction, TransactionOrStatement}

case class SessionActionBuilder(accessMode: AccessMode, seq: Seq[TransactionOrStatement]) extends ActionBuilder {

  override def build(ctx: ScenarioContext, next: Action) = {
    null
  }
}
