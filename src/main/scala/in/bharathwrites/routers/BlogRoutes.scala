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
import in.bharathwrites.actor.BlogActor._

object BlogRoutes {
  val crudURL: String = "blog"
  val getAllURL: String = "blogs"
}

class BlogRoutes(BlogActor: ActorRef)(implicit executionContext: ExecutionContext, system: ActorSystem)
  extends Directives with DefaultJsonFormats with SLF4JLogging {

  import BlogRoutes._

  implicit val timeout = Timeout(5.seconds)

  val route =
    respondWithMediaType(MediaTypes.`application/json`) {
      get {
        path(crudURL / LongNumber) {
          blogId =>
            complete {
              (BlogActor ? Get(blogId)).mapTo[Blog]
            }
        } ~ path(getAllURL) {
          complete {
            (BlogActor ? GetAll).mapTo[List[Blog]]
          }
        }
      } ~ post {
        path(crudURL)
        entity(as[Blog]) {
          blog: Blog =>
            complete {
              (BlogActor ? Create(blog)).mapTo[Blog]
            }
        }
      } ~ put {
        path(crudURL)
        entity(as[Blog]) {
          blog: Blog =>
            complete {
              (BlogActor ? Update(blog.id, blog)).mapTo[Blog]
            }
        }
      } ~ delete {
        path(crudURL / LongNumber) {
          blogId =>
            complete {
              (BlogActor ? Delete(blogId)).mapTo[Blog]
            }
        }
      }
    }
}

