import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.0-M2"
  lazy val wiremock = "com.github.tomakehurst" % "wiremock-jre8" % "2.26.0"
  lazy val circeCore = "io.circe" %% "circe-core" % "0.13.0"
  lazy val circeParser = "io.circe" %% "circe-parser" % "0.13.0"
  lazy val circeGeneric = "io.circe" %% "circe-generic" % "0.13.0"
  lazy val circeGenericExtras = "io.circe" %% "circe-generic-extras" % "0.13.0"
  lazy val sttp = "com.softwaremill.sttp.client" %% "core" % "2.2.4"
  lazy val sttpCirce = "com.softwaremill.sttp.client" %% "circe" % "2.2.4"
  lazy val sttpAsyncClient = "com.softwaremill.sttp.client" %% "async-http-client-backend-future" % "2.2.4"
  lazy val oauth2 = "com.nimbusds" % "oauth2-oidc-sdk" % "6.23"
  lazy val sangria = "org.sangria-graphql" %% "sangria" % "2.0.0-M3"
}
