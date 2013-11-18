package in.bharathwrites.domain

import scala.slick.driver.PostgresDriver.simple._

case class Blog(id: Option[Long], title: String, content: String, date: Option[java.util.Date])

object Blogs extends Table[Blog]("blogs") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def title = column[String]("title")

  def content = column[String]("content")

  def date = column[java.util.Date]("date", O.Nullable)

  def * = id.? ~ title ~ content ~ date.? <> (Blog, Blog.unapply _)

  implicit val dateTypeMapper = MappedTypeMapper.base[java.util.Date, java.sql.Date](
    {
      ud => new java.sql.Date(ud.getTime)
    }, {
      sd => new java.util.Date(sd.getTime)
    })

  val findById = for {
    id <- Parameters[Long]
    c <- this if c.id is id
  } yield c

}