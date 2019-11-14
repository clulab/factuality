name := "factuality-client"

version := "0.1.0-SNAPSHOT"

organization := "org.clulab"

scalaVersion := "2.12.6"

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

libraryDependencies ++= Seq(
  // This must depend on "fatdynet" % "0.2.3" or higher for zip functionality.
  "org.clulab" %% "factuality" % "1.0-SNAPSHOT" //,
//  "org.clulab" %  "factuality-models" % "0.1.0-SNAPSHOT"
)
