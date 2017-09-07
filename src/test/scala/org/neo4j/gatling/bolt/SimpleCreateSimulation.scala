package org.neo4j.gatling.bolt

import org.neo4j.gatling.bolt.Predef._
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation

class SimpleCreateSimulation extends Simulation {

  val boltConfig = bolt.uri("bolt://localhost:7687")

  val testScenario = scenario("simpleCreate")
    .exec(
        cypher("create (n:Dummy) return n")
    )

  setUp(testScenario.inject(atOnceUsers(1))).protocols(boltConfig)

}
