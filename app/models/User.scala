package models

import java.util.UUID

import play.api.libs.json._
import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }
import play.api.libs.functional.syntax.unlift
import reactivemongo.bson.BSONObjectID
import play.api.libs.functional.syntax._

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
    userID: UUID,
    loginInfo: LoginInfo,
    firstName: Option[String],
    lastName: Option[String],
    fullName: Option[String],
    email: Option[String],
    avatarURL: Option[String],
    activated: Boolean,
    goal: List[Goal]
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
    (JsPath \ "userId").format[UUID] and
    (JsPath \ "loginInfo").format[LoginInfo] and
    (JsPath \ "firstName").format[Option[String]] and
    (JsPath \ "lastName").format[Option[String]] and
    (JsPath \ "fullName").format[Option[String]] and
    (JsPath \ "email").format[Option[String]] and
    (JsPath \ "avatarURL").format[Option[String]] and
    (JsPath \ "activated").format[Boolean] and
    (JsPath \ "goal").format[List[Goal]]
  )(User.apply, unlift(User.unapply))

  //  implicit val jsonFormat = Json.format[User]
}
