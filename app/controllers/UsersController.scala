package controllers

import java.util.UUID

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
import forms.UsersGoalForm.form
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

class UsersController @Inject() (
    val reactiveMongoApi: ReactiveMongoApi,
    val messagesApi: MessagesApi,
    ec: ExecutionContext,
    silhouette: Silhouette[DefaultEnv],
    userRepo: UserRepository,
    userService: UserService,
    userDAO: UserDAO,
    implicit val webJarAssets: WebJarAssets
) extends Controller with I18nSupport with MongoController with ReactiveMongoComponents {

}