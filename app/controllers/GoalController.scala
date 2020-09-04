package controllers

import models._
import play.api.mvc._
import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import javax.inject.Inject
import models.daos.GoalDAO._
import reactivemongo.api._
import scala.concurrent.Future
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection
import org.mongodb.scala._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model.Filters._
import models.daos.Helpers._
import org.mongodb.scala.model.Projections

class GoalController extends Controller {

  def index = Action {
    println(goals.find.results)

    Ok(views.html.goals.index(goals.find.results))
  }
}
