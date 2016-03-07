package controllers

import play.api.mvc._
import services.UsersService

import scala.concurrent.ExecutionContext.Implicits.global


class Application extends Controller {

  def Users = new UsersService

  def index = Action.async {
    Users.all().map {case (users) => Ok(views.html.index(users)) }
  }

}