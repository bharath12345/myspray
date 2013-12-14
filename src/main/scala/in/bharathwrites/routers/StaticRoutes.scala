package in.bharathwrites.routers

import spray.routing.{Directives, HttpService}
import akka.actor.{ActorSystem, ActorRef}
import akka.event.slf4j.SLF4JLogging

class StaticRoutes(implicit system: ActorSystem) extends Directives with SLF4JLogging {

  val staticRoutes = get {
    pathSingleSlash {
      complete {
        <html>
          <head>
            <script type="text/javascript" src="css/test.css"></script>
          </head>
          <body>
            <h1>Jai Shri Ram</h1>
          </body>
        </html>
      }
    } ~
      pathPrefix("css") {
        get {
          getFromResourceDirectory("css")
        }
      } ~
      pathPrefix("js") {
        get {
          getFromResourceDirectory("js")
        }
      }
  }
}
