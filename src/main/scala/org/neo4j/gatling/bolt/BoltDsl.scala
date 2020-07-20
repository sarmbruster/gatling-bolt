package org.neo4j.gatling.bolt

import org.neo4j.driver.AccessMode
import org.neo4j.gatling.bolt.protocol.BoltProtocol
import org.neo4j.gatling.bolt.builder.{TransactionOrStatement}

class BoltDsl {

  val bolt = BoltProtocol

  def session(accessMode: AccessMode, transactionOrCypher: TransactionOrStatement, moreTransactionOrCypher: TransactionOrStatement*) = SessionActionBuilder(accessMode, transactionOrCypher +: moreTransactionOrCypher)

}
