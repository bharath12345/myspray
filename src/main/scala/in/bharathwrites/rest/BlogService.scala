package in.bharathwrites.rest

import akka.actor.Actor
import akka.event.slf4j.SLF4JLogging
import spray.routing._
import spray.http._
import spray.httpx.unmarshalling._
import spray.can.Http
import spray.http._
import HttpMethods._
import java.text.{ ParseException, SimpleDateFormat }
import java.util.Date
import net.liftweb.json.Serialization._
import net.liftweb.json.{ DateFormat, Formats }
import scala.Some
import in.bharathwrites.domain._
import in.bharathwrites.dao._
import akka.actor.ActorRef
import akka.event.LoggingReceive
import in.bharathwrites.dao.BlogDAO._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class BlogServiceActor extends Actor with BlogService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def sprayReceive = LoggingReceive { runRoute(myRoutes) }
  
  def customReceive: Receive = LoggingReceive {
    case GetBlog(blog: Either[Failure, Blog]) => {
      log.debug("back")
    }
  }
  
  def receive = customReceive orElse sprayReceive
  
}

// this trait defines our service behavior independently from the service actor
trait BlogService extends HttpService with SLF4JLogging with RequestHandler {

  def createDAO: ActorRef = actorRefFactory.actorOf(BlogDAO.props, "blogdao")

  val BlogDaoActor = createDAO

  implicit val executionContext = actorRefFactory.dispatcher

  implicit val string2Date = new FromStringDeserializer[Date] {
    def apply(value: String) = {
      val sdf = new SimpleDateFormat("yyyy-MM-dd")
      try Right(sdf.parse(value))
      catch {
        case e: ParseException => {
          Left(MalformedContent("'%s' is not a valid Date value" format (value), e))
        }
      }
    }
  }

  val myRoutes =
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
      path("blogs") {
        respondWithMediaType(MediaTypes.`application/json`) {
          get {
            parameters('title.as[String] ?, 'content.as[String] ?, 'date.as[Date] ?).as(BlogSearchParameters) {
              searchParameters: BlogSearchParameters =>
                {
                  request => BlogDaoActor ! Search(searchParameters)
                }
            }
          }
        }
      } ~
      path("blog" / LongNumber) {
        blogId =>
          respondWithMediaType(MediaTypes.`application/json`) {
            get {
              request => BlogDaoActor ! Get(blogId)
            }
          }
      }
}