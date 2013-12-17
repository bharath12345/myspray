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
            <link rel="stylesheet" type="text/css" href="/lib/my/common.min.css"></link>
            <link rel='stylesheet' type='text/css' href="/lib/bootstrap/css/bootstrap.min.css"></link>
            <link rel="stylesheet" type='text/css' href="/lib/bootstrap/css/docs.css"></link>
            <link rel='stylesheet' type='text/css' href="/lib/fontawesome/css/font-awesome.min.css"></link>
            <link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/dojo/1.9.1/dijit/themes/claro/claro.css" media="screen"></link>

            <script type="text/javascript" src="/lib/bootstrap/js/bootstrap.min.js"></script>
            <script type="text/javascript" src="/lib/jQuery/jquery-1.10.2.min.js"></script>
            <script type="text/javascript" src="/lib/jquery.toc/js/jquery.tableofcontents.min.js"></script>
            <script type="text/javascript" src="/lib/d3/d3-3.3.2.min.js"></script>
            <script type="text/javascript" src="/lib/jsPlumb/jquery.jsPlumb-1.5.2-min.js"></script>
          </head>
          <body>
            <h1>Jai Shri Ram</h1>
          </body>
        </html>
      }
    } ~
      pathPrefix("lib") {
        get {
          getFromResourceDirectory("lib")
        }
      } ~
      pathPrefix("images") {
        get {
          getFromResourceDirectory("images")
        }
      }
  }
}
