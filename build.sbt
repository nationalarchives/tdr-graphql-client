import Dependencies._
import sbt.url
import sbtrelease.ReleaseStateTransformations.{checkSnapshotDependencies, commitNextVersion, commitReleaseVersion, inquireVersions, pushChanges, runClean, runTest, setNextVersion, setReleaseVersion, tagRelease}

ThisBuild / version := (version in ThisBuild).value
ThisBuild / organization     := "uk.gov.nationalarchives"
ThisBuild / organizationName := "National Archives"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/nationalarchives/tdr-graphql-client-data"),
    "git@github.com:nationalarchives/tdr-graphql-client.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "SP",
    name  = "Sam Palmer",
    email = "sam.palmer@nationalarchives.gov.uk",
    url   = url("http://tdr-transfer-integration.nationalarchives.gov.uk")
  )
)

ThisBuild / description := "A simple graphql client which uses auto generated sangria classes"
ThisBuild / licenses := List("MIT" -> new URL("https://choosealicense.com/licenses/mit/"))
ThisBuild / homepage := Some(url("https://github.com/nationalarchives/tdr-graphql-client"))

scalaVersion := "2.13.3"

s3acl := None
s3sse := true
ThisBuild / publishMavenStyle := true

ThisBuild / publishTo := {
  val prefix = if (isSnapshot.value) "snapshots" else "releases"
  Some(s3resolver.value(s"My ${prefix} S3 bucket", s3(s"tdr-$prefix-mgmt")))
}

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

releaseVcsSign := true

lazy val root = (project in file("."))
  .settings(
    name := "tdr-graphql-client",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      wiremock,
      circeCore,
      circeParser,
      circeGeneric,
      circeGenericExtras,
      sttp,
      sttpCirce,
      sttpAsyncClient,
      oauth2,
      sangria
    )
  )
