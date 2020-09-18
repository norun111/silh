package controllers

//Import ReactiveMongo plug-in
import com.mongodb.casbah.Imports.MongoConnection
import play.modules.reactivemongo.{ MongoController, ReactiveMongoApi, ReactiveMongoComponents }

//Import BSON-JSON conversions/collection
import com.mohiva.play.silhouette.api.Silhouette
import javax.inject._
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc._
import repositories._
import utils.auth.DefaultEnv
//import com.novus.salat.global._
import scala.concurrent.ExecutionContext

class UsersGoalController @Inject() (
    val reactiveMongoApi: ReactiveMongoApi,
    val messagesApi: MessagesApi,
    ec: ExecutionContext,
    silhouette: Silhouette[DefaultEnv],
    goalRepo: GoalRepository,
    userRepo: UserRepository,
    usersGoalRepo: UsersGoalRepository,
    implicit val webJarAssets: WebJarAssets
) extends Controller with I18nSupport with MongoController with ReactiveMongoComponents {

  def index = TODO

  def edit = TODO
}