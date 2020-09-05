package controllers

import models._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import javax.inject._
import scala.concurrent.{ ExecutionContext, Future }
import reactivemongo.bson.BSONObjectID
import repositories.GoalRepository

class GoalController @Inject() (
    ec: ExecutionContext,
    goalRepo: GoalRepository
) extends Controller {

  def listGoals = Action.async {
    goalRepo.list().map {
      goals => Ok(views.html.goals.index(goals))
    }
  }

  def createGoal = Action.async(parse.json) {
    _.body.validate[Goal].map { goal =>
      goalRepo.create(goal).map { _ =>
        Created
      }
    }.getOrElse(Future.successful(BadRequest("Invalid format")))
  }

  def readGoal(id: BSONObjectID) = Action.async { req =>
    goalRepo.read(id).map { maybeGoal =>
      maybeGoal.map { goal =>
        Ok(Json.toJson(goal))
      }.getOrElse(NotFound)
    }
  }

  def updateGoal(id: BSONObjectID) = Action.async(parse.json) { req =>
    req.body.validate[Goal].map { goal =>
      goalRepo.update(id, goal).map {
        case Some(goal) => Ok(Json.toJson(goal))
        case _ => NotFound
      }
    }.getOrElse(Future.successful(BadRequest("Invalid Json")))
  }

  def deleteGoal(id: BSONObjectID) = Action.async {
    goalRepo.destroy(id).map {
      case Some(goal) => Ok(Json.toJson(goal))
      case _ => NotFound
    }
  }

}