package models

import play.api.libs.json._
import reactivemongo.play.json._
import reactivemongo.bson.BSONObjectID

case class Goal(
  _id: Option[BSONObjectID],
  name: String,
  learning_time: Int,
  challengers_num: Int
)

object Goal {
  implicit val jsonFormat = Json.format[Goal]
}

