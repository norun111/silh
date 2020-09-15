package models.daos

import java.util.UUID

import javax.inject._
import models.AuthToken
import org.joda.time.DateTime
import org.mongodb.scala.{ Document, MongoClient, MongoCollection, MongoDatabase }
import play.api.libs.json._
import reactivemongo.api.commands.bson.BSONCountCommandImplicits._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import reactivemongo.api._
import reactivemongo.play.json._
import play.modules.reactivemongo._
import reactivemongo.api.collections.GenericQueryBuilder
//import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.collection.JSONCollection
import Helpers._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.bson._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.bson.BSONDocument
import play.api.libs.json._

/**
 * Give access to the [[AuthToken]] object.
 */
class AuthTokenDAOImpl @Inject() (val reactiveMongoApi: ReactiveMongoApi) extends AuthTokenDAO {

  def collection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("silhouette.token"))

  /**
   * Finds a token by its ID.
   *
   * @param id The unique token ID.
   * @return The found token or None if no token for the given ID could be found.
   */

  def find(id: String): Future[Option[AuthToken]] = {
    println("token_id:", id)
    collection.flatMap(_.find(BSONDocument("id" -> id)).one[AuthToken])
  }

  /**
   * Finds expired tokens.
   *
   * @param dateTime The current date time.
   */
  def findExpired(dateTime: DateTime): Future[Seq[AuthToken]] = {
    val query = Json.obj("expiry" -> Json.obj("$lt" -> dateTime))
    collection.flatMap(_.find(query).cursor[AuthToken](readPreference = ReadPreference.primary).collect[Seq](
      maxDocs = 10,
      err = Cursor.FailOnError[Seq[AuthToken]]()
    ))
  }

  /**
   * Saves a token.
   *
   * @param token The token to save.
   * @return The saved token.
   */
  def save(token: AuthToken): Future[AuthToken] = {
    collection.flatMap(_.insert(token))
    Future.successful(token)
  }

  /**
   * Removes the token for the given ID.
   *
   * @param id The ID for which the token should be removed.
   * @return A future to wait for the process to be completed.
   */
  def remove(id: String): Future[Unit] = {
    val query = Json.obj("id" -> id)
    collection.flatMap(_.remove(query))
    Future.successful(())
  }
}