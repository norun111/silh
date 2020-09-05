package controllers

import javax.inject.Inject
import play.api.mvc.Controller
import repositories.Users_goalRepository
import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scala.concurrent._
import scala.concurrent.duration.Duration
import play.api.data.Form
import models._
import reactivemongo.api._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.ExecutionContext

class Users_goalController @Inject() (
                                 ec: ExecutionContext,
                                 users_goalRepo: Users_goalRepository
                               ) extends Controller {
  val users_goalForm = Form[User_goals](
    mapping(
      "user_id" -> nonEmptyText,
      "goal_id" -> nonEmptyText,
      "learning_time" -> number
    )(User_goals.apply)(User_goals.unapply)
  )

  def createGoal = Action.async(parse.json) {
    _.body.validate[Goal].map { goal =>
      goalRepo.create(goal).map { _ =>
        Created
      }
    }.getOrElse(Future.successful(BadRequest("Invalid format")))
  }
}