package repositories

import com.mongodb.casbah.Imports.{ MongoConnection, MongoDBObject }
import javax.inject.Inject
import models._
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.Cursor
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json._

import scala.concurrent.{ ExecutionContext, Future }
//import reactivemongo.api.bson.{ BSONObjectID }

import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.bson.BSONDocument
import reactivemongo.api.collections.bson.BSONCollection

class UsersGoalRepository @Inject() (
    implicit
    ec: ExecutionContext,
    reactiveMongoApi: ReactiveMongoApi
) {
  private def collection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("users_goal"))

  val col = MongoConnection()("silhouette")("users_goal")

  def findAll(userID: String)(implicit ec: ExecutionContext): Future[List[UsersGoal]] =
    collection.flatMap(_.find(BSONDocument("user_id" -> userID)).cursor[UsersGoal]().
      collect[List](100, Cursor.FailOnError[List[UsersGoal]]()))

}