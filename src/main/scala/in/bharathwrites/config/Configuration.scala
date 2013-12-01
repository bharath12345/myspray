package in.bharathwrites.config

import com.typesafe.config.ConfigFactory
import util.Try
import scala.util.Properties
import java.net.URI

/**
 * Holds service configuration settings.
 */
trait Configuration {
  
  /**
   * Application config object from src/main/resources/application.conf
   */
  val config = ConfigFactory.load()
  
  /**
   * Web server settings
   */

  /** Host name/address to start web service on. */
  lazy val serviceHost = Try(config.getString("service.host")).getOrElse("0.0.0.0")

  /** Port to start web service on. */
  lazy val staticHost = Try(config.getString("service.port")).getOrElse("9876")
  lazy val servicePort = Properties.envOrElse("PORT", staticHost).toInt // <=== This is the Heroku one

  
  /**
   * Database settings
   */
  var x = System.getenv("DATABASE_URL")
  if(x == null || x.length() < 1) {
    x = config.getString("db.local")
  }
  //lazy val dbUri = new URI(Try().getOrElse(config.getString("db.local")));
  lazy val dbUri = new URI(x)
  
  /** Database host name/address. */
  lazy val dbHost = dbUri.getHost()

  /** Database host port number. */
  lazy val dbPort = dbUri.getPort()

  /** Service database name. */
  lazy val dbName = "dfadohirbu33sv"

  /** User name used to access database. */
  lazy val dbUser = dbUri.getUserInfo().split(":")(0)

  /** Password for specified user and database. */
  lazy val dbPassword = dbUri.getUserInfo().split(":")(1)
  
  lazy val blogsTableName = "blogs"

}
