name := "toil"

version := "1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  guice,
  ws,
  "com.google.apis" % "google-api-services-sheets" % "v4-rev478-1.22.0",
  "org.webjars"     % "jquery"                     % "3.2.1",
  "org.webjars"     % "bootstrap"                  % "3.3.7-1",
  "org.webjars"     % "datatables"                 % "1.10.15",
  "org.webjars"     % "chartjs"                    % "2.4.0",
  "org.scalacheck"  %% "scalacheck"                % "1.13.5" % Test
)
