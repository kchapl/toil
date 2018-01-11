name := "toil"

version := "1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

scalacOptions += "-Ypartial-unification"

scalafmtOnCompile := true

libraryDependencies ++= Seq(
  ws,
  "com.google.apis"   % "google-api-services-sheets" % "v4-rev496-1.23.0",
  "org.typelevel"     %% "cats-core"                 % "1.0.1",
  "org.webjars"       % "jquery"                     % "3.2.1",
  "org.webjars"       % "bootstrap"                  % "4.0.0-beta.3",
  "org.webjars"       % "datatables"                 % "1.10.16",
  "org.webjars"       % "chartjs"                    % "2.7.0",
  "org.webjars.bower" % "open-iconic"                % "1.1.1",
  "org.scalacheck"    %% "scalacheck"                % "1.13.5" % Test
)
