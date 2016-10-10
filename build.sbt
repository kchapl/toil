name := "toil"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.pdfbox" % "pdfbox" % "2.0.3"
)
