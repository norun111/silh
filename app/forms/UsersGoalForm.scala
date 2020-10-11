package forms

import models.UsersGoal
import play.api.data.Forms.{ mapping, of }
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats.doubleFormat

object UsersGoalForm {

  var usersGoalForm = Form(
    mapping(
      "usersGoalID" -> text,
      "user_id" -> text,
      "goal_id" -> text,
      "stack_time" -> of(doubleFormat),
      "note" -> text,
      "learning_time" -> of(doubleFormat),
      "created_at" -> text
    )(UsersGoal.apply)(UsersGoal.unapply)
  )

}
