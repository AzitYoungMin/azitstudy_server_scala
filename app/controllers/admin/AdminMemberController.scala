package controllers.admin

import controllers.authentication.Secured
import play.api.Logger
import play.api.mvc._
import services._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class AdminMemberController extends Controller with Secured {

  var pageSize = Properties.PAGE_SIZE

  def memberService = new MemberService

  def mentorsService = new MentorsService

  def tsMatchingService = new TSMatchingService

  def usersService = new UsersService

  def examService = new ExamService

  def teachersService = new TeachersService

  //회원목록
  def list(p: Int, searchType: String, keyword: String, member_type: List[Int], withdrawal: Int) = withAuth { user => implicit request =>
    Logger.info("[Admin index] request" + searchType + " " + keyword)

    val startIndex = (p - 1) * pageSize

    //검색 키워드 지정
    var withdrawalBoolean = true
    if (withdrawal == -1) withdrawalBoolean = false
    var email = ""
    var name = ""
    var school = ""
    searchType match {
      case "email" => email = keyword
      case "name" => name = keyword
      case "school" => school = keyword
    }
    val list = Await.result(usersService.getUsersForList(member_type, email, name, school, withdrawalBoolean, startIndex, pageSize), Duration.Inf)
    val totalSize = Await.result(usersService.countUsersForList(member_type, email, name, school, withdrawalBoolean), Duration.Inf)

    Ok(views.html.admin.member.management(totalSize, list, p, pageSize, keyword, searchType, member_type, withdrawal))
  }

  //회원승인
  def approval(p: Int, types: String, key: String, memberType: List[Int], approval: Int, accept: Int, deny: Int) = withAuth { user => implicit request =>
    Logger.info("[Admin index] request" + types + " " + key+ " " + memberType.toString())
    val startIndex = (p - 1) * pageSize

    //검색 키워드 지정
    var email = ""
    var name = ""
    types match {
      case "email" => email = key
      case "name" => name = key
    }
    var mTypes = memberType
    if(mTypes.isEmpty) mTypes = List(3,4)

    if(accept != 0){ //승인 요청 처리
      Await.result(usersService.accept(accept), Duration.Inf)
      val mentor = Await.result(mentorsService.findById(accept), Duration.Inf)
      if(mentor.isDefined){
        Await.result(mentorsService.updateApproval(accept), Duration.Inf)
      }else{
        val teacher = Await.result(teachersService.findById(accept), Duration.Inf)
        if(teacher.isDefined){
          Await.result(teachersService.updateApproval(accept), Duration.Inf)
        }
      }
    }
    if(deny != 0){ //불승인 요청 처리
      Await.result(usersService.deny(deny), Duration.Inf)
    }

    val list = Await.result(usersService.getUsersForApproval(mTypes, email, name, approval, startIndex, pageSize), Duration.Inf)
    val totalElement = Await.result(usersService.countUsersForApproval(mTypes, email, name, approval), Duration.Inf)
    Ok(views.html.admin.member.approval(totalElement, list, p, pageSize, key, types, memberType, approval))
  }

  //회원탈퇴
  def withdrawal(p: Int, types: String, key: String, id: Int) = withAuth { user => implicit request =>

    if(id != 0){ //탈퇴 처리
      Await.result(usersService.approvalWithdraw(id), Duration.Inf)
    }

    val startIndex = (p - 1) * pageSize

    //검색 키워드 지정
    var email = ""
    var name = ""
    types match {
      case "email" => email = key
      case "name" => name = key
    }

    val list = Await.result(usersService.getUsersForWithdraw(email, name, startIndex, pageSize), Duration.Inf)
    val totalElement = Await.result(usersService.countUsersForWithdraw(email, name), Duration.Inf)

    Ok(views.html.admin.member.withdrawal(totalElement, list, p, pageSize, key, types))
  }

  //선생님 학생 추가
  def teacherStudent(p: Int, types: String, key: String, studentId: Int, teacherId: Int, approvalType: Int) = withAuth { user => implicit request =>

    val startIndex = (p - 1) * pageSize

    if(approvalType == 1){  //승인
      Await.result(tsMatchingService.updateApproval(teacherId, studentId, true, 1), Duration.Inf)
    }else if(approvalType == 2){ //불승인
      Await.result(tsMatchingService.updateApproval(teacherId, studentId, false, 2), Duration.Inf)
    }else if(approvalType == 3){ //삭제
      Await.result(tsMatchingService.delete(teacherId, studentId), Duration.Inf)
    }

    //검색 키워드 지정
    var teacherEmail = ""
    var teacherName = ""
    var studentEmail = ""
    var studentName = ""
    types match {
      case "teacherEmail" => teacherEmail = key
      case "teacherName" => teacherName = key
      case "studentEmail" => studentEmail = key
      case "studentName" => studentName = key
    }

    val list = Await.result(tsMatchingService.getUnreceivedMatching(teacherName, teacherEmail, studentName, studentEmail, startIndex, pageSize), Duration.Inf)
    val totalElement = Await.result(tsMatchingService.countUnreceivedMatching(teacherName, teacherEmail, studentName, studentEmail), Duration.Inf)

    Ok(views.html.admin.member.teacher_student(totalElement, list, p, pageSize, key, types))
  }

}