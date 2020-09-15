package repositories

import java.util.UUID

import com.mongodb.casbah.Imports.{ MongoConnection, MongoDBObject }
import javax.inject.Inject
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ ExecutionContext, Future }
import models.{ Goal, User }
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson._
//import com.mongodb.casbah.Imports._
import play.api.mvc.Action
import models.User_goals
import reactivemongo.api._
import reactivemongo.api.commands.WriteResult
import reactivemongo.play.json._
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.bson.BSONDocument
import reactivemongo.api.collections.bson.BSONCollection

class UserRepository @Inject() (
    implicit
    ec: ExecutionContext,
    reactiveMongoApi: ReactiveMongoApi
) {
  private val collection = MongoConnection()("silhouette")("silhouette.user")

  private def col: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("goal"))

  def find(id: String): Future[Option[User]] =
    col.flatMap(_.find(BSONDocument("userID" -> id)).one[User])

  def updateUserGoal(id: String, goal: Goal, user: User) = {
    val query = MongoDBObject("userId" -> id)
    println(collection.findOne(query))
    collection.update(query, MongoDBObject(
      "userId" -> user.userID,
      "loginInfo" -> user.loginInfo,
      "firstName" -> user.firstName,
      "lastName" -> user.lastName,
      "fullName" -> user.fullName,
      "email" -> user.email,
      "avatarURL" -> user.avatarURL,
      "activated" -> user.activated,
      "goal" -> goal
    ))
  }
}