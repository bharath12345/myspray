import com.typesafe.sbt.SbtStartScript

import AssemblyKeys._

seq(SbtStartScript.startScriptForClassesSettings: _*)

name := "Bharathz RESTful Blog"

version := "1.0"

organization  := "in.bharathwrites"

version       := "0.1"

scalaVersion  := "2.10.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/"
)

libraryDependencies ++= {
  val akkaV = "2.2.3"
  val sprayV = "1.2-RC4"
  Seq(
    "io.spray"            %   "spray-can"       % sprayV,
    "io.spray"            %   "spray-routing"   % sprayV,
    "io.spray"            %   "spray-testkit"   % sprayV,
    "com.typesafe.akka"   %%  "akka-actor"      % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"    % akkaV,
    "com.typesafe.akka"   %%  "akka-slf4j"      % akkaV,
    "org.specs2"          %%  "specs2"          % "2.2.3" % "test",    
    "net.liftweb"         %%  "lift-json"       % "2.5.1",
    "com.typesafe.slick"  %%  "slick"           % "1.0.1",
    "org.postgresql"      %   "postgresql"      % "9.3-1100-jdbc4",
    "ch.qos.logback"      %   "logback-classic" % "1.0.13"
  )
}

seq(Revolver.settings: _*)

assemblySettings

