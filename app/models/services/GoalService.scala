package models.services

import models.Goal
import scala.concurrent.Future

trait GoalService {
  def save(goal: Goal): Future[Goal]
}
