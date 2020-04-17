package org.neo4j.gatling.bolt

import java.time.Instant
import com.dimafeng.testcontainers.{Container, ForAllTestContainer, GenericContainer}
import io.gatling.core.action.Action
import io.gatling.core.session.Session
import org.neo4j.driver.v1.{Config, Driver, GraphDatabase}
import org.scalatest.{FlatSpec, Matchers}
import org.testcontainers.containers.wait.{LogMessageWaitStrategy, Wait}

trait BoltSpec extends FlatSpec with ForAllTestContainer with Matchers {

  var _bolt: Driver = null
  def bolt = _bolt

  val session = Session("scenario", 0, Instant.now().getEpochSecond)
  val next = new Action {
    override def name: String = "mockAction"

    override def execute(session: Session): Unit = {}
  }
  val statsEngine = new MockStatsEngine


  override val container = GenericContainer("neo4j:3.5.17",
    exposedPorts = Seq(7687),
    env = Map("NEO4J_AUTH" -> "none"),
    waitStrategy = new LogMessageWaitStrategy().withRegEx(".*Started.*\\s")
  )

  override def beforeStop(): Unit = {
    _bolt.close()
  }

  override def afterStart(): Unit = {
    _bolt = GraphDatabase.driver(s"bolt://${container.containerIpAddress}:${container.mappedPort(7687)}")
  }

}
