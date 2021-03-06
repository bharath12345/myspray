package in.bharathwrites.routers

import spray.json._
import java.util.UUID
import scala.reflect.ClassTag
import spray.httpx.marshalling.{MetaMarshallers, Marshaller, CollectingMarshallingContext}
import spray.http.StatusCode
import spray.httpx.SprayJsonSupport
import org.joda.time.DateTime
import in.bharathwrites.domain.FailureType.Failure
import in.bharathwrites.domain.{Blog, FailureType}
import in.bharathwrites.domain.Blog
import org.pegdown.PegDownProcessor

/**
 * Contains useful JSON formats: ``j.u.Date``, ``j.u.UUID`` and others; it is useful
 * when creating traits that contain the ``JsonReader`` and ``JsonWriter`` instances
 * for types that contain ``Date``s, ``UUID``s and such like.
 */
trait DefaultJsonFormats extends DefaultJsonProtocol with SprayJsonSupport with MetaMarshallers {

  val pegdown = new PegDownProcessor

  /**
   * Computes ``RootJsonFormat`` for type ``A`` if ``A`` is object
   */
  def jsonObjectFormat[A: ClassTag]: RootJsonFormat[A] = new RootJsonFormat[A] {
    val ct = implicitly[ClassTag[A]]

    def write(obj: A): JsValue = JsObject("value" -> JsString(ct.runtimeClass.getSimpleName))

    def read(json: JsValue): A = ct.runtimeClass.newInstance().asInstanceOf[A]
  }

  /**
   * Instance of the ``RootJsonFormat`` for the ``j.u.UUID``
   */
  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString)

    def read(value: JsValue) = value match {
      case JsString(x) => UUID.fromString(x)
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val DateFormat = new RootJsonFormat[DateTime] {
    //lazy val format = new java.text.SimpleDateFormat()
    def write(date: DateTime) = JsString(date.toString())

    def read(json: JsValue): DateTime = new DateTime(0)
  }

  implicit object FailureFormat extends RootJsonFormat[Failure] {
    def write(failure: Failure) = JsString(failure.toString)

    def read(json: JsValue): Failure = FailureType.InternalError
  }

  implicit object BlogFormat extends RootJsonFormat[Blog] {
    def write(b: Blog) =
      JsObject(
        "title" -> JsString(b.title),
        "content" -> JsString(pegdown.markdownToHtml(b.content)),
        "id" -> JsNumber(b.id),
        "date" -> JsString(b.dateTime.toString())
      )

    def read(json: JsValue) = {
      json.asJsObject.getFields("title", "content") match {
        case Seq(JsString(title), JsString(content)) => {
          new Blog(0, title, content, new DateTime())
        }
        case _ => {
          throw new DeserializationException("Problem encountered in Blog demarshaling")
        }
      }
    }
  }

  /**
   * Type alias for function that converts ``A`` to some ``StatusCode``
   * @tparam A the type of the input values
   */
  type ErrorSelector[A] = A => StatusCode

  /**
   * Marshals instances of ``Either[A, B]`` into appropriate HTTP responses by marshalling the values
   * in the left or right projections; and by selecting the appropriate HTTP status code for the
   * values in the left projection.
   *
   * @param ma marshaller for the left projection
   * @param mb marshaller for the right projection
   * @param esa the selector converting the left projection to HTTP status code
   * @tparam A the left projection
   * @tparam B the right projection
   * @return marshaller
   */
  implicit def errorSelectingEitherMarshaller[A, B](implicit ma: Marshaller[A], mb: Marshaller[B], esa: ErrorSelector[A]): Marshaller[Either[A, B]] =
    Marshaller[Either[A, B]] {
      (value, ctx) =>
        value match {
          case Left(a) =>
            val mc = new CollectingMarshallingContext()
            ma(a, mc)
            ctx.handleError(ErrorResponseException(esa(a), mc.entity))
          case Right(b) =>
            mb(b, ctx)
        }
    }

}
