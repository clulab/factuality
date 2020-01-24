import ReleaseTransformations._

name := "factuality"

organization := "org.clulab"

// This is presently not compatible with Scala 2.11.
//crossScalaVersions := Seq("2.11.11", "2.12.6")

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

libraryDependencies ++= Seq(
  "org.clulab" %% "fatdynet" % "0.2.4", // Zip functionality of 0.2.3 or higher is required for factuality-client.

  // logging
  "com.typesafe.scala-logging" %% "scala-logging"   % "3.7.2",
  "ch.qos.logback"              % "logback-classic" % "1.0.10",
  "org.slf4j"                   % "slf4j-api"       % "1.7.10"
)

lazy val core = project in file(".")

lazy val `factuality-developer` = project
  .dependsOn(core)

lazy val `factuality-models` = project

lazy val `factuality-client` = project

publishMavenStyle := true

// the standard maven repository
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

// let’s remove any repositories for optional dependencies in our artifact
pomIncludeRepository := { _ => false }

scmInfo := Some(
  ScmInfo(
    url("https://github.com/clulab/factuality"),
    "scm:git:https://github.com/clulab/factuality.git"
  )
)

pomExtra :=
    <url>https://github.com/clulab/factuality</url>
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
  releaseStepCommandAndRemaining("+publishLocal"),
  setNextVersion,
  commitNextVersion//,
//  releaseStepCommandAndRemaining("sonatypeReleaseAll"),
//  pushChanges
)
