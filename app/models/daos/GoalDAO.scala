package models.daos

import models.Goal

import scala.concurrent.Future

trait GoalDAO {
  def save(goal: Goal): Future[Goal]
}
