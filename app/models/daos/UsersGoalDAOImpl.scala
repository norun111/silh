package models.daos

import javax.inject.Inject
import models.UsersGoal
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UsersGoalDAOImpl @Inject() (val reactiveMongoApi: ReactiveMongoApi) extends UsersGoalDAO {
  def collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("users_goal"))

  def save(usersGoal: UsersGoal): Future[UsersGoal] = {
    collection.flatMap(_.update(Json.obj("usersGoalID" -> usersGoal.usersGoalID), usersGoal, upsert = true))
    Future.successful(usersGoal)
  }
}
