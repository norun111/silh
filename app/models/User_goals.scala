package models

import java.util.UUID

import reactivemongo.bson.BSONObjectID

import scala.util.Try

case class User_goals(
  user_id: String = UUID.randomUUID.toString,
  goal_id: Option[BSONObjectID],
  learning_time: Int
)

object User_goals {
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

  implicit val goalFormat: OFormat[User_goals] = Json.format[User_goals]
}
