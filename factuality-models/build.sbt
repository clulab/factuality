import ReleaseTransformations._

name := "factuality-models"

organization := "org.clulab"

scalaVersion := "2.12.6"

crossPaths := false // This is a resource only and is independent of Scala version.

//lazy val core = project in file(".")

// Put these files next to the model, in part so they don't conflict with other dependencies.
mappings in (Compile, packageBin) ++= Seq(
  file("./README.md") -> "org/clulab/factuality/models/README.md",
  file("./CHANGES.md") -> "org/clulab/factuality/models/CHANGES.md"
)

publishArtifact in (Compile, packageBin) := true // Do include the resources.

publishArtifact in (Compile, packageDoc) := false // There is no documentation.

publishArtifact in (Compile, packageSrc) := false // There is no source code.

publishArtifact in (Test, packageBin) := false

publishMavenStyle := true

publishTo := {
  val artifactory = "http://artifactory.cs.arizona.edu:8081/artifactory/"
  val repository = "sbt-release-local"
  val details =
      if (isSnapshot.value) ";build.timestamp=" + new java.util.Date().getTime
      else ""
  val location = artifactory + repository + details

  Some("Artifactory Realm" at location)
}

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
// credentials += Credentials("Artifactory Realm", "<host>", "<user>", "<password>")
// The above credentials are recorded in ~/.sbt/.credentials as such:
// realm=Artifactory Realm
// host=<host>
// user=<user>
// password=<password>

// Let’s remove any repositories for optional dependencies in our artifact.
pomIncludeRepository := { _ => false }

scmInfo := Some(
  ScmInfo(
    url("https://github.com/clulab/factuality-models"),
    "scm:git:https://github.com/clulab/factuality-models.git"
  )
)

// This must be added to add to the pom for publishing.
pomExtra :=
  <url>https://github.com/clulab/factuality-models</url>
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <developers>
    <developer>
      <id>mihai.surdeanu</id>
      <name>Mihai Surdeanu</name>
      <email>mihai@surdeanu.info</email>
    </developer>
  </developers>

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
//  releaseStepCommandAndRemaining("+publishSigned"),
//  releaseStepCommandAndRemaining("+publish"),
  releaseStepCommandAndRemaining("+publishLocal"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
