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
import in.bharathwrites.actor.BlogActor.{ResponseBlog, Get}

class BlogRoutes(BlogActor: ActorRef)(implicit executionContext: ExecutionContext, system: ActorSystem)
  extends Directives with DefaultJsonFormats with SLF4JLogging {

  implicit val timeout = Timeout(2.seconds)

  implicit val blogFormat = jsonFormat4(Blog)

  val route = path("blog" / LongNumber) {
    blogId =>
      respondWithMediaType(MediaTypes.`application/json`) {
        get {
          //request => BlogDaoActor ! Get(blogId)
          //complete { (BlogDaoActor ? Get(blogId)).mapTo[ResponseBlog] }
          complete {
            (BlogActor ? Get(blogId)).mapTo[Blog] // Adding Failure causes Marshalling problems... but you should add it at some point of time
          }
        }
      }
  }
}