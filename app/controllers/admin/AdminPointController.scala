package controllers.admin

import java.util.Calendar

import common.PushHelper
import controllers.api.StaticValues
import controllers.authentication.Secured
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._
import play.api.libs.json.Json
import play.api.mvc._
import services._
import tables.Tables.{PostingsRow, SaveHistoryRow}

import scala.collection.immutable.HashMap
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class AdminPointController extends Controller with Secured{

  var pageSize = Properties.PAGE_SIZE

  def saveHistoryService = new SaveHistoryService

  def refundHistoryService = new RefundHistoryService

  def mentorsService = new MentorsService

  def changeChoiceHistoryService = new ChangeChoiceHistoryService

  def postingsService = new PostingsService

  //포인트 관리 페이지
  def manage(page: Int, year: Int, month: Int, searchType: String, keyword: String) = withAuth { user => implicit request =>
    val startIndex = (page - 1) * pageSize

    //검색 기간 지정
    val cal = Calendar.getInstance()
    var yearValue = year
    var monthValue = month
    if(year == 0 && month == 0){
      yearValue = cal.get(Calendar.YEAR)
      monthValue = cal.get(Calendar.MONTH) + 1
    }
    cal.clear();
    cal.set(Calendar.MONTH, monthValue - 1);
    cal.set(Calendar.YEAR, yearValue);
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH))
    val startDate: java.sql.Date = new java.sql.Date(new java.util.Date(cal.getTimeInMillis()).getTime)
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
    val endDate: java.sql.Date = new java.sql.Date(new java.util.Date(cal.getTimeInMillis()).getTime)

    val list = Await.result(saveHistoryService.getSaveHistoryForList(searchType, keyword, startDate, endDate, startIndex, pageSize), Duration.Inf)
    val totalSize = Await.result(saveHistoryService.countSaveHistoryForList(searchType, keyword, startDate, endDate), Duration.Inf)

    Ok(views.html.admin.point.manage(page, pageSize, totalSize, list, yearValue, monthValue, searchType, keyword))
  }

  //포인트 환급 페이지
  def refund(page: Int, year: Int, month: Int, searchType: String, keyword: String) = withAuth { user => implicit request =>
    val startIndex = (page - 1) * pageSize
    //검색 기간 지정
    val cal = Calendar.getInstance()
    var yearValue = year
    var monthValue = month
    if(year == 0 && month == 0){
      yearValue = cal.get(Calendar.YEAR)
      monthValue = cal.get(Calendar.MONTH) + 1
    }
    cal.clear();
    cal.set(Calendar.MONTH, monthValue - 1);
    cal.set(Calendar.YEAR, yearValue);
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH))
    val startDate: java.sql.Date = new java.sql.Date(new java.util.Date(cal.getTimeInMillis()).getTime)
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
    val endDate: java.sql.Date = new java.sql.Date(new java.util.Date(cal.getTimeInMillis()).getTime)

    val list = Await.result(refundHistoryService.getRefundHistoryForList(searchType, keyword, startDate, endDate, startIndex, pageSize), Duration.Inf)
    val totalSize = Await.result(refundHistoryService.countRefundHistoryForList(searchType, keyword, startDate, endDate), Duration.Inf)
    val totalAmount = Await.result(refundHistoryService.getTotalAmount(startDate, endDate), Duration.Inf)
    val formatter = java.text.NumberFormat.getIntegerInstance

    Ok(views.html.admin.point.refund(page, pageSize, totalSize, list, yearValue, monthValue, searchType, keyword, formatter.format(totalAmount.getOrElse(0))))
  }

  //포인트 집행완료 페이지
  def complete(page: Int, year: Int, month: Int, searchType: String, keyword: String) = withAuth { user => implicit request =>
    val startIndex = (page - 1) * pageSize
    //검색 기간 지정
    val cal = Calendar.getInstance()
    var yearValue = year
    var monthValue = month
    if(year == 0 && month == 0){
      yearValue = cal.get(Calendar.YEAR)
      monthValue = cal.get(Calendar.MONTH) + 1
    }
    cal.clear();
    cal.set(Calendar.MONTH, monthValue - 1);
    cal.set(Calendar.YEAR, yearValue);
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH))
    val startDate: java.sql.Date = new java.sql.Date(new java.util.Date(cal.getTimeInMillis()).getTime)
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
    val endDate: java.sql.Date = new java.sql.Date(new java.util.Date(cal.getTimeInMillis()).getTime)

    val list = Await.result(refundHistoryService.getRefundCompleteHistory(searchType, keyword, startDate, endDate, startIndex, pageSize), Duration.Inf)
    val totalSize = Await.result(refundHistoryService.countRefundCompleteHistory(searchType, keyword, startDate, endDate), Duration.Inf)
    val totalAmount = Await.result(refundHistoryService.getTotalAmount(startDate, endDate), Duration.Inf)
    val formatter = java.text.NumberFormat.getIntegerInstance

    Ok(views.html.admin.point.complete(page, pageSize, totalSize, list, yearValue, monthValue, searchType, keyword, formatter.format(totalAmount.getOrElse(0))))
  }

  //포인트 채택변경 페이지
  def choice(page: Int, keyword: String, change: Int, cancel: Int) = withAuth { user => implicit request =>
    if(change != 0){ //채택변경 요청 처리
      changeChoice(change)
    }
    if(cancel != 0){ //취소 처리
      Await.result(changeChoiceHistoryService.updateIsCancel(cancel), Duration.Inf)
    }
    val startIndex = (page - 1) * pageSize

    val list = Await.result(changeChoiceHistoryService.getHistoryList(keyword, startIndex, pageSize), Duration.Inf)
    val totalSize = Await.result(changeChoiceHistoryService.countHistoryList(keyword), Duration.Inf)

    Ok(views.html.admin.point.choice(page, pageSize, totalSize, list, keyword))
  }

  //환급 승인
  def refundApproval() = withAuth { user => implicit request =>
    val bodyJson = request.body.asJson.get
    val id = (bodyJson \ "id").as[String].toInt
    val history = Await.result(refundHistoryService.findById(id), Duration.Inf).get
    var mentor = Await.result(mentorsService.findById(history.mentorId.get), Duration.Inf).get
    //환급 처리
    val balance = mentor.point.get - history.amount.get
    mentor = mentor.copy(point = Option(balance))
    Await.result(mentorsService.update(mentor), Duration.Inf)
    val today = new java.sql.Date(new java.util.Date().getTime)
    //환급 기록 저장
    Await.result(refundHistoryService.update(history.copy(isApproval = Option(true), balance = Option(balance), updatedAt = Option(today))), Duration.Inf)
    //푸쉬 메세지 전송
    PushHelper.sendPushMessage(List(mentor.id), Properties.PUSH_MESSAGE_ID_MENTOR_REFUND, Properties.PUSH_CODE_MENTOR_POINT)

    Ok("success")
  }

  //채택 변경
  def changeChoice(id: Int) = {
    val history = Await.result(changeChoiceHistoryService.findById(id), Duration.Inf).get
    var answerPosting: PostingsRow = Await.result(postingsService.findById(history.choicedId.get), Duration.Inf).get
    var newAnswerPosting: PostingsRow = Await.result(postingsService.findById(history.newAnswerId.get), Duration.Inf).get
    var parentPosting: PostingsRow = Await.result(postingsService.findById(history.postingId.get), Duration.Inf).get

    answerPosting = answerPosting.copy(isChosen = Option(false))
    Await.result(postingsService.update(answerPosting), Duration.Inf)

    newAnswerPosting = newAnswerPosting.copy(isChosen = Option(true))
    Await.result(postingsService.update(newAnswerPosting), Duration.Inf)

    //기존 채택된 답변 작성자 멘토 포인트 차감
    var mentor = Await.result(mentorsService.findById(answerPosting.userId), Duration.Inf).get
    val balance = mentor.point.get - StaticValues.ANSWER_POINT
    mentor = mentor.copy(point = Option(balance))
    Await.result(mentorsService.update(mentor), Duration.Inf)
    val today = new java.sql.Date(new java.util.Date().getTime)
    Await.result(saveHistoryService.insert(SaveHistoryRow(id = 0, mentorId = Option(mentor.id), postingId = Option(parentPosting.id), balance = Option(balance), createdAt = Option(today), addedPoint = Option(-StaticValues.ANSWER_POINT))), Duration.Inf)
    //채택 취소 알림 푸쉬
    PushHelper.sendPushMessage(List(answerPosting.userId), Properties.PUSH_MESSAGE_ID_MENTOR_CHOICE_CANCEL, Properties.PUSH_CODE_MENTOR_ANSWER)

    //새로이 채택된 답변 작성자 멘토 포인트 추가
    var newMentor = Await.result(mentorsService.findById(newAnswerPosting.userId), Duration.Inf).get
    val newBalance = newMentor.point.get + StaticValues.ANSWER_POINT
    newMentor = newMentor.copy(point = Option(balance))
    Await.result(mentorsService.update(newMentor), Duration.Inf)
    Await.result(saveHistoryService.insert(SaveHistoryRow(id = 0, mentorId = Option(newMentor.id), postingId = Option(parentPosting.id), balance = Option(balance), createdAt = Option(today), addedPoint = Option(StaticValues.ANSWER_POINT))), Duration.Inf)

    //채택변경 요청 처리되었음 푸쉬 전송
    PushHelper.sendPushMessage(List(parentPosting.userId), Properties.PUSH_MESSAGE_ID_STUDENT_CHOICE, Properties.PUSH_CODE_STUDENT)
    Await.result(changeChoiceHistoryService.updateIsChanged(id), Duration.Inf)
  }
}