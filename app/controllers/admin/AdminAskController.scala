package controllers.admin

import controllers.authentication.Secured
import play.api.mvc._

class AdminAskController extends Controller with Secured{

  def list() = withAuth { user => implicit request =>

    Ok(views.html.admin.ask.list())
  }

  def detail() = withAuth { user => implicit request =>

    Ok(views.html.admin.ask.detail())
  }
}