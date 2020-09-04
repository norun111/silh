package test

import org.mongodb.scala._

import test.Helpers._

object MongoDB {

  def main(args: Array[String]) {
    val mongoClient: MongoClient = MongoClient()

    // Databaseを取得する
    val database: MongoDatabase = mongoClient.getDatabase("silhouette")

    // Collectionを取得する
    val goals: MongoCollection[Document] = database.getCollection("goal")

    goals.find.results.foreach((goal => println(goal.toJson())))
  }
}