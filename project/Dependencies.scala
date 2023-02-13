import sbt._

object Dependencies {
  lazy val circeGenericExtrasVersion = "0.14.3"
  lazy val circeVersion = "0.14.4"
  lazy val sttpVersion = "3.8.11"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.15"
  lazy val wiremock = "com.github.tomakehurst" % "wiremock-jre8" % "2.35.0"
  lazy val circeCore = "io.circe" %% "circe-core" % circeVersion
  lazy val circeParser = "io.circe" %% "circe-parser" % circeVersion
  lazy val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  lazy val circeGenericExtras = "io.circe" %% "circe-generic-extras" % circeGenericExtrasVersion
  lazy val sttp = "com.softwaremill.sttp.client3" %% "core" % sttpVersion
  lazy val sttpCirce = "com.softwaremill.sttp.client3" %% "circe" % sttpVersion
  lazy val oauth2 = "com.nimbusds" % "oauth2-oidc-sdk" % "10.5.1"
  lazy val sangria = "org.sangria-graphql" %% "sangria" % "3.5.2"
}
