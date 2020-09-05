package repositories

import javax.inject.Inject
import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.ExecutionContext

class Users_goalRepository @Inject() (
    implicit
    ec: ExecutionContext,
    reactiveMongoApi: ReactiveMongoApi
) {

}