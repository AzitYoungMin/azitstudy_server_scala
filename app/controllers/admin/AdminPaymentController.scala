package controllers.admin

import controllers.authentication.Secured
import play.api.mvc._
import services.{ExamService, MemberService, MentorsService, TSMatchingService, UsersService}

class AdminPaymentController extends Controller with Secured{
  
  var perpage:Int = 2

  def memberService = new MemberService

  def mentorsService = new MentorsService

  def tsMatchingService = new TSMatchingService

  def usersService = new UsersService

  def examService = new ExamService

  
  def pay() = withAuth { user => implicit request =>

    Ok(views.html.admin.payment.pay())
  }

  def refund() = withAuth { user => implicit request =>

    Ok(views.html.admin.payment.refund())
  }

  def complete() = withAuth { user => implicit request =>

    Ok(views.html.admin.payment.complete())
  }


}