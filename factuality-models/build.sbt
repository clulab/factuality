import ReleaseTransformations._

name := "factuality-models"

organization := "org.clulab"

scalaVersion := "2.12.6"

publishArtifact in (Compile, packageBin) := true // Do include the resources.

publishArtifact in (Compile, packageDoc) := false // There is no documentation.

publishArtifact in (Compile, packageSrc) := false // There is no source code.

publishArtifact in (Test, packageBin) := false

// These are the steps to be performed during release.
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
//  inquireVersions,
  runClean,
  runTest,
//  setReleaseVersion,
//  commitReleaseVersion,
//  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _)) //,
//  setNextVersion,
//  commitNextVersion,
    // File upload is unreliable.  Check manually.
//  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)) //,
//  pushChanges
)

// Publish to a maven repo.
publishMavenStyle := true

// Don't include scala version in artifact; we don't need it.
crossPaths := false

// This is the standard maven repository.
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

// Let’s remove any repositories for optional dependencies in our artifact.
pomIncludeRepository := { _ => false }

// mandatory stuff to add to the pom for publishing
pomExtra := (
  <url>https://github.com/clulab/factuality-models</url>
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/clulab/factuality-models</url>
    <connection>https://github.com/clulab/factuality-models</connection>
  </scm>
  <developers>
    <developer>
      <id>mihai.surdeanu</id>
      <name>Mihai Surdeanu</name>
      <email>mihai@surdeanu.info</email>
    </developer>
  </developers>)
