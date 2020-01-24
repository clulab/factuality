name := "factuality-client"

organization := "org.clulab"

scalaVersion := "2.12.6"

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

resolvers += "Artifactory" at "http://artifactory.cs.arizona.edu:8081/artifactory/sbt-release"

libraryDependencies ++= Seq(
  "org.clulab" %% "factuality" % "1.0.0",
  "org.clulab" % "factuality-models" % "0.2.0"
)
