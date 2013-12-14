package in.bharathwrites.services

import akka.event.slf4j.SLF4JLogging
import akka.actor.{ActorSystem, ActorRef}
import akka.util.Timeout
import spray.routing._
import spray.http._
import scala.concurrent.duration._
import in.bharathwrites.domain._
import in.bharathwrites.dao._
import scala.concurrent.ExecutionContext
import akka.pattern.ask
import spray.httpx.unmarshalling.{MalformedContent, FromStringDeserializer}
import spray.httpx.marshalling.BasicMarshallers.StringMarshaller
import java.util.Date
import java.text.{SimpleDateFormat, ParseException}
import net.liftweb.json.{DateFormat, Formats}
import in.bharathwrites.dao.BlogDAOActor.{ResponseBlog, Get}

class BlogService(BlogDAOActor: ActorRef)(implicit executionContext: ExecutionContext, system: ActorSystem)
  extends Directives with DefaultJsonFormats with SLF4JLogging {

  implicit val timeout = Timeout(2.seconds)

  implicit val string2Date = new FromStringDeserializer[Option[Date]] {
    def apply(value: String) = {
      val sdf = new SimpleDateFormat("yyyy-MM-dd")
      try Right(Some(sdf.parse(value)))
      catch {
        case e: ParseException => {
          Left(MalformedContent("'%s' is not a valid Date value" format (value), e))
        }
      }
    }
  }

  implicit def date2String(date: Option[Date]): String =  "xyz"

  /*implicit val liftJsonFormats = new Formats {
    val dateFormat = new DateFormat {
      val sdf = new SimpleDateFormat("yyyy-MM-dd")

      def parse(s: String): Option[Date] = try {
        Some(sdf.parse(s))
      } catch {
        case e: Exception => None
      }

      def format(d: Date): String = sdf.format(d)
    }
  }*/

  implicit val blogFormat = jsonFormat4(Blog)

  val route =
    /*pathSingleSlash {
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
    } ~*/
      pathPrefix("css") {
        get {
          getFromResourceDirectory("css")
        }
      } ~
      pathPrefix("js") {
        get {
          getFromResourceDirectory("js")
        }
      } ~
      path("blog" / LongNumber) {
        blogId =>
          respondWithMediaType(MediaTypes.`application/json`) {
            get {
              //request => BlogDaoActor ! Get(blogId)
              //complete { (BlogDaoActor ? Get(blogId)).mapTo[ResponseBlog] }
              complete {
                (BlogDAOActor ? Get(blogId)).mapTo[ResponseBlog]
              }
            }
          }
      }
}