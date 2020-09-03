package models

import play.api.libs.json._

case class Goal(
  name: String,
  learning_time: Int,
  challengers_num: Int
)

object Goal {
  implicit val jsonFormat = Json.format[Goal]
}

