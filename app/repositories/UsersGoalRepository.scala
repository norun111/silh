package repositories

import com.mongodb.casbah.Imports.{ MongoConnection, MongoDBObject }
import javax.inject.Inject
import models._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json._
import scala.concurrent.{ ExecutionContext, Future }
//import reactivemongo.api.bson.{ BSONObjectID }
import scala.concurrent.ExecutionContext.Implicits.global

class UsersGoalRepository @Inject() (
    implicit
    ec: ExecutionContext,
    reactiveMongoApi: ReactiveMongoApi
) {
  private def collection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("users_goal"))

  val col = MongoConnection()("silhouette")("users_goal")

  def update(id: String, leaning_time: Double, users_goal: UsersGoal): Future[Option[UsersGoal]] =
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
    ).map(_.result[UsersGoal]))

  def updateLearningTime(id: String, users_goal: UsersGoal, user: User) = {
    val query = MongoDBObject("usersGoalID" -> id)
    println(col.findOne(query))
    col.update(query, MongoDBObject(
      "usersGoalID" -> users_goal.usersGoalID,
      "user_id" -> users_goal.user_id,
      "goal_id" -> users_goal.goal_id,
      "learning_time" -> user.goal.get.learning_time.toDouble
    ))
  }

}