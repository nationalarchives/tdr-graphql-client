import sbt._

object Dependencies {
  lazy val circeVersion = "0.14.1"
  lazy val sttpVersion = "3.5.1"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.12-RC2"
  lazy val wiremock = "com.github.tomakehurst" % "wiremock-jre8" % "2.32.0"
  lazy val circeCore = "io.circe" %% "circe-core" % circeVersion
  lazy val circeParser = "io.circe" %% "circe-parser" % circeVersion
  lazy val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  lazy val circeGenericExtras = "io.circe" %% "circe-generic-extras" % circeVersion
  lazy val sttp = "com.softwaremill.sttp.client3" %% "core" % sttpVersion
  lazy val sttpCirce = "com.softwaremill.sttp.client3" %% "circe" % sttpVersion
  lazy val sttpAsyncClient = "com.softwaremill.sttp.client3" %% "async-http-client-backend-future" % sttpVersion
  lazy val oauth2 = "com.nimbusds" % "oauth2-oidc-sdk" % "9.32"
  lazy val sangria = "org.sangria-graphql" %% "sangria" % "2.1.6"
}
