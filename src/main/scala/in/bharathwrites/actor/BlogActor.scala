package in.bharathwrites.actor

import scala.Some
import in.bharathwrites.config.Configuration
import in.bharathwrites.domain._
import akka.actor.Actor
import akka.actor.Props
import akka.event.LoggingReceive
import akka.event.slf4j.SLF4JLogging
import java.sql.SQLException
import scala.slick.driver.{JdbcProfile,PostgresDriver}
import in.bharathwrites.domain.BlogSearchParameters
import in.bharathwrites.domain.Failure
import in.bharathwrites.domain.Blog
import scala.Some
import scala.slick.jdbc.meta.MTable

object BlogActor {
  trait DataAccessRequest
  case class Get(id: Long) extends DataAccessRequest
  case class Search(params: BlogSearchParameters) extends DataAccessRequest

  trait DataAccessResponse
  case class ResponseBlog(blog: Blog) extends DataAccessResponse

  def props() = Props(classOf[BlogActor])
}

class BlogActor extends Actor with Configuration with SLF4JLogging {
  import BlogActor._
  import scala.slick.jdbc.JdbcBackend.{Database, Session}

  def actorRefFactory = context

  def receive = normal

  val normal: Receive = LoggingReceive {
    case Get(id: Long) => {
      log.debug("Retrieving blog with id %d".format(id))
      //sender ! ResponseBlog(get(id))
      get(id) match {
        case Right(blog) => sender ! blog
        case Left(_) =>
      }
    }

    case Search(params: BlogSearchParameters) => {
    }
  }

  // init Database instance
  val db = Database.forURL(url = "jdbc:postgresql://%s:%d/%s".format(dbHost, dbPort, dbName),
    user = dbUser, password = dbPassword, driver = "org.postgresql.Driver")

  val dao = new BlogDAO(PostgresDriver)

  // create tables if not exist
  db.withSession { implicit session: Session =>
    if (!MTable.getTables.list.exists(_.name.name == "BLOGS"))
      dao.create
  }

  def get(id: Long): Either[Failure, Blog] = {
    try {
      db.withSession { implicit session: Session =>
        log.debug("finding for id = " + id)
        dao.findById(id) match {
          case Some(blog: Blog) => {
            log.debug("found blog! = " + blog)
            Right(blog)
          }
          case _ => {
            log.debug("did not find blog!")
            Left(notFoundError(id))
          }
        }
      }
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }

  /*def create(blog: Blog): Either[Failure, Blog] = {
    try {
      val id = db.withSession { implicit session: Session =>
        Blogs returning Blogs.id insert blog
      }
      Right(blog.copy(id = Some(id)))
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }*/


  /*def search(params: BlogSearchParameters): Either[Failure, List[Blog]] = {
    implicit val typeMapper = Blogs.dateTypeMapper

    try {
      db.withSession {
        val query = for {
          blog <- Blogs if {
            Seq(
              params.title.map(blog.title is _),
              params.date.map(blog.date is _)).flatten match {
                case Nil => ConstColumn.TRUE
                case seq => seq.reduce(_ && _)
              }
          }
        } yield blog

        Right(query.run.toList)
      }
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }*/

  protected def databaseError(e: SQLException) =
    Failure("%d: %s".format(e.getErrorCode, e.getMessage), FailureType.DatabaseFailure)

  protected def notFoundError(blogId: Long) =
    Failure("Blog with id=%d does not exist".format(blogId), FailureType.NotFound)


  /*def update(id: Long, blog: Blog): Either[Failure, Blog] = {
    try
      db.withSession {
        Blogs.where(_.id === id) update blog.copy(id = Some(id)) match {
          case 0 => Left(notFoundError(id))
          case _ => Right(blog.copy(id = Some(id)))
        }
      }
    catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }

  def delete(id: Long): Either[Failure, Blog] = {
    try {
      db.withTransaction {
        val query = Blogs.where(_.id === id)
        val blogs = query.run.asInstanceOf[List[Blog]]
        blogs.size match {
          case 0 =>
            Left(notFoundError(id))
          case _ => {
            query.delete
            Right(blogs.head)
          }
        }
      }
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }*/

}