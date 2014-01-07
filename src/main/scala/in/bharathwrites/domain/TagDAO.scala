package in.bharathwrites.domain

import scala.slick.driver.JdbcProfile
import org.joda.time.DateTime

/**
 * Created by bharadwaj on 07/01/14.
 */

case class BlogTag(id: Long, tag: String)

class BlogTagDAO(val driver: JdbcProfile) {
  import driver.simple._

  class BlogTags(tag: Tag) extends Table[BlogTag](tag, "CATEGORYS") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def title = column[String]("title")

    def content = column[String]("content")

    def date = column[DateTime]("date_time", O.Nullable)

    def * = (id, title, content, date) <> (BlogTag.tupled, BlogTag.unapply _)
  }

  val blogTags = TableQuery[BlogTags]
  private val blogTagsAutoInc = blogTags returning blogTags.map(_.id) into { case (b, id) => b.copy(id = id) }

  def create(implicit session: Session) = blogTags.ddl.create

  def findById(id: Long)(implicit session: Session): Option[BlogTag] = (for {
    b <- blogTags
    if b.id === id
  } yield b).firstOption

  def findAll(implicit session: Session): List[BlogTag] = (for{b <- blogTags} yield b).list

  def insert(blogTag: BlogTag)(implicit session: Session): BlogTag = blogTagsAutoInc.insert(blogTag)

  def update(id: Long, blogTag: BlogTag)(implicit session: Session): BlogTag = {
    //val abc = blogTags.where(_.id === id)

    blogTags.where(_.id === id).update(blogTag)
    return blogTag
  }
}
