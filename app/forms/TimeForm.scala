package forms

import play.api.data.Forms.{ mapping, of }
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats.doubleFormat

case class TimeForm(
  sTime: Int,
  wTime: Int,
  oTime: Int
)

object TimeForm {
  var timeForm = Form(
    mapping(
      "sTime" -> number,
      "wTime" -> number,
      "oTime" -> number
    )(TimeForm.apply)(TimeForm.unapply)
  )
}