name := "toil"

version := "1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  ws,
  "com.google.apis" % "google-api-services-sheets" % "v4-rev40-1.22.0",
  "org.webjars" % "bootstrap" % "3.3.7"
)
