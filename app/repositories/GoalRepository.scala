package repositories

import java.util.UUID

import com.mongodb.casbah.Imports.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import javax.inject.Inject
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ ExecutionContext, Future }
import models.Goal
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson._
//import com.mongodb.casbah.Imports._
import org.mongodb.scala.ReadPreference
import play.api.mvc.Action
import models.User_goals
import reactivemongo.api._
import reactivemongo.api.commands.WriteResult
import reactivemongo.play.json._
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.bson.BSONDocument
import reactivemongo.api.collections.bson.BSONCollection
import com.mongodb.casbah.query.dsl.IncOp

class GoalRepository @Inject() (
    implicit
    ec: ExecutionContext,
    reactiveMongoApi: ReactiveMongoApi
) {
  private def collection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("goal"))

  val col = MongoConnection()("silhouette")("goal")

  // sort by "challengers_num"
  def list(limit: Int = 100): Future[Seq[Goal]] =
    collection.flatMap(_.find(BSONDocument.empty)
      .sort(Json.obj("challengers_num" -> -1))
      .cursor[Goal]().collect[Seq](limit, Cursor.FailOnError[Seq[Goal]]()))

  def create(goal: Goal): Future[WriteResult] =
    collection.flatMap((_.insert(goal)))

  def find(id: String): Future[Option[Goal]] =
    collection.flatMap(_.find(BSONDocument("goalID" -> id)).one[Goal])

  def update(id: String, goal: Goal): Future[Option[Goal]] =
    collection.flatMap(_.findAndUpdate(
      BSONDocument("goalID" -> id),
      BSONDocument(
        f"$$set" -> BSONDocument(
          "name" -> goal.name,
          "learning_time" -> goal.learning_time,
          "challengers_num" -> goal.challengers_num
        )
      ),
      true
    ).map(_.result[Goal]))

  def updateChallengersNum(id: String, goal: Goal) = {
    val query = MongoDBObject("goalID" -> id)
    val challengers_num = goal.challengers_num + 1
    println(challengers_num)
    col.update(query, MongoDBObject(
      "goalID" -> goal.goalID,
      "name" -> goal.name,
      "learning_time" -> goal.learning_time,
      "challengers_num" -> challengers_num
    ))
  }

  // Add challengers_num when user chooses goal
  val addChallengerNum = (num: Int) => num + 1

  def destroy(id: String): Future[Option[Goal]] =
    collection.flatMap(_.findAndRemove(BSONDocument("goalID" ->
      id)).map(_.result[Goal]))

  def findOlder1(id: String, collection: BSONCollection): Future[Option[BSONDocument]] = {
    val query = BSONDocument("goalID" -> id)
    // MongoDB .findOne
    collection.find(query).one[BSONDocument]
  }
}
