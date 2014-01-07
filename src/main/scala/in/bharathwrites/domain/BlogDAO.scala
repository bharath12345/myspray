package in.bharathwrites.domain

import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._
import scala.slick.driver.{JdbcProfile}

case class Blog(id: Long,
                title: String, subtitle: String,
                content: String,
                dateTime: DateTime,
                category: Category,
                tags: Array[BlogTag])

class BlogDAO(val driver: JdbcProfile) {
  import driver.simple._

  class Blogs(tag: Tag) extends Table[Blog](tag, "BLOGS") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def title = column[String]("title")

    def subtitle = column[String]("subtitle")

    def content = column[String]("content")

    def date = column[DateTime]("date_time", O.Nullable)

    def * = (id, title, content, date) <> (Blog.tupled, Blog.unapply _)
  }

  val blogs = TableQuery[Blogs]
  private val blogsAutoInc = blogs returning blogs.map(_.id) into { case (b, id) => b.copy(id = id) }

  def create(implicit session: Session) = blogs.ddl.create

  def findById(id: Long)(implicit session: Session): Option[Blog] = (for {
    b <- blogs
    if b.id === id
  } yield b).firstOption

  def findAll(implicit session: Session): List[Blog] = (for{b <- blogs} yield b).list

  def insert(blog: Blog)(implicit session: Session): Blog = blogsAutoInc.insert(blog)

  def update(id: Long, blog: Blog)(implicit session: Session): Blog = {
    //val abc = blogs.where(_.id === id)

    blogs.where(_.id === id).update(blog)
    return blog
  }

  def delete(id: Long)(implicit session: Session): Option[Blog] = {
    val blog = findById(id)
    blogs.where(_.id === id).delete
    return blog
  }

}