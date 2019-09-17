package org.neo4j.gatling.bolt

import io.gatling.core.Predef._
import io.gatling.core.stats.writer.ResponseMessage

class CypherActionSpec extends BoltSpec {

  "CypherAction" should "use the request name in the log message" in {
    val cypher = "create (n) return n"
    val action = CypherAction(bolt, cypher, Map.empty, statsEngine, next)

    action.execute(session)

    statsEngine.dataWriterMsg should have length 1
    statsEngine.dataWriterMsg.head(session).toOption.get.asInstanceOf[ResponseMessage].name should equal(cypher)

  }

}
