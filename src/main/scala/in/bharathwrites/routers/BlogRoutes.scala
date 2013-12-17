package in.bharathwrites.routers

import akka.event.slf4j.SLF4JLogging
import akka.actor.{ActorSystem, ActorRef}
import akka.util.Timeout
import spray.routing._
import spray.http._
import scala.concurrent.duration._
import in.bharathwrites.domain._
import scala.concurrent.ExecutionContext
import akka.pattern.ask
import spray.httpx.unmarshalling.{MalformedContent, FromStringDeserializer}
import in.bharathwrites.actor.BlogActor.{GetAll, Get, Create}
import spray.httpx.unmarshalling._

class BlogRoutes(BlogActor: ActorRef)(implicit executionContext: ExecutionContext, system: ActorSystem)
  extends Directives with DefaultJsonFormats with SLF4JLogging {

  implicit val timeout = Timeout(5.seconds)

  val route =
    respondWithMediaType(MediaTypes.`application/json`) {
      get {
        path("blog" / LongNumber) {
          blogId =>
            complete {
              (BlogActor ? Get(blogId)).mapTo[Blog]
            }
        } ~ path("blogs") {
          complete {
            (BlogActor ? GetAll).mapTo[List[Blog]]
          }
        }
      } ~ post {
        path("blog")
        entity(as[Blog]) {
          blog: Blog =>
            complete {
              (BlogActor ? Create(blog)).mapTo[Blog]
            }
        }
      }
    }
}