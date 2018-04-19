package mongo

import com.google.inject._
import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus._

@ProvidedBy(classOf[MongoConfigProvider])
case class MongoConfig(user: String,
                       password: String,
                       host: String,
                       port: String,
                       dbName: String,
                       options: String) {

  def uri: String = s"mongodb://$user$password$host:$port/$dbName?$options"

}


@Singleton
class MongoConfigProvider @Inject() (config: Config) extends Provider[MongoConfig] {

  val get: MongoConfig = {
    val user = param(_.as[String]("mongo.user"))
    val password = param(_.as[String]("mongo.pass"))
    val host = param(_.as[String]("mongo.host"))
    val port = param(_.as[String]("mongo.port"))
    val dbname = param(_.as[String]("mongo.dbname"))
    val options = param(_.as[String]("mongo.options"))

    MongoConfig(user, password, host, port, dbname, options)
  }

  private def param[T](f: Config => T) =  f.apply(config)
}
