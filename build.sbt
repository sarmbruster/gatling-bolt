lazy val root=project
    .in(file("."))
    .settings(
      name := "gatling-bolt",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.12.10",
      libraryDependencies ++= Seq(
        "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.3.1",
        "io.gatling" % "gatling-test-framework" % "3.3.1",

        "org.neo4j.driver" % "neo4j-java-driver" % "4.2.0",
       // "com.dimafeng" %% "testcontainers-scala" % "0.38.9" % "test",
        "testcontainers" % "testcontainers-scala" % "0.38.9" % "test",
//        "org.neo4j.test" % "neo4j-harness" % "3.2.5" % "test",
//        "com.sun.jersey" % "jersey-core" % "1.19" % "test",

        "org.scalatest" %% "scalatest" % "3.1.2" % "test" // 3.0.8
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