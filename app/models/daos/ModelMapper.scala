package models.daos

import com.mongodb.DBObject

trait ModelMapper[A] {
  // Model -> DBObject
  def toDBObject(model: A): DBObject
  // DBObject -> Option[Model]
  def toModel(obj: DBObject): Option[A]
}