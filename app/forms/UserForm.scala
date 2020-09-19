package forms

import com.mohiva.play.silhouette.api.LoginInfo
import models._
import play.api.data.Forms.{ mapping, of }
import play.api.data.Forms._
import play.api.data.format.Formats.doubleFormat
import play.api.data.Form

object UserForm {
  var userForm = Form(
    mapping(
      "userId" -> text,
      "loginInfo" -> ignored(LoginInfo("credentials", "sample@sample.com")),
      "firstName" -> optional(text),
      "lastName" -> optional(text),
      "fullName" -> optional(text),
      "email" -> optional(text),
      "avatarURL" -> optional(text),
      "activated" -> boolean,
      "goal" -> optional(mapping(
        "goalID" -> text,
        "name" -> text,
        "learning_time" -> of[Double],
        "challengers_num" -> number
      )(Goal.apply)(Goal.unapply)),
      "sTime" -> number,
      "wTime" -> number,
      "oTime" -> number
    )(User.apply)(User.unapply)
  )
}
