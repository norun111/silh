package models.daos

import com.mongodb.casbah.Imports.MongoConnection
import javax.inject._
import models.AuthToken
import org.joda.time.DateTime
import play.modules.reactivemongo._
import reactivemongo.api._
import reactivemongo.bson.{ BSONDocument, _ }
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ ExecutionContext, Future }
import play.api.libs.json._

/**
 * Give access to the [[AuthToken]] object.
 */
class AuthTokenDAOImpl @Inject() (implicit
  ec: ExecutionContext,
    reactiveMongoApi: ReactiveMongoApi) extends AuthTokenDAO {

  private def collection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("silhouette.token"))

  /**
   * Finds a token by its ID.
   *
   * @param id The unique token ID.
   * @return The found token or None if no token for the given ID could be found.
   */

  def find(id: String): Future[Option[AuthToken]] = {
    collection.flatMap(_.find(BSONDocument("id" -> id)).one[AuthToken])
  }

  def list(limit: Int = 100): Future[Seq[AuthToken]] = {
    println(collection)
    collection.flatMap(_.find(BSONDocument.empty)
      .cursor[AuthToken]().collect[Seq](limit, Cursor.FailOnError[Seq[AuthToken]]()))
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