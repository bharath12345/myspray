package in.bharathwrites.boot

import akka.actor.Props
import spray.routing.RouteConcatenation
import in.bharathwrites.services.{BlogService, RoutedHttpService}

/**
 * The REST API layer. It exposes the REST services, but does not provide any
 * web server interface.<br/>
 * Notice that it requires to be mixed in with ``core.CoreActors``, which provides access
 * to the top-level actors that make up the system.
 */
trait Api extends RouteConcatenation {
  this: CoreActors with Core =>

  private implicit val _ = system.dispatcher

  // append all routes using ~
  val routes = new BlogService(blogDAOActor).route

  val rootService = system.actorOf(Props(new RoutedHttpService(routes)))

}