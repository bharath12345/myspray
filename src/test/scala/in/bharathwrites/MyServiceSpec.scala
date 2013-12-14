package in.bharathwrites

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import in.bharathwrites.routers.BlogRoutes

class MyServiceSpec extends Specification with Specs2RouteTest with BlogRoutes {
  def actorRefFactory = system
  
  /*"MyService" should {

    "return a greeting for GET requests to the root path" in {
      Get() ~> myRoutes ~> check {
        responseAs[String] must contain("Say hello")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> myRoutes ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(myRoutes) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }
  }*/
}
