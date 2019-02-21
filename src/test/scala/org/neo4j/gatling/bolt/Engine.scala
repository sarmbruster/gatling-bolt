package org.neo4j.gatling.bolt

import io.gatling.core.config.GatlingPropertiesBuilder
import io.gatling.app.Gatling
import org.neo4j.gatling.bolt.simulation._

object Engine extends App {

  val props = new GatlingPropertiesBuilder()
    .simulationClass(classOf[SimpleCreateSimulation].getName)
    .simulationClass(classOf[CausalClusterSimulation].getName)
    .simulationClass(classOf[AuthSimulation].getName)
    .simulationClass(classOf[CypherParametersSimulation].getName)
      .simulationClass(classOf[CreateFromFeederSimulation].getName)
//    .dataDirectory(IDEPathHelper.dataDirectory.toString)
//    .resultsDirectory(IDEPathHelper.resultsDirectory.toString)
//    .bodiesDirectory(IDEPathHelper.bodiesDirectory.toString)
//    .binariesDirectory(IDEPathHelper.mavenBinariesDirectory.toString)

  Gatling.fromMap(props.build)
}
