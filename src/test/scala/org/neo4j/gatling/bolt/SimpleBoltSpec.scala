package org.neo4j.gatling.bolt

import org.scalatest.Assertions._
import org.testcontainers.shaded.com.google.common.collect.Iterators

class SimpleBoltSpec extends BoltSpec {

  "A create cypher statement" should "succeed" in {
    assert(Iterators.getOnlyElement(bolt.session().run("create (n) return count(n) as c")).get("c").asInt() ==1)

  }

}
