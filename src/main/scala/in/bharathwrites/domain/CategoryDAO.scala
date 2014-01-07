package in.bharathwrites.domain

import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._
import scala.slick.driver.{JdbcProfile}

/**
 * Created by bharadwaj on 07/01/14.
 */

case class Category(id: Long, category: String, dateTime: DateTime)

class CategoryDAO(val driver: JdbcProfile) {
  import driver.simple._

  class Categorys(tag: Tag) extends Table[Category](tag, "CATEGORYS") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def title = column[String]("title")

    def content = column[String]("content")

    def date = column[DateTime]("date_time", O.Nullable)

    def * = (id, title, content, date) <> (Category.tupled, Category.unapply _)
  }

  val categorys = TableQuery[Categorys]
  private val categorysAutoInc = categorys returning categorys.map(_.id) into { case (b, id) => b.copy(id = id) }

  def create(implicit session: Session) = categorys.ddl.create

  def findById(id: Long)(implicit session: Session): Option[Category] = (for {
    b <- categorys
    if b.id === id
  } yield b).firstOption

  def findAll(implicit session: Session): List[Category] = (for{b <- categorys} yield b).list

  def insert(category: Category)(implicit session: Session): Category = categorysAutoInc.insert(category)

  def update(id: Long, category: Category)(implicit session: Session): Category = {
    //val abc = categorys.where(_.id === id)

    categorys.where(_.id === id).update(category)
    return category
  }

}
