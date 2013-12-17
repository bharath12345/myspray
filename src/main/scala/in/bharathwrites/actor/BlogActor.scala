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
  case class Get(id: Long)                extends DataAccessRequest
  case class GetAll()                     extends DataAccessRequest
  case class Create(blog: Blog)           extends DataAccessRequest
  case class Update(id: Long, blog: Blog) extends DataAccessRequest
  case class Delete(id: Long)             extends DataAccessRequest

  case class Search(params: BlogSearchParameters) extends DataAccessRequest

  def props() = Props(classOf[BlogActor])
}

class BlogActor extends Actor with Configuration with SLF4JLogging with AbstractActor {
  import BlogActor._
  import scala.slick.jdbc.JdbcBackend.{Database, Session}

  def actorRefFactory = context

  def receive: Receive = LoggingReceive {
    case Get(id: Long) => {
      log.debug("Retrieving blog with id %d".format(id))
      get(id) match {
        case Right(blog) => sender ! blog
        case Left(_) =>
      }
    }

    case GetAll => sender ! getAll

    case Search(params: BlogSearchParameters) => {}

    case Create(blog: Blog) => sender ! create(blog)

    case Update(id: Long, blog: Blog) => sender ! update(id, blog)

    case Delete(id: Long) => {
      log.debug("Deleting blog with id %d".format(id))
      get(id) match {
        case Right(blog) => sender ! blog
        case Left(_) =>
      }
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

  // ToDo: Wrap each CRUD operation in a Transaction

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

  def getAll: List[Blog] = {
      db.withSession{ implicit session: Session =>
        log.debug("getting all blogs")
        dao.findAll
    }
  }

  def create(blog: Blog): Blog = {
      db.withSession { implicit session: Session =>
        log.debug("creating blog = " + blog.toString)
        dao.insert(blog)
      }
  }

  def update(id: Long, blog: Blog): Blog = {
      db.withSession { implicit session: Session =>
        dao.update(id, blog)
      }
  }

  def delete(id: Long): Option[Blog] = {
    db.withSession { implicit session: Session =>
      dao.delete(id)
    }
  }


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

}