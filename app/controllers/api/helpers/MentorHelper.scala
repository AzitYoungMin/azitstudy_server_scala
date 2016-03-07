package controllers.api.helpers

import java.util.Date

import common.PushHelper
import controllers.admin.Properties
import controllers.api.StaticValues
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._
import play.api.libs.json.{JsError, JsSuccess, JsValue}
import services._
import tables.Tables._

import scala.collection.immutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object MentorHelper {
  def mentorsService = new MentorsService

  def mentorSubjectsService = new MentorSubjectService

  def msMatchingService = new MSMatchingService

  def postingsService = new PostingsService

  def postingImagesService = new PostingImagesService

  def usersService = new UsersService

  def textbooksService = new TextbooksService

  def saveHistoryService = new SaveHistoryService

  def refundHistoryService = new RefundHistoryService

  def studentsService = new StudentsService

  def customTextbooksService = new CustomTextbooksService

  //멘토 생성
  def insert(mentor: MentorsRow): Option[MentorsRow] = {
    Await.result(mentorsService.insert(mentor), Duration.Inf)
    Await.result(mentorsService.findById(mentor.id), Duration.Inf)
  }

  //멘토 아이디로 조회
  def findById(id: Int): Option[MentorsRow] = Await.result(mentorsService.findById(id), Duration.Inf)

  //멘토 선택과목 조회
  def getSubjectsForProfile(id: Int): String = {
    var result = ""
    val rows = Await.result(mentorSubjectsService.findByMentorId(id), Duration.Inf)
    if (rows.isEmpty) {
      result
    } else {
      rows.foreach(row => result += row.subjectId + ",")
      result.substring(0, result.lastIndexOf(","))
    }
  }

  //멘토 프로필 업데이트
  def updateProfile(bodyJson: JsValue, image: String) = {
    val id = (bodyJson \ "user_id").as[String].toInt
    val existMentor = Await.result(mentorsService.findById(id), Duration.Inf).get
    val newMentor = existMentor.copy(university = Option((bodyJson \ "university").as[String]), introduce = Option((bodyJson \ "introduce").as[String]), major = Option((bodyJson \ "major").as[String]), nickname = Option((bodyJson \ "nickname").as[String]))
    Await.result(mentorsService.update(newMentor), Duration.Inf)

    val existUser = Await.result(usersService.findById(id), Duration.Inf).get
    val newUser = existUser.copy( gender = Option((bodyJson \ "gender").as[String]), profileImage = Option(image))
    Await.result(usersService.update(newUser), Duration.Inf)

    val existSubjects = getSubjectsForProfile(id).split(",")
    val newSubjects = (bodyJson \ "subjects").as[String].split(",")

    newSubjects.foreach(subject =>
      if (!existSubjects.contains(subject) && subject != "") {
        val mentorsSubject = new MentorSubjectsRow(newMentor.id, subject.toInt)
        Await.result(mentorSubjectsService.insert(mentorsSubject), Duration.Inf)
      }
    )
    existSubjects.foreach(subject =>
      if (!newSubjects.contains(subject) && subject != "") {
        Await.result(mentorSubjectsService.delete(newMentor.id, subject.toInt), Duration.Inf)
      }
    )
  }

  //멘토 승인여부 조회
  def isAuthenticated(id: Int): Boolean = {
    val mentor = findById(id).get
    if (mentor.isAuthenticated.get) true else false
  }

  //멘토별 등록된 학생 조회, 초기기획 내용
  def getStudents(bodyJson: JsValue): List[StudentsForList] = {
    val teacherId = (bodyJson \ "user_id").as[String].toInt
    val students = Await.result(msMatchingService.getStudentsForMentor(teacherId), Duration.Inf)
    students.toList
  }

  //답변 저장
  def answerSave(bodyJson: JsValue, image: String) = {
    val date = new Date()
    val createdAt =  new java.sql.Timestamp(date.getTime)
    val parentId = (bodyJson \ "posting_id").as[String].toInt
    var parentPosting: PostingsRow = Await.result(postingsService.findById(parentId), Duration.Inf).get
    val postingType = parentPosting.typeId
    val posting = PostingsRow(id = 0, userId = (bodyJson \ "user_id").as[String].toInt, parentId = Option(parentId), article = Option((bodyJson \ "answer").as[String]), typeId = postingType, createdAt = Option(createdAt), updatedAt = Option(createdAt), isAnswered = Option(false), numOfReply = Option(0), isChosen = Option(false), isDeleted = Option(false))
    val postingId = Await.result(postingsService.insert(posting), Duration.Inf)

    if(!image.equals("")){
      Await.result(postingImagesService.insert(PostingImagesRow(id = 0, postingId = postingId, imageUrl = Option(image))), Duration.Inf)
    }
    parentPosting = parentPosting.copy(isAnswered = Option(true), hasNewAnswer = Option(true))
    Await.result(postingsService.update(parentPosting), Duration.Inf)
    //답변이 달리 게시물에 작성자에게 푸쉬 전송
    parentPosting.typeId match{
      case StaticValues.MENTORING_TYPE_ID => PushHelper.sendPushMessage(List(parentPosting.userId), Properties.PUSH_MESSAGE_ID_STUDENT_MENTORING_REPLY, Properties.PUSH_CODE_STUDENT)
      case StaticValues.CLINIC_TYPE_ID => PushHelper.sendPushMessage(List(parentPosting.userId), Properties.PUSH_MESSAGE_ID_STUDENT_CLINIC_REPLY, Properties.PUSH_CODE_STUDENT)
    }
  }

  //답변 내역 조회
  def getPostings(bodyJson: JsValue): List[PostingsRowForList] = {
    val postingType = (bodyJson \ "posting_type").as[String].toInt
    val postingRows = Await.result(postingsService.findForMentor((bodyJson \ "user_id").as[String].toInt, postingType), Duration.Inf)
    var postingRowForList = new ListBuffer[PostingsRowForList]()
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    postingRows.foreach { row =>
      //게시물별 이미지 및 세부 정보 추가
      val postingImages = CommonHelper.getImagesByPostingId(row._1.id)
      var year:Option[Int] = Option(0)
      if (postingType.equals(StaticValues.CLINIC_TYPE_ID)) {
        year = Await.result(studentsService.getYearByStudentId(row._1.userId), Duration.Inf)
      }
      postingRowForList += PostingsRowForList(posting_id = Option(row._1.id), title = row._1.title, contents = row._1.article, year = year, writer = row._2.name, user_id = Option(row._1.userId), num_of_images = Option(postingImages.size), images = Option(postingImages), created_at = Option(format.format(row._1.createdAt.get)), num_of_reply = row._1.numOfReply, is_answered = row._1.isAnswered, textbook = row._1.title, article = row._1.article, profile_image = row._2.profileImage)
    }
    postingRowForList.toList
  }

  //포인트 적립 내역 조회
  def getSaveHistory(bodyJson: JsValue): Map[String, String] = {
    val mentorId = (bodyJson \ "user_id").as[String].toInt
    val saveHistory = Await.result(saveHistoryService.getSaveHistory(mentorId), Duration.Inf)
    implicit val formats = Serialization.formats(NoTypeHints)
    var result: Map[String, String] = new HashMap[String, String]
    result += ("result" -> "success")
    result += ("save_list" -> write(saveHistory))
    result += ("num_of_save" -> saveHistory.size.toString)
    result
  }

  //포인트 환급 내역 조회
  def getRefundHistory(bodyJson: JsValue): Map[String, String] = {
    val mentorId = (bodyJson \ "user_id").as[String].toInt
    val refundHistory = Await.result(refundHistoryService.getRefundHistory(mentorId), Duration.Inf)
    implicit val formats = Serialization.formats(NoTypeHints)
    var result: Map[String, String] = new HashMap[String, String]
    result += ("result" -> "success")
    result += ("refund_list" -> write(refundHistory))
    result += ("num_of_save" -> refundHistory.size.toString)
    result
  }

  //환급 신청
  def registerRefund(bodyJson: JsValue): Map[String, String] = {
    val mentorId = (bodyJson \ "user_id").as[String].toInt
    val name = (bodyJson \ "name").as[String]
    val bank = (bodyJson \ "bank").as[String]
    val account = (bodyJson \ "account").as[String]
    val amount = (bodyJson \ "amount").as[Int]
    val today = new java.sql.Date(new java.util.Date().getTime)
    val refund = RefundHistoryRow(id = 0, mentorId = Option(mentorId), name = Option(name), account = Option(account), bank = Option(bank), amount = Option(amount), isApproval = Option(false), createdAt = Option(today), updatedAt = Option(today))
    Await.result(refundHistoryService.insert(refund), Duration.Inf)
    var result: Map[String, String] = new HashMap[String, String]
    result += ("result" -> "success")
    result += ("message" -> "refund register success")
    result
  }
}
