package models

import play.api.libs.functional.syntax.unlift
import play.api.libs.json._
import reactivemongo.bson.BSONObjectID
//import reactivemongo.play.json.compat._
import reactivemongo.play.json.BSONFormats
//import reactivemongo.bson.BSONObjectID
//import reactivemongo.bson._
import play.modules.reactivemongo.json._, ImplicitBSONHandlers._
import scala.util.Try

case class Goal(
  id: String = Option[BSONObjectID],
  name: String,
  learning_time: Int,
  challengers_num: Int
)

object Goal {
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
  implicit val goalFormat: OFormat[Goal] = Json.format[Goal]
}

