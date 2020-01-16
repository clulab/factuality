name := "factuality-developer"

version := "0.1.0-SNAPSHOT"

organization := "org.clulab"

scalaVersion := "2.12.6"

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

resolvers += "Artifactory" at "http://artifactory.cs.arizona.edu:8081/artifactory/sbt-release"

libraryDependencies ++= Seq(
  "org.clulab" %% "processors-main" % "7.5.1", // Word2Vec
  "org.clulab" % "glove-42b-300d" % "0.1.0"
)
