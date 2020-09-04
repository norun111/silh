package controllers

import models._
import play.api.mvc._
import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import models._
import reactivemongo.api._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import models.daos.GoalDAO._

class GoalController extends Controller {

  def index = Action {
    val goals = indexGoals
    println((goals))
    Ok(views.html.goals.index(goals))
  }
}
