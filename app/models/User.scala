package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import com.mongodb.DBObject
import play.api.libs.functional.syntax.unlift
import play.api.libs.functional.syntax._
import models.daos.ModelMapper
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import scala.concurrent._
/**
 * The user object.
 *
 * @param userID The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param firstName Maybe the first name of the authenticated user.
 * @param lastName Maybe the last name of the authenticated user.
 * @param fullName Maybe the full name of the authenticated user.
 * @param email Maybe the email of the authenticated provider.
 * @param goal List of goals the user has.
 * @param avatarURL Maybe the avatar URL of the authenticated provider.
 * @param activated Indicates that the user has activated its registration.
 */
case class User(
    userID: String = UUID.randomUUID.toString,
    loginInfo: LoginInfo,
    firstName: Option[String],
    lastName: Option[String],
    fullName: Option[String],
    email: Option[String],
    avatarURL: Option[String],
    activated: Boolean,
    goal: Option[Goal]
) extends Identity {

  /**
   * Tries to construct a name.
   *
   * @return Maybe a name.
   */
  def name = fullName.orElse {
    firstName -> lastName match {
      case (Some(f), Some(l)) => Some(f + " " + l)
      case (Some(f), None) => Some(f)
      case (None, Some(l)) => Some(l)
      case _ => None
    }
  }
}

object User {
  import play.api.libs.json._
  implicit def optionFormat[T: Format]: Format[Option[T]] = new Format[Option[T]] {
    override def reads(json: JsValue): JsResult[Option[T]] = json.validateOpt[T]

    override def writes(o: Option[T]): JsValue = o match {
      case Some(t) ⇒ implicitly[Writes[T]].writes(t)
      case None ⇒ JsNull
    }
  }

  implicit val userFormat: OFormat[User] = (
    (JsPath \ "userId").format[String] and
    (JsPath \ "loginInfo").format[LoginInfo] and
    (JsPath \ "firstName").format[Option[String]] and
    (JsPath \ "lastName").format[Option[String]] and
    (JsPath \ "fullName").format[Option[String]] and
    (JsPath \ "email").format[Option[String]] and
    (JsPath \ "avatarURL").format[Option[String]] and
    (JsPath \ "activated").format[Boolean] and
    (JsPath \ "goal").format[Option[Goal]]
  )(User.apply, unlift(User.unapply))

  //  implicit val jsonFormat = Json.format[User]

  //  def toDBObject(user: User): DBObject = {
  //    DBObject(
  //      "userID" -> user.userID,
  //      "loginInfo" -> user.loginInfo,
  //      "firstName" -> user.firstName,
  //      "lastName" -> user.lastName,
  //      "fullName" -> user.fullName,
  //      "email" -> user.email,
  //      "avatarURL" -> user.avatarURL,
  //      "activated" -> user.activated,
  //      "goal" -> user.goal
  //    )
  //  }
  //  def toModel(obj: DBObject): Option[User] = {
  //    for {
  //      userID   <- obj.as[String]("userID")
  //      loginInfo  <- obj.as[LoginInfo]("loginInfo")
  //      firstName <- obj.as[String]("firstName")
  //      lastName <- obj.as[String]("lastName")
  //      fullName <- obj.as[String]("fullName")
  //      email <- obj.as[String]("email")
  //      avatarURL <- obj.as[String]("avatarURL")
  //      activated <- obj.as[Boolean]("activated")
  //      goal <- obj.as[Goal]("goal")
  //    } yield User(userID, loginInfo, firstName, lastName, fullName, email, avatarURL, activated, goal)
  //  }
}
