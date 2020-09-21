package models.daos

import models.UsersGoal
import scala.concurrent.Future

trait UsersGoalDAO {
  def save(usersGoal: UsersGoal): Future[UsersGoal]
}
