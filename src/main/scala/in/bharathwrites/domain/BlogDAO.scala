package in.bharathwrites.domain

import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._
import scala.slick.driver.{JdbcProfile}

case class Blog(id: Long, title: String, content: String, dateTime: DateTime)

class BlogDAO(val driver: JdbcProfile) {
  import driver.simple._

  class Blogs(tag: Tag) extends Table[Blog](tag, "BLOGS") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def title = column[String]("title")

    def content = column[String]("content")

    def date = column[DateTime]("date_time", O.Nullable)

    def * = (id, title, content, date) <> (Blog.tupled, Blog.unapply _)

  }

  val props = TableQuery[Blogs]

  def create(implicit session: Session) =
    props.ddl.create

  def findById(id: Long)(implicit session: Session): Option[Blog] = (for {
    p <- props
    if p.id === id
  } yield p).firstOption


}