package controllers.admin

import java.sql.Timestamp
import java.util.Calendar

import caseClasses.{MentorForPushList, StudentForPushList}
import common.{SingletonSchedule, PushHelper}
import controllers.authentication.Secured
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import services._
import tables.Tables.MessagesRow

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class AdminPushController extends Controller with Secured {


  def studentsService = new StudentsService

  def usersService = new UsersService

  def messagesService = new MessagesService

  def mentorsService = new MentorsService

  def pushMessageService = new PushMessageService

  var pageSize = Properties.PAGE_SIZE

  //학생 자동 페이지
  def studentAuto() = withAuth { user => implicit request =>
    val list = Await.result(pushMessageService.findByType(Properties.USER_TYPE_STUDENT), Duration.Inf)
    var less = ""
    var more = ""
    var clinicReply = ""
    var mentoringReply = ""
    var report = ""
    var exam = ""
    var choice = ""
    var lessDay = "0,0,0,0,0,0,0"
    var lessHour = 1
    var lessMinute = 1
    var moreDay = "0,0,0,0,0,0,0"
    var moreHour = 1
    var moreMinute = 1
    //지정된 메세지 분류
    list.foreach(row =>
      row.id match{
        case Properties.PUSH_MESSAGE_ID_STUDENT_LESS => {
          less = row.message.get
          lessDay = row.day.get
          lessHour = row.hour.get
          lessMinute = row.minute.get
        }
        case Properties.PUSH_MESSAGE_ID_STUDENT_MORE => {
          more = row.message.get
          moreDay = row.day.get
          moreHour = row.hour.get
          moreMinute = row.minute.get
        }
        case Properties.PUSH_MESSAGE_ID_STUDENT_CLINIC_REPLY => clinicReply = row.message.get
        case Properties.PUSH_MESSAGE_ID_STUDENT_MENTORING_REPLY => mentoringReply = row.message.get
        case Properties.PUSH_MESSAGE_ID_STUDENT_REPORT => report = row.message.get
        case Properties.PUSH_MESSAGE_ID_STUDENT_EXAM => exam = row.message.get
        case Properties.PUSH_MESSAGE_ID_STUDENT_CHOICE => choice = row.message.get
      }
    )

    Ok(views.html.admin.push.student_auto(less, more, clinicReply, mentoringReply, report, exam, choice, lessDay, lessHour, lessMinute, moreDay, moreHour, moreMinute))
  }

  //학생 수동 페이지
  def studentManual(page: Int, totalGrade: List[Int], year: Int, school: String, fromYear: Int, fromMonth: Int, toYear: Int, toMonth: Int) = withAuth { user => implicit request =>

    val startIndex =  (page - 1) * pageSize
    var list: Seq[StudentForPushList] = null
    var totalSize = 0
    if(fromYear != 0 && fromMonth != 0 && toYear != 0 && toMonth != 0){
      val start = getStartTimestamp(fromYear, fromMonth)
      val end = getEndTimestamp(toYear, toMonth)
      list = Await.result(studentsService.getStudentsForPushByDate(totalGrade, year, school, start, end, startIndex, pageSize), Duration.Inf)
      totalSize = Await.result(studentsService.countStudentsForPushByDate(totalGrade, year, school, start, end), Duration.Inf)
    }else{
      list = Await.result(studentsService.getStudentsForPush(totalGrade, year, school, startIndex, pageSize), Duration.Inf)
      totalSize = Await.result(studentsService.countStudentsForPush(totalGrade, year, school), Duration.Inf)
    }

    Ok(views.html.admin.push.student_manual(list, page, pageSize, totalSize, totalGrade, year, school, fromYear, fromMonth, toYear, toMonth))
  }

  //검색시 가입시기 시작날짜
  def getStartTimestamp(year: Int, month: Int): Timestamp = {
    val timestamp = Timestamp.valueOf(year + "-" + month + "-01 00:00:00");
    timestamp
  }

  //검색시 가입시기 끝 날짜
  def getEndTimestamp(year: Int, month: Int): Timestamp = {
    val cal = Calendar.getInstance()
    cal.set(Calendar.MONTH, month - 1)
    cal.set(Calendar.YEAR, year)
    val lastDay = cal.getActualMaximum (Calendar.DAY_OF_MONTH);
    val timestamp = Timestamp.valueOf(year + "-" + month + "-" + lastDay + " 23:59:59");
    timestamp
  }

  //멘토 자동 페이지
  def mentorAuto() = withAuth { user => implicit request =>
    val list = Await.result(pushMessageService.findByType(Properties.USER_TYPE_MENTOR), Duration.Inf)
    var clinicQuestion = ""
    var clinicEvaluation = ""
    var mentoringReply = ""
    var mentoringEvaluation = ""
    var report = ""
    var choiceCancel = ""
    var refund = ""
    //지정된 메세지 분류
    list.foreach(row =>
      row.id match{
        case Properties.PUSH_MESSAGE_ID_MENTOR_CLINIC_QUESTION => clinicQuestion = row.message.get
        case Properties.PUSH_MESSAGE_ID_MENTOR_CLINIC_EVALUATION => clinicEvaluation = row.message.get
        case Properties.PUSH_MESSAGE_ID_MENTOR_MENTORING_REPLY => mentoringReply = row.message.get
        case Properties.PUSH_MESSAGE_ID_MENTOR_MENTORING_EVALUATION => mentoringEvaluation = row.message.get
        case Properties.PUSH_MESSAGE_ID_MENTOR_REPORT => report = row.message.get
        case Properties.PUSH_MESSAGE_ID_MENTOR_CHOICE_CANCEL => choiceCancel = row.message.get
        case Properties.PUSH_MESSAGE_ID_MENTOR_REFUND => refund = row.message.get
      }
    )

    Ok(views.html.admin.push.mentor_auto(clinicQuestion, clinicEvaluation, mentoringReply, mentoringEvaluation, report, choiceCancel, refund))
  }

  //멘토 수동 페이지
  def mentorManual(page: Int, university: String, major: String, year: Int, gender: String, subjects: List[Int]) = withAuth { user => implicit request =>
    val startIndex =  (page - 1) * pageSize
    var list: Seq[MentorForPushList] = null
    var totalSize = 0
    if(subjects.isEmpty){
      list = Await.result(mentorsService.getMentorsForPush(university, major, year, gender, startIndex, pageSize), Duration.Inf)
      totalSize = Await.result(mentorsService.countMentorsForPush(university, major, year, gender), Duration.Inf)
    }else{
      list = Await.result(mentorsService.getMentorsBySubjectsForPush(university, major, year, gender, subjects, startIndex, pageSize), Duration.Inf)
      totalSize = Await.result(mentorsService.countMentorsBySubjectsForPush(university, major, year, gender, subjects), Duration.Inf)
    }

    Ok(views.html.admin.push.mentor_manual(list, page, pageSize, totalSize, university, major, year, gender, subjects))
  }

  //수동 push 전송
  def manualPush() = withAuth { user => implicit request =>
    val bodyJson = request.body.asJson.get
    val message = (bodyJson \ "message").as[String]
    val ids = (bodyJson \ "ids").as[String]
    val code = (bodyJson \ "code").as[String]
    val idList: List[Int] = ids.split(",").map(_.trim.toInt).toList
    val optionTokens = Await.result(usersService.getTokenByIds(idList), Duration.Inf)
    val tokens = optionTokens.flatten
    val pushJson = PushHelper.getJsonString(tokens, "", message, code)
    val pushResult = PushHelper.sendPush(pushJson)
    var result = "success"
    if(pushResult.isError){
      result = "fail"
    }else{
      messageSend(idList, Properties.ADMIN_ID, "Azit", message)
    }
    Ok(result)
  }

  //push 전송 후 azit message 전송
  def messageSend(receiverIds: List[Int], senderId: Int, title: String, message: String) = {
    val date = new java.util.Date()
    val createdAt = new java.sql.Timestamp(date.getTime)
    receiverIds.foreach(receiverId =>
        Await.result(messagesService.insert(MessagesRow(id = 0, message = Option(message), senderId = senderId, receiverId = receiverId, createdAt = Option(createdAt), isNew = Option(true), title = Option(title))), Duration.Inf)
    )
  }

  //자동 push 메세지 업데이트
  def updateMessage() = withAuth { user => implicit request =>
    val bodyJson = request.body.asJson.get
    val message = (bodyJson \ "message").as[String]
    val id = (bodyJson \ "id").as[String].toInt
    val day = (bodyJson \ "day").as[String]
    val hour = (bodyJson \ "hour").as[String]
    val minute = (bodyJson \ "minute").as[String]
    val result = "success"
    println(day)
    Await.result(pushMessageService.updateMessage(id, message, day, hour.toInt, minute.toInt), Duration.Inf)
    Ok(result)
  }

  //자동 push 시작
  def pushStart() = withAuth { user => implicit request =>
    Logger.info("[API Common push start] request")
    val schedule = SingletonSchedule.getSchedule()
    schedule ! "start"
    Ok(Json.toJson(Map("result" -> "success")))
  }

  //자동 push 정지
  def pushStop() = withAuth { user => implicit request =>
    Logger.info("[API Common push stop] request")
    val schedule = SingletonSchedule.getSchedule()
    schedule ! "stop"
    Ok(Json.toJson(Map("result" -> "success")))
  }
}