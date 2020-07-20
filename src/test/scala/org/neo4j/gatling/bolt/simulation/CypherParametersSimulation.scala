package org.neo4j.gatling.bolt.simulation

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.neo4j.driver.GraphDatabase.driver
import org.neo4j.gatling.bolt.Predef._
import org.neo4j.gatling.bolt.builder.SessionHelper._

class CypherParametersSimulation extends Simulation {

  val boltConfig = bolt(driver("bolt://localhost:7687"))

  val testScenario = scenario("parameters")
    .exec(
          cypher("create (n:Person{name:$name}) return n", Map("name" -> "John"))
    ).pause(5)
    .exec(
      cypher("match (n) where n.name=$name return count(n)", Map("name" -> "John"))
    )

  setUp(testScenario.inject(atOnceUsers(1))).protocols(boltConfig)

}
