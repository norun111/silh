package controllers

import models._
import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import javax.inject.{ Inject, Singleton }
import models.daos.GoalDAO._
import reactivemongo.api._

import scala.concurrent.{ ExecutionContext, Future }
import play.modules.reactivemongo._
import reactivemongo.api.commands.bson.BSONCountCommandImplicits._
import org.mongodb.scala._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model.Filters._
import reactivemongo.api.bson._
import models.daos.Helpers._
import org.mongodb.scala.model.Projections
import repositories.GoalRepository

class GoalController @Inject() (
    ec: ExecutionContext,
    goalRepo: GoalRepository
) extends Controller {

  //  def collection: reactivemongo.play.json.collection.JSONCollection = db.collection[reactivemongo.play.json.collection.JSONCollection]("goal")
  //
  //  def index = Action {
  //    Ok(views.html.goals.index(goals.find.results))
  //  }

  def listGoals = Action.async {
    goalRepo.list().map {
      goals => Ok(Json.toJson(goals))
    }
  }
}
