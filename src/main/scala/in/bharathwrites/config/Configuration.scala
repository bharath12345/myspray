package in.bharathwrites.config

import com.typesafe.config.ConfigFactory
import util.Try
import scala.util.Properties
import java.net.URI

/**
 * Holds service configuration settings.
 */
trait Configuration {

  /** Host name/address to start service on. */
  lazy val serviceHost = "0.0.0.0"

  /** Port to start service on. */
  lazy val servicePort = Properties.envOrElse("PORT", "8080").toInt

  lazy val dbUri = new URI(System.getenv("DATABASE_URL"));

  //lazy val username = dbUri.getUserInfo().split(":")(0);
  //lazy val password = dbUri.getUserInfo().split(":")(1);
  //lazy val dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

  /** Database host name/address. */
  lazy val dbHost = Try(dbUri.getHost()).getOrElse("localhost")

  /** Database host port number. */
  lazy val dbPort = Try(dbUri.getPort()).getOrElse(5432)

  /** Service database name. */
  lazy val dbName = "dfadohirbu33sv"

  /** User name used to access database. */
  lazy val dbUser = dbUri.getUserInfo().split(":")(0)

  /** Password for specified user and database. */
  lazy val dbPassword = dbUri.getUserInfo().split(":")(1)

  /**
   * Application config object from src/main/resources/application.conf
   */
  val config = ConfigFactory.load()

}
