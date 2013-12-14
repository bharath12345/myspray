package in.bharathwrites.boot

import akka.actor.{Props, ActorSystem}
import in.bharathwrites.config.Configuration
import in.bharathwrites.actor.BlogActor

/**
 * Core is type containing the ``system: ActorSystem`` member. This enables us to use it in our
 * apps as well as in our tests.
 */
trait Core {

  implicit def system: ActorSystem

}

/**
 * This trait implements ``Core`` by starting the required ``ActorSystem`` and registering the
 * termination handler to stop the system when the JVM exits.
 */
trait BootedCore extends Core {

  /**
   * Construct the ActorSystem we will use in our application
   */
  implicit lazy val system = ActorSystem("akka-spray")

  system.registerOnTermination {
    // put additional cleanup code here
    system.log.info("Application shut down")
    println("application shutting down")
  }

  /**
   * Ensure that the constructed ActorSystem is shut down when the JVM shuts down
   */
  sys.addShutdownHook(system.shutdown())

}

/**
 * This trait contains the actors that make up our application; it can be mixed in with
 * ``BootedCore`` for running code or ``TestKit`` for unit and integration tests.
 */
trait CoreActors extends Configuration {
  this: Core =>

  val blogActor = system.actorOf(Props[BlogActor], "BlogActor")

}