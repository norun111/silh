package controllers

import java.util.UUID

import com.mohiva.play.silhouette.api.Silhouette
import models._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import javax.inject._
import jdk.nashorn.internal.runtime.options.LoggingOption.LoggerInfo
import play.api.data.Form
import play.api.data.Forms._

import scala.concurrent._
import play.api.i18n.{ I18nSupport, MessagesApi }
import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }

import scala.concurrent.{ ExecutionContext, Future }
import reactivemongo.bson.BSONObjectID
import repositories.GoalRepository
import utils.auth.DefaultEnv
import play.modules.reactivemongo._
import reactivemongo.play.json.collection.JSONCollection

class GoalController @Inject() (
    val reactiveMongoApi: ReactiveMongoApi,
    val messagesApi: MessagesApi,
    ec: ExecutionContext,
    silhouette: Silhouette[DefaultEnv],
    goalRepo: GoalRepository,
    implicit val webJarAssets: WebJarAssets
) extends Controller with I18nSupport with MongoController with ReactiveMongoComponents {

  val user_goalForm = Form(
    mapping(
      "user_id" -> text,
      "goal_id" -> ignored(BSONObjectID.generate: BSONObjectID),
      "learning_time" -> number
    )(User_goals.apply)(User_goals.unapply)
  )

//    val userForm = Form(
//      mapping(
//        "userID" -> text,
//        "loginInfo" -> ignored(LoginInfo),
//        "firstName" -> optional(text),
//        "lastName" -> optional(text),
//        "fullName" -> optional(text),
//        "email" -> optional(text),
//        "avatarURL" -> optional(text),
//        "activated" -> boolean,
//        "goal" -> list(mapping(
//          "_id" -> ignored(BSONObjectID.generate: BSONObjectID),
//          "name" -> text,
//          "learning_time" -> number,
//          "challengers_num" -> number
//        )(Goal.apply)(Goal.unapply))
//      )(User.apply)(User.unapply)
//    )

  def listGoals = silhouette.SecuredAction.async { implicit request =>
    // sort by descending "challengers_num"
    // input user_goalForm in parameter goal_id -> goal._id user_id -> request.identity.userId
    goalRepo.list().map {
      goals =>
        Ok(views.html.goals.index())
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

  //  def userUpdate(id: UUID) = Action.async {
  //    implicit request =>
  //      employeeForm.bindFromRequest.fold(
  //        formWithErrors => Future.successful(BadRequest(views.html.edit(id, formWithErrors))),
  //        employee => {
  //          val futureUpdateEmployee = collection.update(Json.obj("_id" -> Json.obj("$oid" -> id)), employee.copy(_id = BSONObjectID(id)))
  //          futureUpdateEmployee.map { result => home }
  //        })
  //  }

  def deleteGoal(id: BSONObjectID) = Action.async {
    goalRepo.destroy(id).map {
      case Some(goal) => Ok(Json.toJson(goal))
      case _ => NotFound
    }
  }

}