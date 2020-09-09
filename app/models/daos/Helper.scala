package models.daos

import java.util.concurrent.TimeUnit

import org.mongodb.scala.{ Completed, Document, Observable }
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import com.mongodb.casbah.Imports._

object Helpers {
  implicit class DocumentObservable[C](val observable: Observable[Document]) extends ImplicitObservable[Document]

  implicit class CompletedObservable[C](val observable: Observable[Completed]) extends ImplicitObservable[Completed]

  trait ImplicitObservable[C] {
    val observable: Observable[C]

    def results: Seq[C] = Await.result(observable.toFuture(), Duration(10, TimeUnit.SECONDS))
  }
}

class DBObjectHelper(underlying: DBObject) {
  def asString(key: String) = underlying.as[String](key)

  def asDouble(key: String) = underlying.as[Double](key)

  def asInt(key: String) = underlying.as[Int](key)

  def asList[A](key: String) =
    (List() ++ underlying(key).asInstanceOf[BasicDBList]) map { _.asInstanceOf[A] }

  def asDoubleList(key: String) = asList[Double](key)
}

object DBObjectHelper {
  implicit def toDBObjectHelper(obj: DBObject) = new DBObjectHelper(obj)
}