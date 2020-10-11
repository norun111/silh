package controllers

import java.util.{ Date, Locale, UUID }

import forms.UsersGoalForm.usersGoalForm
import models.daos.UserDAO
import models.services.UserService
import play.api.i18n.Messages

import scala.math._
//Import ReactiveMongo plug-in
import play.modules.reactivemongo.{ MongoController, ReactiveMongoApi, ReactiveMongoComponents }

//Import BSON-JSON conversions/collection
import com.mohiva.play.silhouette.api.Silhouette
import com.mongodb.casbah.Imports._
import com.novus.salat._
import forms.TimeForm.timeForm
import javax.inject._
import models.Goal
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._
import reactivemongo.play.json.collection.{ JSONCollection, _ }
import repositories._
import utils.auth.DefaultEnv
import scala.concurrent.{ ExecutionContext, Future }

import org.joda.time.DateTime
import org.joda.time.format._
import org.joda.time.DateTimeZone

class UserController @Inject() (
    val reactiveMongoApi: ReactiveMongoApi,
    val messagesApi: MessagesApi,
    ec: ExecutionContext,
    silhouette: Silhouette[DefaultEnv],
    userRepo: UserRepository,
    goalRepo: GoalRepository,
    usersGoalRepo: UsersGoalRepository,
    userService: UserService,
    userDAO: UserDAO,
    implicit val webJarAssets: WebJarAssets
) extends Controller with I18nSupport with MongoController with ReactiveMongoComponents {

  private def collection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("users_goal"))

  def show(id: String) = silhouette.SecuredAction.async { implicit request =>
    val uuid = UUID.randomUUID.toString
    val dateTime = "%tY/%<tm/%<td %<tR" format new Date
    val dateSplit = dateTime.split(" ")
    val timeSplit = dateSplit(1).split(":")
    val hour = timeSplit(0)
    val nowTime = hour.toInt

    var greeting = ""
    if (nowTime >= 4 && nowTime < 10)
      greeting = "Good Morning"
    else if (nowTime >= 10 && nowTime < 18)
      greeting = "Good Afternoon"
    else if (nowTime >= 18 && nowTime < 23 || nowTime >= 0)
      greeting = "Good Evening"

    userService.retrieve(request.identity.userID).flatMap {
      case Some(user) =>
        //        ユーザーのshowページへのリダイレクト
        usersGoalRepo.findAll(user.userID).flatMap { usersGoals =>
          Future.successful(Ok(views.html.users.show(user, greeting, usersGoalForm, user.userID, user.goal.get.goalID, dateTime, uuid, usersGoals)))
        }
      case None =>
        Future.successful(Redirect(routes.SignInController.view()).flashing("error" -> Messages("invalid.activation.link")))
    }
  }

  // Error Handle
  val testGoal: Seq[Goal] = Seq(Goal(goalID = "Error", name = "Error", learning_time = 1000, challengers_num = 0))
  //  進捗の保存
  def createProgress = silhouette.SecuredAction.async {
    implicit request =>
      usersGoalForm.bindFromRequest.fold(
        //        エラー表示
        formWithErrors => Future(BadRequest(views.html.goals.index(testGoal, request.identity, request.identity.userID, "%tY/%<tm/%<td %<tR" format new Date, formWithErrors))),
        userGoal => {
          collection.flatMap(_.insert(userGoal))
          goalRepo.find(userGoal.goal_id).flatMap {
            case Some(goal) =>
              userService.retrieve(userGoal.user_id).flatMap {
                case Some(user) =>
                  userService.saveUsersGoal(userGoal.copy(learning_time = userGoal.learning_time)) // users_goalのlearning_timeカラムを更新している
                  //                  usersGoalRepo.updateLearningTime(userGoal.usersGoalID, userGoal, goal.learning_time)
                  Future(Redirect(routes.GoalController.calculate(userGoal.user_id)))
                case None =>
                  Future.successful(Redirect(routes.GoalController.listGoals(userGoal.user_id)).flashing("error" -> Messages("invalid")))
              }
            case None => Future.successful(Redirect(routes.GoalController.listGoals(userGoal.user_id)).flashing("error" -> Messages("invalid")))
          }
        }
      )
  }
}