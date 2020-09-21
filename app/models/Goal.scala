package models

import play.api.libs.functional.syntax.unlift
import reactivemongo.bson.BSONObjectID
import play.api.libs.functional.syntax._
import play.modules.reactivemongo.json._
import ImplicitBSONHandlers._

import scala.util.Try
import java.util.UUID

import com.novus.salat.Context

case class Goal(
  goalID: String,
  name: String,
  learning_time: Double,
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

  //      implicit val goalFormat: OFormat[Goal] = (
  //        (JsPath \ "_id" \ "$oid").format[BSONObjectID] and // Focus
  //        (JsPath \ "name").format[String] and
  //        (JsPath \ "learning_time").format[Int] and
  //        (JsPath \ "challengers_num").format[Int]
  //      )(Goal.apply, unlift(Goal.unapply))

  implicit val goalFormat: OFormat[Goal] = (
    (JsPath \ "goalID").format[String] and
    (JsPath \ "name").format[String] and
    (JsPath \ "learning_time").format[Double] and
    (JsPath \ "challengers_num").format[Int]
  )(Goal.apply, unlift(Goal.unapply))

  //    implicit val goalReads: Reads[Goal] = (
  //      (JsPath \ "_id").readNullable[BSONObjectID] and
  //        (JsPath \ "name").read[String] and
  //        (JsPath \ "leaning_time").read[Int] and
  //        (JsPath \ "challengers_num").read[Int] and
  //    )(Goal.apply _)
  //
  //    implicit val goalWrites: Writes[Goal] = (
  //      (JsPath \ "_id").write[BSONObjectID] and
  //        (JsPath \ "name").write[String] and
  //        (JsPath \ "leaning_time").write[Int] and
  //        (JsPath \ "challengers_num").write[Int] and
  //    )(unlift(Goal.unapply))

  //  implicit val goalFormat: OFormat[Goal] = Json.format[Goal]
}

