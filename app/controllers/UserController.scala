package controllers

import java.util.{ Date, Locale, UUID }

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
    userService: UserService,
    userDAO: UserDAO,
    implicit val webJarAssets: WebJarAssets
) extends Controller with I18nSupport with MongoController with ReactiveMongoComponents {

  def show(id: String) = silhouette.SecuredAction.async { implicit request =>
    val dateTime = "%tY/%<tm/%<td %<tR" format new Date
    println(dateTime)
    val dateSplit = dateTime.split(" ")
    val timeSplit = dateSplit(1).split(":")
    val hour = timeSplit(0)
    println(hour)
    val nowTime = hour.toInt

    var greeting = ""
    if (nowTime >= 4 && nowTime < 10)
      greeting = "Good Morning"
    else if (nowTime >= 10 && nowTime < 18)
      greeting = "Good Afternoon"
    else if (nowTime >= 18 && nowTime < 23 || nowTime >= 0)
      greeting = "Good Evening"

    println(greeting)
    userService.retrieve(request.identity.userID).flatMap {
      case Some(user) =>
        Future.successful(Ok(views.html.users.show(user)))
      case None =>
        Future.successful(Redirect(routes.SignInController.view()).flashing("error" -> Messages("invalid.activation.link")))
    }

  }
}