package repository

import java.util.Date

import com.google.inject._
import mongo.MongoProvider
import reactivemongo.api.Cursor
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import reactivemongo.bson.{BSONDocument, BSONDocumentHandler, BSONObjectID}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


case class Movie(_id: Option[BSONObjectID], title: String, description: String, sessions: Option[Seq[Date]])


object Movie {

  import reactivemongo.bson.Macros
  import play.api.libs.json._

  implicit val movieBsonFormat: BSONDocumentHandler[Movie] = Macros.handler[Movie]

  implicit val movieJsonFormat: OFormat[Movie] = Json.format[Movie]

}


class MovieRepository @Inject() (mongo: MongoProvider) {

  import Movie._

  val collection: Future[BSONCollection] = mongo.db.map(_.collection("movies"))

  def findAll(): Future[Seq[Movie]] = {
    val query = BSONDocument.empty
    collection.flatMap(_.find(query).cursor[Movie]().collect[List](20, Cursor.FailOnError[List[Movie]]()))
  }

  def findByTitle(title: String): Future[Movie] = {
    val query = BSONDocument("title" -> title)
    collection.flatMap(_.find(query).requireOne[Movie])
  }

  def updateDescriptionByTitle(title: String, description: String): Future[UpdateWriteResult] = {
    val selector = BSONDocument("title" -> title)

    val modifier = BSONDocument(
      "$set" -> BSONDocument(
        "description" -> description
      )
    )

    collection.flatMap(_.update(selector, modifier))
  }

  def deleteMovieByTitle(title: String): Future[WriteResult] = {
    val selector = BSONDocument("title" -> title)

    collection.flatMap(_.remove(selector))
  }

}