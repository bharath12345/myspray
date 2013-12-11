package in.bharathwrites.services

import akka.event.slf4j.SLF4JLogging
import akka.actor.ActorRef
import akka.util.Timeout
import spray.routing._
import spray.http._
import scala.concurrent.duration._
import in.bharathwrites.domain._
import in.bharathwrites.dao._
import in.bharathwrites.dao.BlogDAOActor._
import scala.concurrent.ExecutionContext
import akka.pattern.ask

class BlogService(registration: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with DefaultJsonFormats with SLF4JLogging {

  implicit val timeout = Timeout(2.seconds)
  implicit val blogFormat = jsonFormat4(Blog)

  val route =
    pathPrefix("css") { get { getFromResourceDirectory("css") } } ~
      pathPrefix("js") { get { getFromResourceDirectory("js") } } ~ 
      get {
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
        }
      } ~
      path("blog" / LongNumber) {
        blogId =>
          respondWithMediaType(MediaTypes.`application/json`) {
            get {
              //request => BlogDaoActor ! Get(blogId)
              //complete { (BlogDaoActor ? Get(blogId)).mapTo[ResponseBlog] }
                handleWith { ctx: RequestContext => (BlogDAOActor ? Get(blogId)).mapTo[ResponseBlog] }
            }
          }
      }
}