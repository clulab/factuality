name := "factuality"

version := "1.0-SNAPSHOT"

organization := "org.clulab"

scalaVersion := "2.12.6"

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

libraryDependencies ++= Seq(
  "org.clulab" %% "processors-main" % "7.5.1",
  "org.clulab" %% "processors-corenlp" % "7.5.1",
  "org.clulab" %% "processors-modelscorenlp" % "7.5.1",
  "org.clulab" %% "processors-modelsmain" % "7.5.1",
  "org.clulab" %% "fatdynet" % "0.2.0",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)
