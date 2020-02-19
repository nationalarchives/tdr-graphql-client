import Dependencies._
import sbt.url
import sbtrelease.ReleaseStateTransformations.{checkSnapshotDependencies, commitNextVersion, commitReleaseVersion, inquireVersions, pushChanges, runClean, runTest, setNextVersion, setReleaseVersion, tagRelease}

lazy val supportedScalaVersions = List("2.13.0", "2.12.8")
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

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := sonatypePublishToBundle.value
ThisBuild / publishMavenStyle := true

useGpgPinentry := true

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

releasePublishArtifactsAction := PgpKeys.publishSigned.value

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
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
      sttpAsyncClient,
      oauth2,
      sangria
    ),
    crossScalaVersions := supportedScalaVersions
  )