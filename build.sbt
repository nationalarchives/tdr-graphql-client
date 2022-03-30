import Dependencies._
import sbt.url
import sbtrelease.ReleaseStateTransformations.{checkSnapshotDependencies, commitNextVersion, commitReleaseVersion, inquireVersions, pushChanges, runClean, runTest, setNextVersion, setReleaseVersion, tagRelease}

ThisBuild / version := (version in ThisBuild).value
ThisBuild / organization     := "uk.gov.nationalarchives"
ThisBuild / organizationName := "National Archives"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/nationalarchives/tdr-graphql-client"),
    "git@github.com:nationalarchives/tdr-graphql-client.git"
  )
)
developers := List(
  Developer(
    id    = "tna-digital-archiving-jenkins",
    name  = "TNA Digital Archiving",
    email = "digitalpreservation@nationalarchives.gov.uk",
    url   = url("https://github.com/nationalarchives/tdr-generated-grapqhl")
  )
)

ThisBuild / description := "A simple graphql client which uses auto generated sangria classes"
ThisBuild / licenses := List("MIT" -> new URL("https://choosealicense.com/licenses/mit/"))
ThisBuild / homepage := Some(url("https://github.com/nationalarchives/tdr-graphql-client"))

scalaVersion := "2.13.8"

useGpgPinentry := true
publishTo := sonatypePublishToBundle.value
publishMavenStyle := true

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommand("publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

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
