package models.services

import javax.inject.Inject
import models.Goal
import models.daos.GoalDAO

class GoalServiceImpl @Inject() (goalDAO: GoalDAO) extends GoalService {
  def save(goal: Goal) = goalDAO.save(goal)
}
