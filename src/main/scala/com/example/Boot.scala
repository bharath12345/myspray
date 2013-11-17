package com.example

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import util.Properties

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[MyServiceActor], "demo-service")

  val port = Properties.envOrElse("PORT", "8080").toInt
  println("Starting on port:"+port)

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = "localhost", port = port)
}