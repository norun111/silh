package models

import java.util.UUID
import reactivemongo.bson.BSONObjectID

case class User_goals(
                 user_id: String = UUID.randomUUID.toString,
                 goal_id: String = Option[BSONObjectID],
                 learning_time: Int
               )

object User_goals {
  import play.api.libs.json._

  implicit val goalFormat: OFormat[User_goals] = Json.format[User_goals]
}
