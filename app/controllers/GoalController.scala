package controllers

import java.util.UUID

import models.daos.UserDAO
import models.services.UserService
import play.api.i18n.Messages

import scala.math._
//Import ReactiveMongo plug-in
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}

//Import BSON-JSON conversions/collection
import com.mohiva.play.silhouette.api.Silhouette
import com.mongodb.casbah.Imports._
import com.novus.salat._
import forms.TimeForm.timeForm
import forms.UsersGoalForm.form
import javax.inject._
import models.Goal
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._
import reactivemongo.play.json.collection.{JSONCollection, _}
import repositories._
import utils.auth.DefaultEnv
import scala.concurrent.{ExecutionContext, Future}

class GoalController @Inject() (
    val reactiveMongoApi: ReactiveMongoApi,
    val messagesApi: MessagesApi,
    ec: ExecutionContext,
    silhouette: Silhouette[DefaultEnv],
    goalRepo: GoalRepository,
    userRepo: UserRepository,
    userService: UserService,
    userDAO: UserDAO,
    usersGoalRepo: UsersGoalRepository,
    implicit val webJarAssets: WebJarAssets
) extends Controller with I18nSupport with MongoController with ReactiveMongoComponents {

  implicit val ctx = new Context {
    val name = "Custom_Classloader"
    override val typeHintStrategy = StringTypeHintStrategy(when = TypeHintFrequency.WhenNecessary, typeHint = "_t")
  }

  private def collection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("users_goal"))

  val collGoal = MongoConnection()("silhouette")("goal")
  val collUser = MongoConnection()("silhouette")("silhouette.user")

  def listGoals(userID: String) = silhouette.SecuredAction.async { implicit request =>
    // sort by descending "challengers_num"
    // input user_goalForm in parameter goal_id -> goal._id user_id -> request.identity.userId
    val uuid = UUID.randomUUID
    goalRepo.list().map {
      goals =>
        Ok(views.html.goals.index(goals, request.identity, uuid.toString, form))
    }
  }

  def createGoal = Action.async(parse.json) {
    _.body.validate[Goal].map { goal =>
      goalRepo.create(goal).map { _ =>
        Created
      }
    }.getOrElse(Future.successful(BadRequest("Invalid format")))
  }

  // Error Handle
  val testGoal: Seq[Goal] = Seq(Goal(goalID = "Error", name = "Error", learning_time = 1000, challengers_num = 0))

  def saveUserGoal = silhouette.SecuredAction.async {
    implicit request =>
      form.bindFromRequest.fold(
        formWithErrors => Future(BadRequest(views.html.goals.index(testGoal, request.identity, request.identity.userID, formWithErrors))),
        userGoal => {
          collection.flatMap(_.insert(userGoal))
          goalRepo.find(userGoal.goal_id).flatMap {
            case Some(goal) =>
              userService.retrieve(userGoal.user_id).flatMap {
                case Some(user) =>
                  userService.save(user.copy(goal = Option(goal)))
                  //                  userRepo.updateUserGoal(userGoal.user_id, goal, user)
                  val challengers_num = goal.challengers_num + 1
                  userService.saveGoal(goal.copy(challengers_num = challengers_num))
                  //                  goalRepo.updateChallengersNum(userGoal.goal_id, goal)
                  userService.saveUsersGoal(userGoal.copy(learning_time = goal.learning_time))
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

  def calculate(userID: String) = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.goals.calculate(request.identity, timeForm)))
  }

  def updateUserTime(id: String) = silhouette.SecuredAction.async {
    implicit request =>
      timeForm.bindFromRequest.fold(
        formWithErrors => Future.successful(BadRequest(views.html.goals.calculate(request.identity, formWithErrors))),
        time => {
          userService.save(request.identity.copy(sTime = time.sTime, wTime = time.wTime, oTime = time.oTime))
          Future(Redirect(routes.GoalController.result(id)))
        }
      )
  }

  def result(userID: String) = silhouette.SecuredAction.async { implicit request =>
    userService.retrieve(request.identity.userID).flatMap {
      case Some(user) =>
        val week_time: Int = 24 * 7 - ((7 * user.sTime) + 5 * (user.wTime) + 7 * (user.oTime))
        val week_day_time: Int = 24 - (user.sTime + user.wTime + user.oTime)
        val week_day_end: Int = 24 - (user.sTime + user.oTime)
        val require_week: Double = user.goal.get.learning_time / week_time
        val require_month: Double = require_week / 4
        val monthStr: String = f"$require_month%1.1f"
        val ceilWeek: Int = round(require_week).toInt
        //        val now = java.time.LocalDate.now
        //        val dateAsInt = now.getYear * 10000 + now.getMonthValue * 100 + now.getDayOfMonth
        //        val achievement_time: Int = dateAsInt + (7 * require_week).toInt
        Future.successful(Ok(views.html.goals.result(request.identity, week_time, week_day_time, week_day_end, ceilWeek, monthStr)))
    }
  }

  def readGoal(id: String) = Action.async { req =>
    goalRepo.find(id).map { maybeGoal =>
      maybeGoal.map { goal =>
        Ok(Json.toJson(goal))
      }.getOrElse(NotFound)
    }
  }

  def updateGoal(id: String) = Action.async(parse.json) { req =>
    req.body.validate[Goal].map { goal =>
      goalRepo.update(id, goal).map {
        case Some(goal) => Ok(Json.toJson(goal))
        case _ => NotFound
      }
    }.getOrElse(Future.successful(BadRequest("Invalid Json")))
  }

  def deleteGoal(id: String) = Action.async {
    goalRepo.destroy(id).map {
      case Some(goal) => Ok(Json.toJson(goal))
      case _ => NotFound
    }
  }

}