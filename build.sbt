name := "factuality"

organization := "org.clulab"

scalaVersion := "2.12.6"

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
