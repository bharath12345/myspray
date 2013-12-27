import com.typesafe.sbt.SbtStartScript

seq(SbtStartScript.startScriptForClassesSettings: _*)

name := "BharathzBlog"

version := "1.0"

organization  := "in.bharathwrites"

version       := "0.1"

scalaVersion  := "2.10.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/",
  "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= {
  val akkaV = "2.2.3"
  val sprayV = "1.2.0"
  Seq(
    "io.spray"               %   "spray-io"           % sprayV,
    "io.spray"               %   "spray-http"         % sprayV,
    "io.spray"               %   "spray-httpx"        % sprayV,
    "io.spray"               %%  "spray-json"         % "1.2.5",
    "io.spray"               %   "spray-can"          % sprayV,
    "io.spray"               %   "spray-routing"      % sprayV,
    "io.spray"               %   "spray-testkit"      % sprayV,
    "com.typesafe.akka"      %%  "akka-actor"         % akkaV,
    "com.typesafe.akka"      %%  "akka-testkit"       % akkaV,
    "com.typesafe.akka"      %%  "akka-slf4j"         % akkaV,
    "com.typesafe.slick"     %%  "slick"              % "2.0.0-M3",
    "org.specs2"             %%  "specs2"             % "2.2.3" % "test",
    "org.postgresql"         %   "postgresql"         % "9.3-1100-jdbc4",
    "ch.qos.logback"         %   "logback-classic"    % "1.0.13",
    "net.liftweb"            %%  "lift-json"          % "2.5.1",
    "joda-time"              %   "joda-time"          % "2.3",
    "com.github.tototoshi"   %%  "slick-joda-mapper"  % "1.0.0-SNAPSHOT",
    "org.pegdown"            %   "pegdown"            % "1.4.2",
    "org.parboiled"          %   "parboiled-java"     % "1.1.6"
  )
}

