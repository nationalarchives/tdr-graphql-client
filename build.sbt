import Dependencies._
import sbt.url
import sbtrelease.ReleaseStateTransformations.{checkSnapshotDependencies, commitNextVersion, commitReleaseVersion, inquireVersions, pushChanges, runClean, runTest, setNextVersion, setReleaseVersion, tagRelease}

ThisBuild / version := (ThisBuild / version).value
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
    id    = "tna-da-bot",
    name  = "TNA Digital Archiving",
    email = "s-GitHubDABot@nationalarchives.gov.uk",
    url   = url("https://github.com/nationalarchives/tdr-generated-grapqhl")
  )
)

ThisBuild / description := "A simple graphql client which uses auto generated sangria classes"
ThisBuild / licenses := List("MIT" -> new URL("https://choosealicense.com/licenses/mit/"))
ThisBuild / homepage := Some(url("https://github.com/nationalarchives/tdr-graphql-client"))

scalaVersion := "2.13.16"

useGpgPinentry := true
publishTo := {
  val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
  if (isSnapshot.value) Some("central-snapshots" at centralSnapshots)
  else localStaging.value
}
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
  releaseStepCommand("sonatypeRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)

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
      oauth2,
      sangria
    )
  )
