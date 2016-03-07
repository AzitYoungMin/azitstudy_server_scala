package controllers.user

import play.api.mvc._
import views.html

object MainController extends Controller{



  def index = Action {
    Ok(html.user.index())
  }

}