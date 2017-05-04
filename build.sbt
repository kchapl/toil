name := "toil"

version := "1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  ws,
  "com.google.apis" % "google-api-services-sheets" % "v4-rev464-1.22.0",
  "org.webjars"     % "jquery"                     % "3.2.0",
  "org.webjars.npm" % "types__jquery.datatables"   % "1.10.33",
  "org.webjars"     % "bootstrap"                  % "3.3.7-1",
  "org.webjars"     % "datatables-bootstrap"       % "2-20120202-2",
  "org.webjars"     % "chartjs"                    % "26962ce-1",
  "org.scalacheck"  %% "scalacheck"                % "1.13.5" % Test
)
