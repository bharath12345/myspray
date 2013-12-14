package in.bharathwrites.routers

import akka.actor._
import spray.routing.HttpService

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class StaticRoutesActor extends Actor with StaticRoutes {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing,
  // timeout handling or alternative handler registration
  def receive = runRoute(demoRoute)
}


// this trait defines our service behavior independently from the service actor
trait StaticRoutes extends HttpService {

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher

  val demoRoute = get {
    path("/") {
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
