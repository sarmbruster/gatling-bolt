package org.neo4j.gatling.bolt.simulation

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import scala.concurrent.duration._
import org.neo4j.driver.GraphDatabase.driver
import org.neo4j.driver.AuthTokens.basic
import org.neo4j.gatling.bolt.Predef._
import org.neo4j.gatling.bolt.builder.SessionHelper._


import scala.util.Random

class ConcurrentNodeWriters extends Simulation {

  val feeder = Iterator.continually(Map(
    "email" -> (Random.alphanumeric.take(15).mkString + "@foo.com"),
    "name" ->  Random.alphanumeric.take(15).mkString,
    "age" -> Random.nextInt(100)
  ))

  val writerBolt = bolt(driver("bolt+routing://{DBID}.databases.neo4j.io:7687",
    basic("{USERNAME}", "{PASSWORD}")))
  val writerScenario = scenario("writer")
    .feed(feeder)
    .exec(
      transaction(
        cypher("CREATE (n:Person{name:$name, email:$email, age: $age})-[:Jibes]->(m:Person{name:$name, email:$email, age:$age}) RETURN n",
          Map("name" -> "${name}", "email" -> "${email}", "age" -> "${age}")),
        cypher("MATCH (n:Person) WHERE n.name = $name RETURN n",
          Map("name" -> "${name}"))
      )
    ).pause(100 milliseconds)
    .exec(
      transaction(
        cypher("MATCH (n:Person)-[:Jibes]->(m:Person) WHERE n.name = $name RETURN n,m",
          Map("name" -> "${name}"))
      )
    ).pause(200 milliseconds)

  setUp(
    writerScenario.inject(
      rampConcurrentUsers(0) to (100) during (20 seconds),
      constantConcurrentUsers(100) during (1800 seconds)
    ).protocols(writerBolt))
}
