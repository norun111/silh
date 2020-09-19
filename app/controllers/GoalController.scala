package controllers

import java.util.UUID

import play.api.i18n.Messages

//Import ReactiveMongo plug-in
import play.modules.reactivemongo.{ MongoController, ReactiveMongoApi, ReactiveMongoComponents }

//Import BSON-JSON conversions/collection
import com.mohiva.play.silhouette.api.Silhouette
import com.mongodb.casbah.Imports._
import com.novus.salat._
import forms.UsersGoalForm.form
import forms.UserForm.userForm
import javax.inject._
import models.{ Goal, _ }
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._
import reactivemongo.play.json.collection.{ JSONCollection, _ }
import repositories._
import utils.auth.DefaultEnv
//import com.novus.salat.global._
import scala.concurrent.{ ExecutionContext, Future }
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._

class GoalController @Inject() (
    val reactiveMongoApi: ReactiveMongoApi,
    val messagesApi: MessagesApi,
    ec: ExecutionContext,
    silhouette: Silhouette[DefaultEnv],
    goalRepo: GoalRepository,
    userRepo: UserRepository,
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

  def calculate(userID: String) = Action { implicit request =>
    Ok(views.html.goals.calculate(userForm))
  }

  def listGoals(userID: String = UUID.randomUUID.toString) = silhouette.SecuredAction.async { implicit request =>
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
        formWithErrors => Future(BadRequest(views.html.goals.index(testGoal, request.identity, UUID.randomUUID().toString, formWithErrors))),
        userGoal => {
          collection.flatMap(_.insert(userGoal))

          // use Casbah Find method
          val goalQuery = MongoDBObject("goalID" -> userGoal.goal_id)
          val goal = collGoal findOne goalQuery
          val goalObj = grater[Goal].asObject(goal.get) // .get is so important

          // use Casbah Find method
          val userQuery = MongoDBObject("userId" -> userGoal.user_id)
          val user = collUser findOne userQuery
          val userObj = grater[User].asObject(user.get) // .get is so important

          goalRepo.find(userGoal.goal_id).flatMap {
            case Some(goal) =>
              usersGoalRepo.updateLearningTime(userGoal.usersGoalID, userGoal, goal.learning_time)
              Future(goal)
            case None => Future.successful(Redirect(routes.GoalController.saveUserGoal()).flashing("error" -> Messages("invalid")))
          }
          // insert goal into column goal of User model
          userRepo.updateUserGoal(userGoal.user_id, goalObj, userObj)
          goalRepo.updateChallengersNum(userGoal.goal_id, goalObj)
          Future(Redirect(s"/calculate/${userGoal.user_id}"))
        }
      )
  }

  def readGoal(id: String) = Action.async { req =>
    goalRepo.find(id).map { maybeGoal =>
      maybeGoal.map { goal =>
        Ok(Json.toJson(goal))
      }.getOrElse(NotFound)
    }
  }

  def updateGoal(id: String = UUID.randomUUID().toString) = Action.async(parse.json) { req =>
    req.body.validate[Goal].map { goal =>

      goalRepo.update(id, goal).map {
        case Some(goal) => Ok(Json.toJson(goal))
        case _ => NotFound
      }
    }.getOrElse(Future.successful(BadRequest("Invalid Json")))
  }

  def deleteGoal(id: String = UUID.randomUUID().toString) = Action.async {
    goalRepo.destroy(id).map {
      case Some(goal) => Ok(Json.toJson(goal))
      case _ => NotFound
    }
  }

}