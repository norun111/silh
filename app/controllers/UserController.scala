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

  def show(id: String) = Action {
    val dateTime = new DateTime()
    // dateTime: org.joda.time.DateTime = 2014-10-30T09:30:11.634Z

    val dateString = DateTimeFormat.forPattern("HH").print(dateTime.withZone(DateTimeZone.UTC))
    // yyyy-MM-dd HH:mm:ss dateString: String = 2014-10-30 09:30:11
    val nowTime = dateString.toInt

    var greeting = ""
    if (nowTime >= 4 && nowTime < 10)
      greeting = "Good Morning"
    else if (nowTime >= 10 && nowTime < 18)
      greeting = "Good Afternoon"
    else if (nowTime >= 18 && nowTime < 23 || nowTime >= 0)
      greeting = "Good Evening"

    println(greeting)

    Ok(views.html.users.show())
  }
}