package mongo

import com.google.inject._
import reactivemongo.api.{MongoConnection, MongoDriver}
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class MongoProvider @Inject() (config: MongoConfig) {

  val driver: MongoDriver = MongoDriver()

  val connection: MongoConnection = {
    val tryConnection = MongoConnection.parseURI(config.uri).map { parsedUri =>
      driver.connection(parsedUri)
    }

    tryConnection match {
      case Success(connection) =>
        connection

      case _ =>
        throw new ExceptionInInitializerError()
    }
  }

  val db = connection.database(config.dbName)

}
