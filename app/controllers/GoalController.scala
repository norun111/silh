package controllers

import java.util.UUID

import com.mongodb.client._
import com.novus.salat.util.encoding.TypeHintEncoding
import org.bson.Document

//Import ReactiveMongo plug-in
import play.modules.reactivemongo.{ MongoController, ReactiveMongoApi, ReactiveMongoComponents }

//Import BSON-JSON conversions/collection
import com.mohiva.play.silhouette.api.Silhouette
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoCollection
import javax.inject._
import models.{ Goal, _ }
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._
import reactivemongo.play.json._
import reactivemongo.play.json.collection.{ JSONCollection, _ }
import repositories._
import utils.auth.DefaultEnv
import models.daos.Helpers._
import com.novus.salat._
//import com.novus.salat.global._
import scala.concurrent.{ ExecutionContext, Future }
import com.mongodb.MongoClient
import scala.collection.immutable.IndexedSeq
import com.mongodb.client.MongoCollection
import play.api.Environment

class GoalController @Inject() (
    val reactiveMongoApi: ReactiveMongoApi,
    val messagesApi: MessagesApi,
    ec: ExecutionContext,
    silhouette: Silhouette[DefaultEnv],
    goalRepo: GoalRepository,
    userRepo: UserRepository,
    usersGoalRepo: Users_goalRepository,
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

  var user_goalForm = Form(
    mapping(
      "usersGoalID" -> text,
      "user_id" -> text,
      "goal_id" -> text,
      "learning_time" -> of(doubleFormat)
    )(User_goals.apply)(User_goals.unapply)
  )

  def confirm = silhouette.SecuredAction.async { implicit request =>
    Future(Ok(views.html.goals.confirm(request.identity.userID)))
  }

  def listGoals(userID: String = UUID.randomUUID.toString) = Action.async { implicit request =>
    // sort by descending "challengers_num"
    // input user_goalForm in parameter goal_id -> goal._id user_id -> request.identity.userId
    val uuid = UUID.randomUUID
    println(uuid)
    goalRepo.list().map {
      goals =>
        Ok(views.html.goals.index(goals, userID, uuid.toString, user_goalForm))
    }
  }

  def createGoal = Action.async(parse.json) {
    _.body.validate[Goal].map { goal =>
      goalRepo.create(goal).map { _ =>
        Created
      }
    }.getOrElse(Future.successful(BadRequest("Invalid format")))
  }

  val testGoal: Seq[Goal] = Seq(Goal(goalID = "test", name = "test", learning_time = 1000, challengers_num = 0))

  def saveUserGoal = Action {
    implicit request =>
      user_goalForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.goals.index(testGoal, "test", UUID.randomUUID().toString, formWithErrors)),
        userGoal => {
          collection.flatMap(_.insert(userGoal))
          // use Casbah Find method
          val goalQuery = MongoDBObject("goalID" -> userGoal.goal_id)
          val goal = collGoal findOne goalQuery
          val goalObj = grater[Goal].asObject(goal.get) // .get is so important
          println(userGoal.user_id)
          val userQuery = MongoDBObject("userId" -> userGoal.user_id)
          val user = collUser findOne userQuery
          val userObj = grater[User].asObject(user.get)

          userRepo.updateUserGoal(userGoal.user_id, goalObj, userObj)

          // get "learning_time"
          //          val learning_time = goal.get("learning_time").asInstanceOf[Double]
          //          println(learning_time)
          //          usersGoalRepo.updateLearningTime(userGoal.usersGoalID, learning_time, userGoal)

          Ok(views.html.goals.test())
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

  def testUpdate(id: String, leaning_time: Double, users_goal: User_goals) = Action.async {
    collection.flatMap(_.update(Json.obj("usersGoalID" -> id), Json.obj("$set" -> Json.obj(
      "usersGoalID" -> users_goal.usersGoalID,
      "user_id" -> users_goal.user_id,
      "goal_id" -> users_goal.goal_id,
      "learning_time" -> leaning_time
    )))).map { uwr =>
      if (uwr.ok && uwr.n == 1) {
        Ok("success")
      } else {
        Ok("fail")
      }
    }.recover {
      case t: Throwable =>
        Ok("error")
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

  def updateUsersGoal(id: String, leaning_time: Double) = Action.async(parse.json) { req =>
    req.body.validate[User_goals].map { usersGoal =>
      println("hi")
      usersGoalRepo.update(id, leaning_time, usersGoal).map {
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