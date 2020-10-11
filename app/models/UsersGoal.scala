package models

import java.util.UUID

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import reactivemongo.bson.BSONObjectID
import play.api.libs.functional.syntax.unlift
import play.api.libs.functional.syntax._

import scala.util.Try

case class UsersGoal(
  usersGoalID: String = UUID.randomUUID.toString,
  user_id: String,
  goal_id: String,
  stack_time: Double,
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

  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss"

  val jodaDateReads = Reads[DateTime](js =>
    js.validate[String].map[DateTime](dtString =>
      DateTime.parse(dtString, DateTimeFormat.forPattern(dateFormat))))

  val jodaDateWrites: Writes[DateTime] = new Writes[DateTime] {
    def writes(d: DateTime): JsValue = JsString(d.toString())
  }

  //  val usersGoalReads: Reads[UsersGoal] = (
  //    (JsPath \ "usersGoalID").read[String] and
  //    (JsPath \ "user_id").read[String] and
  //    (JsPath \ "goal_id").read[String] and
  //    (JsPath \ "stack_time").read[Double] and
  //    (JsPath \ "learning_time").read[Double] and
  //    (JsPath \ "created_at").read[DateTime](jodaDateReads)
  //  )(UsersGoal.apply _)
  //
  //  val usersGoalWrites: OWrites[UsersGoal] = (
  //    (JsPath \ "usersGoalID").write[String] and
  //    (JsPath \ "user_id").write[String] and
  //    (JsPath \ "goal_id").write[String] and
  //    (JsPath \ "stack_time").write[Double] and
  //    (JsPath \ "learning_time").write[Double] and
  //    (JsPath \ "created_at").write[DateTime](jodaDateWrites)
  //  )(unlift(UsersGoal.unapply))

  //  implicit val goalFormat: OFormat[UsersGoal] = (
  //    (JsPath \ "usersGoalID").format[String] and
  //    (JsPath \ "user_id").format[String] and
  //    (JsPath \ "goal_id").format[String] and
  //    (JsPath \ "stack_time").format[Double] and
  //    (JsPath \ "learning_time").format[Double] and
  //    (JsPath \ "created_at").format[DateTime](jodaDateWrites)(jodaDateReads)
  //  )(UsersGoal.apply, unlift(UsersGoal.unapply))

  implicit val goalFormat: OFormat[UsersGoal] = Json.format[UsersGoal]
}
