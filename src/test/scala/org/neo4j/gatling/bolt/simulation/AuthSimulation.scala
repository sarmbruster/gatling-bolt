package org.neo4j.gatling.bolt.simulation

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.neo4j.driver.{AccessMode, AuthTokens}
import org.neo4j.driver.GraphDatabase.driver
import org.neo4j.gatling.bolt.Predef._
import org.neo4j.gatling.bolt.builder.SessionHelper._

class AuthSimulation extends Simulation {

  val boltProtocol = bolt(driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "123")))
  val testScenario = scenario("simpleCreate")
    .exec(
        session(AccessMode.WRITE,
          cypher("create (n:Dummy) return n")
        )
    ).pause(1)

  setUp(testScenario.inject(atOnceUsers(1))).protocols(boltProtocol)

}
