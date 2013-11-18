package in.bharathwrites.domain

import java.util.Date

case class BlogSearchParameters(title: Option[String] = None,
                                    content: Option[String] = None,
                                    date: Option[Date] = None)