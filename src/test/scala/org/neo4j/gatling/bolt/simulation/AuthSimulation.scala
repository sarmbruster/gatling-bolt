package org.neo4j.gatling.bolt.simulation

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.neo4j.driver.v1.AuthTokens
import org.neo4j.driver.v1.GraphDatabase.driver
import org.neo4j.gatling.bolt.Predef._

class AuthSimulation extends Simulation {

  val boltProtocol = bolt(driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "123")))
  val testScenario = scenario("simpleCreate")
    .exec(
        cypher("create (n:Dummy) return n")
    ).pause(1)

  setUp(testScenario.inject(atOnceUsers(1))).protocols(boltProtocol)

}
