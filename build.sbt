name := "toil"

version := "1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  ws,
  "com.google.apis" % "google-api-services-sheets" % "v4-rev491-1.23.0" excludeAll (
    ExclusionRule(organization = "org.apache.httpcomponents", name = "httpclient"),
    ExclusionRule(organization = "com.fasterxml.jackson.core", name = "jackson-core")
  ),
  "org.apache.httpcomponents"  % "httpclient"   % "4.5.3" % Runtime,
  "com.fasterxml.jackson.core" % "jackson-core" % "2.9.2" % Runtime,
  "org.webjars"                % "jquery"       % "3.2.1",
  "org.webjars"                % "bootstrap"    % "3.3.7-1",
  "org.webjars"                % "datatables"   % "1.10.15",
  "org.webjars"                % "chartjs"      % "2.4.0",
  "org.scalacheck"             %% "scalacheck"  % "1.13.5" % Test
)
