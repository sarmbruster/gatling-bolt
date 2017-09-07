lazy val root=project
    .in(file("."))
    .settings(
      name := "gatling-bolt",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.12.3",
      libraryDependencies ++= Seq(
        "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.3.0",
        "io.gatling" % "gatling-test-framework" % "2.3.0",
        "org.neo4j.driver" % "neo4j-java-driver" % "1.4.3",
        "org.scalatest" %% "scalatest" % "3.0.1" % "test"
      )
    ).enablePlugins(GatlingPlugin)

publishArtifact in(Test, packageBin) := true

parallelExecution in Test := false
