package models.daos

import org.mongodb.scala.{ Document, MongoClient, MongoCollection, MongoDatabase }
import Helpers._
import models.Goal

object GoalDAO {
  val mongoClient: MongoClient = MongoClient()

  // Databaseを取得する
  val database: MongoDatabase = mongoClient.getDatabase("silhouette")

  // Collectionを取得する
  val goals: MongoCollection[Document] = database.getCollection("goal")
}
