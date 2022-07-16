import sbt._

object Dependencies {
  lazy val circeGenericExtrasVersion = "0.14.2"
  lazy val circeVersion = "0.14.2"
  lazy val sttpVersion = "3.7.0"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.12"
  lazy val wiremock = "com.github.tomakehurst" % "wiremock-jre8" % "2.33.2"
  lazy val circeCore = "io.circe" %% "circe-core" % circeVersion
  lazy val circeParser = "io.circe" %% "circe-parser" % circeVersion
  lazy val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  lazy val circeGenericExtras = "io.circe" %% "circe-generic-extras" % circeGenericExtrasVersion
  lazy val sttp = "com.softwaremill.sttp.client3" %% "core" % sttpVersion
  lazy val sttpCirce = "com.softwaremill.sttp.client3" %% "circe" % sttpVersion
  lazy val oauth2 = "com.nimbusds" % "oauth2-oidc-sdk" % "9.38.1"
  lazy val sangria = "org.sangria-graphql" %% "sangria" % "3.0.1"
}
