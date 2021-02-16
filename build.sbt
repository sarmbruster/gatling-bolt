lazy val root=project
    .in(file("."))
    .settings(
      name := "gatling-bolt",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.12.10",
      libraryDependencies ++= Seq(
        "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.3.1" % "provided",
        "io.gatling" % "gatling-test-framework" % "3.3.1" % "test",
        "org.neo4j.driver" % "neo4j-java-driver" % "4.2.0",
        "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.38.9" % "test",
        "org.scalatest" %% "scalatest" % "3.1.2" % "test"
      )
    ).enablePlugins(GatlingPlugin)

publishArtifact in(Test, packageBin) := true

parallelExecution in Test := false

//
// When building a fat jar, discard the dependencies duplicate files that are under META-INF
//
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
