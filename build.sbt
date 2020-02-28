import sbt._
import Keys._

name := "scala-playground"

lazy val commonSettings = Seq(
  organization := "com.dreamlines",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.13.1"
)

lazy val scalaPlayground = (project in file("."))
  .settings(commonSettings)
  .aggregate(doobie, monix, prettyP)

lazy val common = (project in file("common"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.1.0",
      "org.typelevel" %% "cats-effect" % "2.1.1",

      "org.scalactic" %% "scalactic" % "3.1.1",
      "org.scalatest" %% "scalatest" % "3.1.1" % "test",

      "org.postgresql" % "postgresql" % "42.0.0",
      "org.flywaydb" % "flyway-core" % "4.1.2"
    )
  )

lazy val doobie = (project in file("doobie"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-postgres" % "0.8.8",
      "org.tpolecat" %% "doobie-hikari"    % "0.8.8",
      "org.tpolecat" %% "doobie-postgres"  % "0.8.8", // Postgres driver 42.2.9 + type mappings.
      "org.tpolecat" %% "doobie-scalatest" % "0.8.8" % "test",

      "mysql" % "mysql-connector-java" % "6.0.6"
    )
  )
  .dependsOn(common)

lazy val monix = (project in file("monix"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "io.nats" % "java-nats-streaming" % "2.2.3",

      "com.typesafe.akka" %% "akka-http"   % "10.1.11",
      "com.typesafe.akka" %% "akka-stream" % "2.5.26",

      "io.monix" %% "monix" % "3.1.0"
    )
  )
  .dependsOn(common)

lazy val prettyP = (project in file("prettyP"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq("com.lihaoyi" %% "pprint" % "0.5.6")
  )
  .dependsOn(common)