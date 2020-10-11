package controllers

import javax.inject.Inject
import com.mohiva.play.silhouette.api.{ LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import models.Goal
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.mvc.Controller
import repositories.GoalRepository
import utils.auth.DefaultEnv

import scala.concurrent.{ ExecutionContext, Future }

/**
 * The basic application controller.
 *
 * @param messagesApi The Play messages API.
 * @param silhouette The Silhouette stack.
 * @param socialProviderRegistry The social provider registry.
 * @param webJarAssets The webjar assets implementation.
 */
class ApplicationController @Inject() (
  val messagesApi: MessagesApi,
  goalRepo: GoalRepository,
  implicit val ec: ExecutionContext,
  silhouette: Silhouette[DefaultEnv],
  socialProviderRegistry: SocialProviderRegistry,
  implicit val webJarAssets: WebJarAssets
)
    extends Controller with I18nSupport {
  /**
   * Handles the index action.
   *
   * @return The result to display.
   *
   *         redirect goal_list
   *         if goal.length > 0 redirect user_show
   *         else views.html.goals.index(goals, request.identity) processing register goal
   */
  def index = silhouette.SecuredAction.async { implicit request =>
    if (request.identity.goal == Nil) {
//      ログインユーザーが目標を設定していなかったら登録ページにリダイレクト
      Future.successful(Redirect(routes.GoalController.listGoals(request.identity.userID)))
    } else {
//      ログインユーザーが目標を設定していたらshowページにリダイレクト
      println(request.identity)
      Future(Redirect(routes.UserController.show(request.identity.userID)))
    }
  }

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut = silhouette.SecuredAction.async { implicit request =>
    val result = Redirect(routes.ApplicationController.index())
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }
}