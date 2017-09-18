package org.neo4j.gatling.bolt

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.neo4j.gatling.bolt.Predef._

class CypherParametersSimulation extends Simulation {

  val boltConfig = bolt.uri("bolt://localhost:7687")

  val testScenario = scenario("parameters")
    .exec(
        cypher("create (n:Person{name:'John'}) return n")
    ).pause(5)
    .exec(
      cypher("match (n) where n.name=$name return count(n)", Map("name" -> "John"))
    )

  setUp(testScenario.inject(atOnceUsers(1))).protocols(boltConfig)

}
