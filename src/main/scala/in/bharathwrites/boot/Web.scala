package in.bharathwrites.boot

import akka.io.IO
import spray.can.Http

/**
 * Provides the web server (spray-can) for the REST api in ``Api``, using the actor system
 * defined in ``Core``.
 *
 * You may sometimes wish to construct separate ``ActorSystem`` for the web server machinery.
 * However, for this simple application, we shall use the same ``ActorSystem`` for the
 * entire application.
 *
 * Benefits of separate ``ActorSystem`` include the ability to use completely different
 * configuration, especially when it comes to the threading model.
 */
trait Web {
  this: Api with CoreActors with Core =>

  println(s"Starting on host: $serviceHost, port: $servicePort")
  println(s"Using DB host: $dbHost, port: $dbPort, name: $dbName, user: $dbUser, pass: $dbPassword")

  // start a new HTTP server with our service actor as the handler
  IO(Http) ! Http.Bind(rootService, interface = serviceHost, port = servicePort)

}
