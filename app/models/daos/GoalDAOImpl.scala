package models.daos

import javax.inject.Inject
import models.Goal
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json._

import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.Future

class GoalDAOImpl @Inject() (val reactiveMongoApi: ReactiveMongoApi) extends GoalDAO {
  def collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("goal"))

  def save(goal: Goal): Future[Goal] = {
    collection.flatMap(_.update(Json.obj("goalID" -> goal.goalID), goal, upsert = true))
    Future.successful(goal)
  }
}
