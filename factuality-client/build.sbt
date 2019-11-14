name := "factuality-client"

version := "0.1.0-SNAPSHOT"

organization := "org.clulab"

scalaVersion := "2.12.6"

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

libraryDependencies ++= Seq(
  // The models are not needed for compilation.  Uncomment this line and update
  // the version number in order to run the client with jarred models from maven.
  "org.clulab" %  "factuality-models" % "0.1.0-SNAPSHOT"
)
