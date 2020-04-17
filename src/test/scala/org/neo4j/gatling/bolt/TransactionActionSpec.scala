package org.neo4j.gatling.bolt

import io.gatling.core.Predef._
import io.gatling.core.stats.writer.ResponseMessage
import org.neo4j.gatling.bolt.builder.SessionHelper._

class TransactionActionSpec extends BoltSpec {

  "TransactionAction" should "use the request name in the log message" in {
    val query = "CREATE (n) RETURN n"
    val statements = Seq(cypher(query, Map.empty))
    val action = TransactionAction(bolt, statements, statsEngine, next)

    action.execute(session)

    statsEngine.dataWriterMsg should have length 1
    statsEngine.dataWriterMsg.head(session).toOption.get.asInstanceOf[ResponseMessage].name should equal(query)

  }

}
