package models.daos

import java.util.concurrent.TimeUnit
import models.Goal

import org.mongodb.scala.{ Completed, Document, Observable }

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Helpers {

  implicit class DocumentObservable[C](val observable: Observable[Document]) extends ImplicitObservable[Document]

  implicit class CompletedObservable[C](val observable: Observable[Completed]) extends ImplicitObservable[Completed]

  trait ImplicitObservable[C] {
    val observable: Observable[C]

    def results: Seq[C] = Await.result(observable.toFuture(), Duration(10, TimeUnit.SECONDS))
  }
}