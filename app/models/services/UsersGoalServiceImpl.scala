package models.services

import javax.inject.Inject
import models.UsersGoal
import models.daos.{ UserDAO, UsersGoalDAO }

class UsersGoalServiceImpl @Inject() (usersGoalDAO: UsersGoalDAO) extends UsersGoalService {
  def save(usersGoal: UsersGoal) = usersGoalDAO.save(usersGoal)
}
