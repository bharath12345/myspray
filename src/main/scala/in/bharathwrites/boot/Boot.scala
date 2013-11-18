package in.bharathwrites.boot

import akka.actor.{ActorSystem, Props}
import akka.event.slf4j.SLF4JLogging
import akka.io.IO
import spray.can.Http
import in.bharathwrites.config.Configuration
import akka.actor.actorRef2Scala
import in.bharathwrites.rest.BlogServiceActor

object Boot extends App with Configuration with SLF4JLogging {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[BlogServiceActor], "blog-service")

  println("Starting on host:" + serviceHost + " port:" + servicePort)

  // start a new HTTP server with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = serviceHost, port = servicePort)
}