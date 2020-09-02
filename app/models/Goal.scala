package models

import play.api.libs.json._

case class Goal(
  name: String,
  learning_time: Int
)

object Goal {
  implicit val jsonFormat = Json.format[Goal]
}

