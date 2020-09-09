package repositories

import com.mongodb.casbah.Imports.{ MongoConnection, MongoDBObject }
import javax.inject.Inject
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ ExecutionContext, Future }
import models.Goal
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson._
import play.api.mvc.Action
import models.User_goals
import reactivemongo.api._
//import reactivemongo.api.bson.{ BSONObjectID }
import reactivemongo.api.commands.WriteResult
import reactivemongo.play.json._
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.bson.BSONDocument
import reactivemongo.api.collections.bson.BSONCollection

class Users_goalRepository @Inject() (
    implicit
    ec: ExecutionContext,
    reactiveMongoApi: ReactiveMongoApi
) {
  private def collection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("users_goal"))

  val col = MongoConnection()("silhouette")("users_goal")

  def update2(id: String, leaning_time: Double, users_goal: User_goals): Future[Option[User_goals]] = {
    val updateModifier = BSONDocument(
      f"$$set" -> BSONDocument(
        "usersGoalID" -> users_goal.usersGoalID,
        "user_id" -> users_goal.user_id,
        "goal_id" -> users_goal.goal_id,
        "learning_time" -> leaning_time
      )
    )
    collection.flatMap(_.findAndUpdate(
      selector = BSONDocument("usersGoalID" -> id),
      update = updateModifier,
      fetchNewObject = true
    ).map(_.result[User_goals]))
  }

  def update(id: String, leaning_time: Double, users_goal: User_goals): Future[Option[User_goals]] =
    collection.flatMap(_.findAndUpdate(
      BSONDocument("usersGoalID" -> id),
      BSONDocument(
        f"$$set" -> BSONDocument(
          "usersGoalID" -> users_goal.usersGoalID,
          "user_id" -> users_goal.user_id,
          "goal_id" -> users_goal.goal_id,
          "learning_time" -> leaning_time
        )
      ),
      true
    ).map(_.result[User_goals]))

  def updateLearningTime(id: String, leaning_time: Double, users_goal: User_goals) = {
    println(leaning_time)

    val query = MongoDBObject("usersGoalID" -> id)
    println(col.findOne(query))
    col.update(query, MongoDBObject(
      "usersGoalID" -> users_goal.usersGoalID,
      "user_id" -> users_goal.user_id,
      "goal_id" -> users_goal.goal_id,
      "learning_time" -> leaning_time
    ))
  }

}