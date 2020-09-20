package repositories

import com.mongodb.casbah.Imports.{ MongoConnection, MongoDBObject, _ }
import com.novus.salat._
import javax.inject.Inject
import models.{ Goal, User }
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.{ BSONDocument, _ }
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ ExecutionContext, Future }

class UserRepository @Inject() (
    implicit
    ec: ExecutionContext,
    reactiveMongoApi: ReactiveMongoApi
) {

  implicit val ctx = new Context {
    val name = "Custom_Classloader"
    override val typeHintStrategy = StringTypeHintStrategy(when = TypeHintFrequency.WhenNecessary, typeHint = "_t")
  }

  private val collection = MongoConnection()("silhouette")("silhouette.user")

  private def col: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("silhouette.user"))

  def find(id: String): Future[Option[User]] =
    col.flatMap(_.find(BSONDocument("userId" -> id)).one[User])

  def updateUserGoal(id: String, goal: Goal, user: User) = {
    val query = MongoDBObject("userId" -> id)
    collection.update(query, MongoDBObject(
      "userId" -> user.userID,
      "loginInfo" -> user.loginInfo,
      "firstName" -> user.firstName,
      "lastName" -> user.lastName,
      "fullName" -> user.fullName,
      "email" -> user.email,
      "avatarURL" -> user.avatarURL,
      "activated" -> user.activated,
      "goal" -> goal,
      "sTime" -> user.sTime,
      "wTime" -> user.wTime,
      "oTime" -> user.oTime
    ))
  }

  def updateTime(id: String, user: User, goal: Goal, sTime: Int, wTime: Int, oTime: Int) = {
    val query = MongoDBObject("userId" -> id)
    collection.update(query, MongoDBObject(
      "userId" -> user.userID,
      "loginInfo" -> user.loginInfo,
      "firstName" -> user.firstName,
      "lastName" -> user.lastName,
      "fullName" -> user.fullName,
      "email" -> user.email,
      "avatarURL" -> user.avatarURL,
      "activated" -> user.activated,
      "goal" -> goal,
      "sTime" -> sTime,
      "wTime" -> wTime,
      "oTime" -> oTime
    ))
  }
}