package controllers.admin

import controllers.authentication.Secured
import play.api.mvc._

class AdminReportController extends Controller with Secured{

  def list() = withAuth { user => implicit request =>

    Ok(views.html.admin.report.list())
  }

  def detail() = withAuth { user => implicit request =>

    Ok(views.html.admin.report.detail())
  }
}