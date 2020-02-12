import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "uk.gov.nationalarchives.tdr"

lazy val root = (project in file("."))
  .settings(
    name := "tdr-graphql-client",
    libraryDependencies += scalaTest % Test,
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.13.0",
      "io.circe" %% "circe-parser" % "0.13.0",
      "io.circe" %% "circe-generic" % "0.13.0",
      "io.circe" %% "circe-generic-extras" % "0.13.0",
      "com.softwaremill.sttp.client" %% "core" % "2.0.0-RC9",
      "com.softwaremill.sttp.client" %% "circe" % "2.0.0-RC9",
      "com.softwaremill.sttp.client" %% "async-http-client-backend-future" % "2.0.0-RC9",
      "com.nimbusds" % "oauth2-oidc-sdk" % "6.23",
      "org.sangria-graphql" %% "sangria" % "2.0.0-M3"
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
