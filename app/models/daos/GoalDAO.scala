package models.daos

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import models._
import reactivemongo.api._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

object GoalDAO extends Controller with MongoController {
  def collection: JSONCollection = db.collection[JSONCollection]("goal")
}
