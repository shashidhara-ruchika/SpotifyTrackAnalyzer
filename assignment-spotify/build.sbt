import scala.collection.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .settings(
    name := "assignment-spotify"
  )

libraryDependencies ++= Seq(
    // HTTP Client for making API requests
    "com.lihaoyi" %% "requests" % "0.8.0",

    // JSON parsing library
    "com.lihaoyi" %% "ujson" % "3.0.0",
    "com.lihaoyi" %% "upickle" % "3.1.0",

    // Apache Spark for data processing
    "org.apache.spark" %% "spark-core" % "3.3.0",
    "org.apache.spark" %% "spark-sql" % "3.3.0",
    "org.apache.spark" %% "spark-streaming" % "3.3.0",

    // Logging for better debugging
    "ch.qos.logback" % "logback-classic" % "1.4.7",

    // Scala Tests
    "org.scalatest" %% "scalatest" % "3.2.9" % Test,

    // Scala Mock
    "org.scalamock" %% "scalamock" % "5.1.0" % Test
)

javaOptions ++= Seq(
    "--add-opens", "java.base/java.nio=ALL-UNNAMED",
    "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED",
    "--add-opens", "java.base/java.util=ALL-UNNAMED",
    "--add-opens", "java.base/java.lang.invoke=ALL-UNNAMED"
)

