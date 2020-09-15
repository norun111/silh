package models

import java.util.UUID

import reactivemongo.bson.BSONObjectID
import play.api.libs.functional.syntax.unlift
import play.api.libs.functional.syntax._

import scala.util.Try

case class UsersGoal(
  usersGoalID: String = UUID.randomUUID.toString,
  user_id: String,
  goal_id: String,
  learning_time: Double
)

object UsersGoal {
  import play.api.libs.json._
  implicit object BSONObjectIDFormat extends Format[BSONObjectID] {
    def writes(objectId: BSONObjectID): JsValue = JsString(objectId.toString())
    def reads(json: JsValue): JsResult[BSONObjectID] = json match {
      case JsString(x) => {
        val maybeOID: Try[BSONObjectID] = BSONObjectID.parse(x)
        if (maybeOID.isSuccess) JsSuccess(maybeOID.get) else {
          JsError("Expected BSONObjectID as JsString")
        }
      }
      case _ => JsError("Expected BSONObjectID as JsString")
    }
  }

  implicit val goalFormat: OFormat[UsersGoal] = (
    (JsPath \ "usersGoalID").format[String] and
    (JsPath \ "user_id").format[String] and
    (JsPath \ "goal_id").format[String] and
    (JsPath \ "learning_time").format[Double]
  )(UsersGoal.apply, unlift(UsersGoal.unapply))

  //  implicit val goalFormat: OFormat[User_goals] = Json.format[User_goals]
}
