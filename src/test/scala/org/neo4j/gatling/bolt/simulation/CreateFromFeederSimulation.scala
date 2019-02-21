package org.neo4j.gatling.bolt.simulation

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import org.neo4j.driver.v1.GraphDatabase.driver
import org.neo4j.gatling.bolt.Predef._
import org.neo4j.gatling.bolt.builder.SessionHelper._


import scala.util.Random

class CreateFromFeederSimulation extends Simulation {

  val feeder = Iterator.continually(Map(
    "email" -> (Random.alphanumeric.take(8).mkString + "@foo.com"),
    "name" ->  Random.alphanumeric.take(8).mkString,
    "age" -> Random.nextInt(100)
  ))

  val boltProtocol = bolt(driver("bolt://localhost:7687"))
  val testScenario = scenario("insertFromFeeder")
    .feed(feeder)
    .exec(
      cypher("create (n:Person{name:$name, email:$email, age: $age}) return n",
        // you have to use the "${}" notation to perform feeder data injection.
        // "${age}" resolves to a numeric value in the graph
        Map("name" -> "${name}", "email" -> "${email}", "age" -> "${age}"))
    ).pause(1)
  setUp(testScenario.inject(atOnceUsers(1))).protocols(boltProtocol)


}
