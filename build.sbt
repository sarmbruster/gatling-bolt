lazy val root=project
    .in(file("."))
    .settings(
      name := "gatling-bolt",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.12.3",
//      scalaVersion := "2.11.8",
      libraryDependencies ++= Seq(
//        "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.5",
        "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.0.3",
//        "io.gatling" % "gatling-test-framework" % "2.2.5",
        "io.gatling" % "gatling-test-framework" % "3.0.3",

        "org.neo4j.driver" % "neo4j-java-driver" % "1.7.2",
        "com.dimafeng" %% "testcontainers-scala" % "0.20.0" % "test",

//        "org.neo4j.test" % "neo4j-harness" % "3.2.5" % "test",
//        "com.sun.jersey" % "jersey-core" % "1.19" % "test",

        "org.scalatest" %% "scalatest" % "3.0.1" % "test"
      )
    ).enablePlugins(GatlingPlugin)

publishArtifact in(Test, packageBin) := true

parallelExecution in Test := false
