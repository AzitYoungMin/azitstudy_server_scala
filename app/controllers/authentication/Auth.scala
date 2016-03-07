package controllers.authentication

import controllers.api.helpers.CommonHelper
import play.api.Logger
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import services.UsersService


object Auth extends Controller {

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => check(email, password)
    })
  )

  //form check
  def check(username: String, password: String) = {
    def users = new UsersService
    users.authenticate(username, password) match {
      case Some(user) => {
        Logger.info("[Admin Login] success")
        true
      }
      case None => {
        Logger.info("[Admin Login] fail")
        false
      }
    }
  }

  //로그인 페이지
  def login = Action { implicit request =>
    Ok(views.html.admin.login(loginForm))
  }

  //로그인
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.admin.login(formWithErrors)),
      user => Redirect("/admin/member").withSession(Security.username -> user._1)
    )
  }

  //로그아웃
  def logout = Action {
    Redirect(routes.Auth.login).withNewSession.flashing(
      "success" -> "You are now logged out."
    )
  }
}
