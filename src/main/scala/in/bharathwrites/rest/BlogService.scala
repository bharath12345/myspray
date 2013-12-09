package in.bharathwrites.rest

import akka.actor.Actor
import akka.event.slf4j.SLF4JLogging

import spray.routing._
import spray.http._
import spray.httpx.unmarshalling._
import spray.can.Http
import spray.http._

import HttpMethods._
import StatusCodes._

import java.text.{ ParseException, SimpleDateFormat }
import java.util.Date

import net.liftweb.json.Serialization._
import net.liftweb.json.{ DateFormat, Formats }

import scala.Some

import in.bharathwrites.domain._
import in.bharathwrites.dao._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class BlogServiceActor extends Actor with BlogService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling

  def receive = runRoute(myRoutes)

  /*def receive = {
    
    case HttpRequest(GET, Uri.Path("/"), _, _, _) => sender ! index
    
    case _: Http.Connected => sender ! Http.Register(self)
    
    case _: HttpRequest => runRoute(myRoutes)
  }
  
  lazy val index = HttpResponse(
      entity = HttpEntity(MediaTypes.`text/html`,
        <html>
          <body>
            <h1>Tiny <i>spray-can</i> benchmark server</h1>
            <p>Defined resources:</p>
            <ul>
              <li><a href="/ping">/ping</a></li>
              <li><a href="/fast-ping">/fast-ping</a></li>
              <li><a href="/json">/json</a></li>
              <li><a href="/fast-json">/fast-json</a></li>
              <li><a href="/stop">/stop</a></li>
            </ul>
          </body>
        </html>.toString()
      )
    )*/
}

// this trait defines our service behavior independently from the service actor
trait BlogService extends HttpService with SLF4JLogging {

  val blogService = new BlogDAO

  implicit val executionContext = actorRefFactory.dispatcher

  implicit val liftJsonFormats = new Formats {
    val dateFormat = new DateFormat {
      val sdf = new SimpleDateFormat("yyyy-MM-dd")

      def parse(s: String): Option[Date] = try {
        Some(sdf.parse(s))
      } catch {
        case e: Exception => None
      }

      def format(d: Date): String = sdf.format(d)
    }
  }

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

  implicit val customRejectionHandler = RejectionHandler {
    case rejections => mapHttpResponse {
      response =>
        response.withEntity(HttpEntity(ContentType(MediaTypes.`application/json`),
          write(Map("error" -> response.entity.asString))))
    } {
      RejectionHandler.Default(rejections)
    }
  }

  val myRoutes =
    get {
      pathSingleSlash {
        complete {
          <html>
            <body>
              <h1>Tiny <i>spray-can</i> benchmark server</h1>
              <p>Defined resources:</p>
              <ul>
                <li><a href="/ping">/ping</a></li>
                <li><a href="/fast-ping">/fast-ping</a></li>
                <li><a href="/json">/json</a></li>
                <li><a href="/fast-json">/fast-json</a></li>
                <li><a href="/stop">/stop</a></li>
              </ul>
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
                  ctx: RequestContext =>
                    handleRequest(ctx) {
                      log.debug("Searching for blogs with parameters: %s".format(searchParameters))
                      blogService.search(searchParameters)
                    }
                }
            }
          }
        }
      } ~
      path("blog" / LongNumber) {
        blogId =>
          respondWithMediaType(MediaTypes.`application/json`) {
            delete {
              ctx: RequestContext =>
                handleRequest(ctx) {
                  log.debug("Deleting blog with id %d".format(blogId))
                  blogService.delete(blogId)
                }
            } ~
              get {
                ctx: RequestContext =>
                  handleRequest(ctx) {
                    log.debug("Retrieving blog with id %d".format(blogId))
                    blogService.get(blogId)
                  }
              }
          }
      }

  /**
   * Handles an incoming request and create valid response for it.
   *
   * @param ctx         request context
   * @param successCode HTTP Status code for success
   * @param action      action to perform
   */
  protected def handleRequest(ctx: RequestContext, successCode: StatusCode = StatusCodes.OK)(action: => Either[Failure, _]) {
    action match {
      case Right(result: Object) =>
        ctx.complete(successCode, write(result))
      case Left(error: Failure) =>
        ctx.complete(error.getStatusCode, net.liftweb.json.Serialization.write(Map("error" -> error.message)))
      case _ =>
        ctx.complete(StatusCodes.InternalServerError)
    }
  }

}