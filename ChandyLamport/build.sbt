ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

lazy val root = (project in file("."))
  .settings(
    name := "ProjectScala"
  )


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % "2.6.14",
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.6.14",
  "com.typesafe.akka" %% "akka-serialization-jackson" % "2.6.14",
  "com.google.guava" % "guava" % "33.0.0-jre",
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
  "com.typesafe.akka" %% "akka-slf4j" % "2.6.14",
  "ch.qos.logback" % "logback-classic" % "1.4.14"
)

