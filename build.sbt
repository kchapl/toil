name := "toil"

version := "1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  ws,
  "com.google.apis" % "google-api-services-sheets" % "v4-rev464-1.22.0",
  "org.webjars" % "bootstrap" % "3.3.7",
  "org.scalacheck" %% "scalacheck" % "1.13.5" % Test
)
