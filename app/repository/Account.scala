package repository

import com.google.inject._
import mongo.MongoProvider
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONDocumentHandler}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


case class Account (name: String,
                    email: String,
                    password: String,
                    role: String)


object Account {

  import reactivemongo.bson.Macros

  implicit val userFormat: BSONDocumentHandler[Account] = Macros.handler[Account]

}


class AccountRepository @Inject() (mongo: MongoProvider) {

  import Account._

  val collection: Future[BSONCollection] = mongo.db.map(_.collection("accounts"))

  def create(account: Account): Unit = collection.flatMap(_.insert(account))

  def findByEmail(email: String): Future[Account] = {
    val query = BSONDocument("email" -> email)
    collection.flatMap(_.find(query).requireOne[Account])
  }

  def findByPassword(password: String): Future[Account] = {
    val query = BSONDocument("email" -> password)
    collection.flatMap(_.find(query).requireOne[Account])
  }

}
