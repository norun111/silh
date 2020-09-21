package models.services

import models.UsersGoal
import scala.concurrent.Future

trait UsersGoalService {
  def save(usersGoal: UsersGoal): Future[UsersGoal]
}
