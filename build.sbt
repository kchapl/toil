name := "toil"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  ws,
  "org.webjars" % "bootstrap" % "3.3.7",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)
