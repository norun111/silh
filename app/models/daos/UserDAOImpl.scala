package models.daos

import java.util.UUID

import javax.inject._
import com.mohiva.play.silhouette.api.LoginInfo
import com.mongodb.casbah.Imports.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import models.{ Goal, User, UsersGoal }

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
import reactivemongo.play.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import reactivemongo.api._
import play.modules.reactivemongo._
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.collection.JSONCollection

import scala.collection.mutable

/**
 * Give access to the user object.
 */
class UserDAOImpl @Inject() (val reactiveMongoApi: ReactiveMongoApi) extends UserDAO {

  def collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("silhouette.user"))
  val col = MongoConnection()("silhouette")("silhouette.user")

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo): Future[Option[User]] = {
    val query = Json.obj("loginInfo" -> loginInfo)
    collection.flatMap(_.find(query).one[User])
  }

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: String): Future[Option[User]] = {
    collection.flatMap(_.find(Json.obj("userId" -> userID)).one[User])
  }

  def list(limit: Int = 100): Future[Seq[User]] = {
    collection.flatMap(_.find(BSONDocument.empty)
      .cursor[User]().collect[Seq](limit, Cursor.FailOnError[Seq[User]]()))
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User): Future[User] = {
    println("save")
    collection.flatMap(_.update(Json.obj("userId" -> user.userID), user, upsert = true))
    Future.successful(user)
  }

  def colGoal: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("goal"))

  def saveGoal(goal: Goal): Future[Goal] = {
    colGoal.flatMap(_.update(Json.obj("goalID" -> goal.goalID), goal, upsert = true))
    Future.successful(goal)
  }

  def colUsersGoal: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("users_goal"))

  def saveUsersGoal(usersGoal: UsersGoal): Future[UsersGoal] = {
    colUsersGoal.flatMap(_.update(Json.obj("usersGoalID" -> usersGoal.usersGoalID), usersGoal, upsert = true))
    Future.successful(usersGoal)
  }
}