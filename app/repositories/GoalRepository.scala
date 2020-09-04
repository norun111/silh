package repositories

import javax.inject.Inject
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import models.Goal
import org.mongodb.scala.ReadPreference
import reactivemongo.bson.BSONDocument
import reactivemongo.api._

class GoalRepository @Inject()(
                              implicit ec: ExecutionContext,
                              reactiveMongoApi: ReactiveMongoApi
                              ) {
  private def collection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("goal"))

  def list(limit: Int = 100): Future[Seq[Goal]] =
    collection.flatMap(_.find(BSONDocument)
        .cursor[Goal](ReadPreference.primary())
        .collect[Seq](limit, Cursor.FailOnError[Seq[Goal]]())
    )

}
