package controllers.api.helpers

import java.sql.Timestamp
import java.util.Calendar

import common.{PushHelper, Utils}
import controllers.admin.Properties
import controllers.api.StaticValues
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import play.api.libs.json.{JsArray, JsValue}
import services._
import tables.Tables._

import scala.collection.immutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object StudentHelper {
  def studentsService = new StudentsService

  def usersService = new UsersService

  def postingsService = new PostingsService

  def postingImagesService = new PostingImagesService

  def textbooksService = new TextbooksService

  def dDayService = new DDayService

  def studiesService = new StudiesService

  def activitiesService = new ActivitiesService

  def activityRecordService = new ActivityRecordsService

  def studyRecordService = new StudyRecordsService

  def optionalSubjectsService = new OptionalSubjectsService

  def targetDepartmentsService = new TargetDepartmentsService

  def schoolExamRecordsService = new SchoolExamRecordService

  def schoolExamService = new SchoolExamService

  def examService = new ExamService

  def examCriteriaService = new ExamCriteriaService

  def examRecordService = new ExamRecordService

  def recommendUniversityService = new RecommendUniversityService

  def customActivityTypeService = new CustomActivityTypeService

  def selectedTextbookService = new SelectedTextbookService

  def subjectsService = new SubjectsService

  def customActivitiesService = new CustomActivitiesService

  def customActivityRecordsService = new CustomActivityRecordsService

  def studyTimeService = new StudyTimeService

  def studyTypeService = new StudyTypeService

  def activityTypeService = new ActivityTypeService

  def StudyGoalService = new StudyGoalService

  def studyTimeSubjectService = new StudyTimeSubjectService

  def studyTimeSubSubjectService = new StudyTimeSubSubjectService

  def mentorsService = new MentorsService

  def saveHistoryService = new SaveHistoryService

  def changeChoiceHistoryService = new ChangeChoiceHistoryService

  //학생 추가
  def insert(student: StudentsRow): Option[StudentsRow] = {
    Await.result(studentsService.insert(student), Duration.Inf)
    Await.result(studentsService.findById(student.id), Duration.Inf)
  }

  //학생 회원가입
  def signUp(bodyJson: JsValue): Int = {
    var newStudent: StudentsRow = new StudentsRow(id = 0, nickname = Option((bodyJson \ "nickname").as[String]), department = Option((bodyJson \ "department").as[Int]), mathType = Option((bodyJson \ "math_type").as[Int]), foreignLanguage = Option((bodyJson \ "foreign_language").as[Int]), year = Option((bodyJson \ "year").as[Int]), parentName = Option((bodyJson \ "parent_name").as[String]), parentPhone = Option((bodyJson \ "parent_phone").as[String]), mpEducation = Option((bodyJson \ "mp_education").as[Boolean]))
    val userId = CommonHelper.createUser(bodyJson, StaticValues.USER_TYPE_STUDENT)
    newStudent = newStudent.copy(id = userId)
    StudentHelper.insert(newStudent)
    val subjects = (bodyJson \ "optional_subjects").as[String]
    if (!subjects.equals("")) {
      val splitSubject = subjects.split(",")
      splitSubject.foreach(subject =>
        if (subject != null)
          Await.result(optionalSubjectsService.insert(OptionalSubjectsRow(userId, subject.toInt)), Duration.Inf)
      )
    }

    val departments = (bodyJson \ "target_departments").as[String].split(",")
    departments.foreach(department =>
      if (department != null)
        Await.result(targetDepartmentsService.insert(department.toInt, userId), Duration.Inf)
    )
    userId
  }

  //아이디로 학생 조회
  def findById(id: Int): Option[StudentsRow] = Await.result(studentsService.findById(id), Duration.Inf)

  //학생 프로필 업데이트
  def updateProfile(bodyJson: JsValue, imageUrl: String) = {
    val existUser = Await.result(usersService.findBySecret((bodyJson \ "secret").as[String]), Duration.Inf).get
    val newUser = existUser.copy(name = Option((bodyJson \ "name").as[String]), gender = Option((bodyJson \ "gender").as[String]), profileImage = Option(imageUrl))
    Await.result(usersService.update(newUser), Duration.Inf)

    val existStudent = Await.result(studentsService.findById(newUser.id), Duration.Inf).get
    val newStudent = existStudent.copy(introduce = Option((bodyJson \ "introduce").as[String]), targetUniversity = Option((bodyJson \ "target_university").as[String]), targetDepartment = Option((bodyJson \ "target_department").as[String]), year = Option((bodyJson \ "year").as[String].toInt), nickname = Option((bodyJson \ "nickname").as[String]))
    Await.result(studentsService.update(newStudent), Duration.Inf)
  }

  //멘토만나기 저장
  def mentoringSave(bodyJson: JsValue) = {
    val date = new java.util.Date()
    val createdAt = new java.sql.Timestamp(date.getTime)
    val posting = PostingsRow(id = 0, title = Option((bodyJson \ "title").as[String]), userId = (bodyJson \ "user_id").as[String].toInt, article = Option((bodyJson \ "article").as[String]), typeId = StaticValues.MENTORING_TYPE_ID, createdAt = Option(createdAt), updatedAt = Option(createdAt), isAnswered = Option(false), numOfReply = Option(0), isChosen = Option(false), isDeleted = Option(false))
    val postingId = Await.result(postingsService.insert(posting), Duration.Inf)
    val existPosting = Await.result(postingsService.findById(postingId), Duration.Inf).get
    val newPosting = existPosting.copy(parentId = Option(postingId))
    Await.result(postingsService.update(newPosting), Duration.Inf)
  }

  //멘토만나기 수정시 세부정보 조회
  def mentoringGet(bodyJson: JsValue): Map[String, String] = {
    val postingId = (bodyJson \ "posting_id").as[Int]
    val posting = Await.result(postingsService.findById(postingId), Duration.Inf).get
    var result: Map[String, String] = new HashMap[String, String]
    result += ("result" -> "success")
    result += ("posting_id" -> posting.id.toString)
    result += ("title" -> posting.title.get)
    result += ("article" -> posting.article.get)
    result
  }

  //멘토만나기 수정
  def mentoringUpdate(bodyJson: JsValue) = {
    val date = new java.util.Date()
    val updatedAt = new java.sql.Timestamp(date.getTime)
    val postingId = (bodyJson \ "posting_id").as[Int]
    var existPosting = Await.result(postingsService.findById(postingId), Duration.Inf).get
    existPosting = existPosting.copy(title = Option((bodyJson \ "title").as[String]), article = Option((bodyJson \ "article").as[String]), updatedAt = Option(updatedAt))
    Await.result(postingsService.update(existPosting), Duration.Inf)
  }

  //풀어주세요 저장
  def clinicSave(bodyJson: JsValue, imageUrl: Seq[String]) = {
    val date = new java.util.Date()
    val createdAt = new java.sql.Timestamp(date.getTime)
    var subjectId = (bodyJson \ "subject_id").as[String].toInt
    val subSubject = subjectId
    if (subjectId >= 60000 && subjectId <= 80000) {
      subjectId = 70000
    }
    val textbookId = (bodyJson \ "textbook_id").as[String].toInt
    val customTextbookId = (bodyJson \ "custom_textbook_id").as[String].toInt
    val textbookRow = Await.result(textbooksService.findById(textbookId), Duration.Inf)
    val customTextbook = Await.result(customTextbooksService.findById(customTextbookId), Duration.Inf)
    val textbookPage = (bodyJson \ "textbook_page").as[String].toInt
    val questionNumber = (bodyJson \ "question_number").as[String].toInt
    var title: String = ""
    var textbook: String = ""
    if (textbookRow.isDefined) {
      title = textbookRow.get.title.get
    } else {
      title = customTextbook.get.title.get
    }
    //게시글 제목
    textbook = title + " " + textbookPage + "쪽 " + questionNumber + "번"
    val posting = PostingsRow(id = 0, userId = (bodyJson \ "user_id").as[String].toInt, title = Option(textbook), article = Option((bodyJson \ "article").as[String]), typeId = StaticValues.CLINIC_TYPE_ID, createdAt = Option(createdAt), updatedAt = Option(createdAt), isAnswered = Option(false), numOfReply = Option(0), textbookId = Option(textbookId), textbookPage = Option(textbookPage), questionNumber = Option(questionNumber), isChosen = Option(false), subjectId = Option(subjectId), subSubject = Option(subSubject), isDeleted = Option(false), customTextbookId = Option(customTextbookId))
    val postingId = Await.result(postingsService.insert(posting), Duration.Inf)
    val existPosting = Await.result(postingsService.findById(postingId), Duration.Inf).get
    //게시글의 parentId 지정(답변일 경우 답변이 달린 게시물의 id, 글일 경우 해당 글의 id)
    val newPosting = existPosting.copy(parentId = Option(postingId))
    Await.result(postingsService.update(newPosting), Duration.Inf)
    imageUrl.foreach { image =>
      Await.result(postingImagesService.insert(PostingImagesRow(id = 0, postingId = postingId, imageUrl = Option(image))), Duration.Inf)
    }

    //auto push to mentor
    val mentorIds = Await.result(mentorsService.getMentorsIdsBySubjectId(subjectId), Duration.Inf)
    PushHelper.sendPushMessage(mentorIds.toList, Properties.PUSH_MESSAGE_ID_MENTOR_CLINIC_QUESTION, Properties.PUSH_CODE_MENTOR_CLINIC)
  }

  //풀어주세요 수정시 세부정보 조회
  def clinicGet(bodyJson: JsValue): Map[String, String] = {
    val postingId = (bodyJson \ "posting_id").as[Int]
    val posting = Await.result(postingsService.findById(postingId), Duration.Inf).get
    val postingImages = CommonHelper.getImagesByPostingId(postingId)
    val textbook = Await.result(textbooksService.findById(posting.textbookId.get), Duration.Inf)
    val customTextbook = Await.result(customTextbooksService.findById(posting.customTextbookId.get), Duration.Inf)

    //데이터 전송을 위해 포맷 변경
    implicit val formats = Serialization.formats(NoTypeHints)
    var result: Map[String, String] = new HashMap[String, String]
    result += ("result" -> "success")
    result += ("posting_id" -> posting.id.toString)
    result += ("article" -> posting.article.get)
    result += ("subject_id" -> posting.subjectId.get.toString)
    result += ("sub_subject" -> posting.subSubject.get.toString)
    if(posting.textbookId.get == 0){
      result += ("textbook" -> customTextbook.get.title.get)
    }else{
      result += ("textbook" -> textbook.get.title.get)
    }
    result += ("textbook_id" -> posting.textbookId.get.toString)
    result += ("custom_textbook_id" -> posting.customTextbookId.get.toString)
    result += ("textbook_page" -> posting.textbookPage.get.toString)
    result += ("question_number" -> posting.questionNumber.get.toString)
    result += ("images" -> write(postingImages))

    result
  }

  //풀어주세요 업데이트
  def clinicUpdate(bodyJson: JsValue, imageUrl: Seq[String]) = {
    val date = new java.util.Date()
    val updatedAt = new java.sql.Timestamp(date.getTime)
    val postingId = (bodyJson \ "posting_id").as[Int]
    var subjectId = (bodyJson \ "subject_id").as[String].toInt
    val subSubject = subjectId
    if (subjectId >= 60000 && subjectId <= 80000) {
      subjectId = 70000
    }
    val textbookId = (bodyJson \ "textbook_id").as[String].toInt
    val customTextbookId = (bodyJson \ "custom_textbook_id").as[String].toInt
    val textbookRow = Await.result(textbooksService.findById(textbookId), Duration.Inf)
    val customTextbook = Await.result(customTextbooksService.findById(customTextbookId), Duration.Inf)
    val textbookPage = (bodyJson \ "textbook_page").as[String].toInt
    val questionNumber = (bodyJson \ "question_number").as[String].toInt
    var title: String = ""
    var textbook: String = ""
    if (textbookRow.isDefined) {
      title = textbookRow.get.title.get
    } else {
      title = customTextbook.get.title.get
    }
    textbook = title + " " + textbookPage + "쪽 " + questionNumber + "번"
    var existPosting = Await.result(postingsService.findById(postingId), Duration.Inf).get
    existPosting = existPosting.copy(title = Option(textbook), article = Option((bodyJson \ "article").as[String]), updatedAt = Option(updatedAt), textbookId = Option(textbookId), textbookPage = Option(textbookPage), questionNumber = Option(questionNumber), subjectId = Option(subjectId), subSubject = Option(subSubject), customTextbookId = Option(customTextbookId))
    Await.result(postingsService.update(existPosting), Duration.Inf)
    imageUrl.foreach { image =>
      Await.result(postingImagesService.insert(PostingImagesRow(id = 0, postingId = postingId, imageUrl = Option(image))), Duration.Inf)
    }
    //기존 이미지 삭제시 삭제 처리
    val deletedImages = (bodyJson \ "deleted_images").as[String]
    if (!deletedImages.equals("") && deletedImages != null) {
      val splitData = deletedImages.split(",")
      splitData.foreach { imageId =>
        Await.result(postingImagesService.delete(imageId.toInt), Duration.Inf)
      }
    }
  }

  //질문내역 조회
  def getPostings(bodyJson: JsValue): List[PostingsRowForList] = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val postingType = (bodyJson \ "posting_type").as[String].toInt
    val postingRows = Await.result(postingsService.findByTypeIdAndUser(userId, postingType), Duration.Inf)
    var postingRowForList = new ListBuffer[PostingsRowForList]()
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    postingRows.foreach { row =>
      //게시물별 이미지 및 세부 정보 추가
      val postingImages = getImagesByPostingId(row.id)
      val user = Await.result(usersService.findById(row.userId), Duration.Inf).get
      var year: Option[Int] = Option(0)
      if (postingType.equals(StaticValues.CLINIC_TYPE_ID)) {
        year = Await.result(studentsService.getYearByStudentId(user.id), Duration.Inf)
      }
      postingRowForList += PostingsRowForList(posting_id = Option(row.id), title = row.title, contents = row.article, year = year, writer = user.name, user_id = Option(row.userId), num_of_images = Option(postingImages.size), images = Option(postingImages), created_at = Option(format.format(row.createdAt.get)), num_of_reply = row.numOfReply, is_answered = row.isAnswered, textbook = row.title, article = row.article, profile_image = user.profileImage)
    }
    postingRowForList.toList
  }

  //게시물 이미지 조회
  def getImagesByPostingId(id: Int): List[PostingImagesForList] = {
    val imageRows = Await.result(postingImagesService.findByPostingId(id), Duration.Inf)
    var postingImages = new ListBuffer[PostingImagesForList]()
    imageRows.foreach { row =>
      postingImages += PostingImagesForList(id = row.id, image_url = row.imageUrl)
    }
    postingImages.toList
  }

  //답변 채택
  def answerChoice(bodyJson: JsValue) = {
    val answerId = (bodyJson \ "answer_id").as[String].toInt
    val userId = (bodyJson \ "user_id").as[String].toInt
    var answerPosting: PostingsRow = Await.result(postingsService.findById(answerId), Duration.Inf).get
    var parentPosting: PostingsRow = Await.result(postingsService.findById(answerPosting.parentId.get), Duration.Inf).get
    if (!parentPosting.userId.equals(userId)) {
      throw new Exception
    } else {
      answerPosting = answerPosting.copy(isChosen = Option(true))
      Await.result(postingsService.update(answerPosting), Duration.Inf)
      parentPosting = parentPosting.copy(isChosen = Option(true))
      Await.result(postingsService.update(parentPosting), Duration.Inf)
      //채택된 답변의 작성자 멘토 포인트 추가
      var mentor = Await.result(mentorsService.findById(answerPosting.userId), Duration.Inf).get
      val balance = mentor.point.get + StaticValues.ANSWER_POINT
      mentor = mentor.copy(point = Option(balance))
      Await.result(mentorsService.update(mentor), Duration.Inf)
      val today = new java.sql.Date(new java.util.Date().getTime)
      Await.result(saveHistoryService.insert(SaveHistoryRow(id = 0, mentorId = Option(mentor.id), postingId = Option(parentPosting.id), balance = Option(balance), createdAt = Option(today), addedPoint = Option(StaticValues.ANSWER_POINT))), Duration.Inf)
    }
  }

  //답변 점수 저장
  def answerEvaluation(bodyJson: JsValue) = {
    val answerId = (bodyJson \ "answer_id").as[String].toInt
    val userId = (bodyJson \ "user_id").as[String].toInt
    val score = (bodyJson \ "score").as[String].toInt
    var answerPosting: PostingsRow = Await.result(postingsService.findById(answerId), Duration.Inf).get
    val parentPosting = Await.result(postingsService.findById(answerPosting.parentId.get), Duration.Inf).get
    if (!parentPosting.userId.equals(userId)) {
      throw new Exception
    } else {
      answerPosting = answerPosting.copy(score = Option(score))
      Await.result(postingsService.update(answerPosting), Duration.Inf)
    }
    //답변의 작성자 멘토에게 push 전송
    parentPosting.typeId match{
      case StaticValues.MENTORING_TYPE_ID => PushHelper.sendPushMessage(List(answerPosting.userId), Properties.PUSH_MESSAGE_ID_MENTOR_MENTORING_EVALUATION, Properties.PUSH_CODE_MENTOR_ANSWER)
      case StaticValues.CLINIC_TYPE_ID => PushHelper.sendPushMessage(List(answerPosting.userId), Properties.PUSH_MESSAGE_ID_MENTOR_CLINIC_EVALUATION, Properties.PUSH_CODE_MENTOR_ANSWER)
    }
  }

  //d-day 저장
  def dDaySave(bodyJson: JsValue) = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val title = (bodyJson \ "title").as[String]
    val date = (bodyJson \ "date").as[String]
    var isActive: Option[Boolean] = Option(false)
    val numOfDDay = Await.result(dDayService.countByStudentId(userId), Duration.Inf)
    println(numOfDDay)
    if (numOfDDay == 0) {
      isActive = Option(true)
    }
    val dDayRow: DDayRow = DDayRow(id = 0, studentId = userId, title = Option(title), date = Option(date), isActive = isActive)
    Await.result(dDayService.insert(dDayRow), Duration.Inf)
  }

  //d-day 수정
  def dDayEdit(bodyJson: JsValue) = {
    val dDayId = (bodyJson \ "dday_id").as[String].toInt
    val title = (bodyJson \ "title").as[String]
    val date = (bodyJson \ "date").as[String]
    var dDayRow: DDayRow = Await.result(dDayService.findById(dDayId), Duration.Inf).get
    dDayRow = dDayRow.copy(title = Option(title), date = Option(date))
    Await.result(dDayService.update(dDayRow), Duration.Inf)
  }

  //d-day 삭제
  def dDayDelete(bodyJson: JsValue) = {
    val dDayId = (bodyJson \ "dday_id").as[String].toInt
    Await.result(dDayService.delete(dDayId), Duration.Inf)
  }

  //d-day 설정
  def dDayChoice(bodyJson: JsValue) = {
    val dDayId = (bodyJson \ "dday_id").as[String].toInt
    val userId = (bodyJson \ "user_id").as[String].toInt
    Await.result(dDayService.updateIsActiveFalse(userId), Duration.Inf)
    Await.result(dDayService.updateIsActiveTrue(dDayId), Duration.Inf)
  }

  //d-day 리스트
  def dDayList(bodyJson: JsValue): Seq[DDayRowForList] = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    Await.result(dDayService.findByStudentId(userId), Duration.Inf)
  }

  //설정된 d-day 조회
  def dDay(bodyJson: JsValue): Option[DDayRow] = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    Await.result(dDayService.findByStudentIdAndIsActive(userId), Duration.Inf)
  }

  //공부 활동 추가
  def studySave(bodyJson: JsValue) = {
    val today = Calendar.getInstance()
    val createdAt = getToday(today.get(Calendar.HOUR_OF_DAY))
    val userId = (bodyJson \ "user_id").as[String].toInt
    val activityTypeId = (bodyJson \ "activity_type_id").as[Int]
    val studyType = (bodyJson \ "study_type").as[Int]
    val textbookId = (bodyJson \ "textbook_id").as[Int]
    val customTextbookId = (bodyJson \ "custom_textbook_id").as[Int]
    val goal = (bodyJson \ "goal").as[String]
    val isRepeated = (bodyJson \ "is_repeated").as[Boolean]
    val lectureId = 0
    val studyRow = StudiesRow(id = 0, studentId = userId, goal = Option(goal), createdAt = Option(createdAt), textbookId = Option(textbookId), isRepeated = Option(isRepeated), isDeleted = Option(false), lectureId = Option(lectureId), activityTypeId = activityTypeId, studyTypeId = studyType, customTextbookId = Option(customTextbookId))
//val studyRow = StudiesRow(id = 0, studentId = userId, goal = Option(goal), createdAt = Option(createdAt), textbookId = Option(textbookId), isRepeated = Option(isRepeated), isDeleted = Option(false), lectureId = Option(lectureId), activityTypeId = activityTypeId, studyTypeId = studyType)
    Await.result(studiesService.insert(studyRow), Duration.Inf)
  }

  //공부이외 활동 추가
  def activitySave(bodyJson: JsValue) = {
    val today = Calendar.getInstance()
    val createdAt = getToday(today.get(Calendar.HOUR_OF_DAY))
    val userId = (bodyJson \ "user_id").as[String].toInt
    val typeId = (bodyJson \ "activity_type_id").as[Int]
    val contents = (bodyJson \ "contents").as[String]
    val goal = (bodyJson \ "goal").as[String]
    val isRepeated = (bodyJson \ "is_repeated").as[Boolean]
    val activityRow = ActivitiesRow(id = 0, studentId = userId, goal = Option(goal), createdAt = Option(createdAt), typeId = typeId, contents = Option(contents), isRepeated = Option(isRepeated), isDeleted = Option(false))
    Await.result(activitiesService.insert(activityRow), Duration.Inf)
  }

  //사용자 정의 활동 추가
  def customActivitySave(bodyJson: JsValue) = {
    val today = Calendar.getInstance()
    val createdAt = getToday(today.get(Calendar.HOUR_OF_DAY))
    val userId = (bodyJson \ "user_id").as[String].toInt
    val typeId = (bodyJson \ "activity_type_id").as[Int]
    val contents = (bodyJson \ "contents").as[String]
    val goal = (bodyJson \ "goal").as[String]
    val isRepeated = (bodyJson \ "is_repeated").as[Boolean]
    val activityRow = CustomActivitiesRow(id = 0, studentId = userId, goal = Option(goal), createdAt = Option(createdAt), customActivityTypeId = typeId, contents = Option(contents), isRepeated = Option(isRepeated), isDeleted = Option(false))
    Await.result(customActivitiesService.insert(activityRow), Duration.Inf)
  }

  //3시 이전에는 이전날짜의 데이터 조회를 위해 날짜 설정
  def getTodayForActivityList(date: java.util.Date): java.sql.Date = {
    var today: java.sql.Date = null
    val cal = Calendar.getInstance()
    val startTime = cal.get(Calendar.HOUR_OF_DAY)
    if (startTime < 3) {
      cal.setTime(date)
      cal.add(Calendar.DATE, -1)
      today = new java.sql.Date(new java.util.Date(cal.getTimeInMillis()).getTime)
    } else {
      today = new java.sql.Date(date.getTime)
    }
    today
  }

  //활동 리스트 조회
  def activityList(bodyJson: JsValue): Seq[ActivitiesRowForList] = {
    val format = new java.text.SimpleDateFormat("yyyy.MM.dd")
    val date = format.parse((bodyJson \ "date").as[String])
    val createdAt = getTodayForActivityList(date)
    val startTime = getStartTimeStamp(createdAt)
    val endTime = getEndTimeStamp(createdAt)
    val result = new ListBuffer[ActivitiesRowForList]
    val userId = (bodyJson \ "user_id").as[String].toInt
    val studyList: Seq[ActivitiesRowForList] = Await.result(studiesService.getByStudentIdAndCreatedAt(userId, createdAt), Duration.Inf)
    val activityList: Seq[ActivitiesRowForList] = Await.result(activitiesService.findByStudentIdAndCreatedAt(userId, createdAt), Duration.Inf)
    val customActivityList: Seq[ActivitiesRowForList] = Await.result(customActivitiesService.findByStudentIdAndCreatedAt(userId, createdAt), Duration.Inf)
    //공부활동 리스트
    studyList.foreach { row =>
      val record = Await.result(studyRecordService.findByActivityIdAndDate(row.id, startTime, endTime), Duration.Inf)
      var tempRow = row
      //해당 활동에 기록된 시간이 있을경우 시간 추가
      if (record.isDefined) {
        tempRow = tempRow.copy(start_time = record.get.startTime, end_time = record.get.endTime, start_page = record.get.startPage, end_page = record.get.endPage)
      }
      result += tempRow
    }
    //공부이외 활동 리스트
    activityList.foreach { row =>
      val record = Await.result(activityRecordService.findByActivityIdAndDate(row.id, startTime, endTime), Duration.Inf)
      var tempRow = row
      //해당 활동에 기록된 시간이 있을경우 시간 추가
      if (record.isDefined) {
        tempRow = tempRow.copy(start_time = record.get.startTime, end_time = record.get.endTime)
      }
      result += tempRow
    }
    //사용자 정의 활동 리스트
    customActivityList.foreach { row =>
      val record = Await.result(customActivityRecordsService.findByActivityIdAndDate(row.id, startTime, endTime), Duration.Inf)
      var tempRow = row
      //해당 활동에 기록된 시간이 있을경우 시간 추가
      if (record.isDefined) {
        tempRow = tempRow.copy(start_time = record.get.startTime, end_time = record.get.endTime)
      }
      result += tempRow
    }
    result.toSeq
  }

  //검색을 위해 시작 시간을 00:00:00으로 설정
  def getStartTimeStamp(date: java.sql.Date): Timestamp = {
    val cal = Calendar.getInstance()
    cal.setTime(date)
    cal.set(Calendar.HOUR, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    new Timestamp(cal.getTime.getTime)
  }

  //검색을 위해 끝 시간을 23:59:59으로 설정
  def getEndTimeStamp(date: java.sql.Date): Timestamp = {
    val cal = Calendar.getInstance()
    cal.setTime(date)
    cal.set(Calendar.HOUR, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    new Timestamp(cal.getTime.getTime)
  }

  //사용자 정의 활동 타입 추가
  def customActivityTypeAdd(bodyJson: JsValue) = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val iconId = (bodyJson \ "icon_id").as[Int]
    val title = (bodyJson \ "title").as[String]
    val activityRow = CustomActivityTypeRow(id = 0, title = Option(title), iconId = Option(iconId), studentId = userId, isDelete = Option(false))
    Await.result(customActivityTypeService.insert(activityRow), Duration.Inf)
  }

  //사용자 정의 활동 타입 삭제
  def customActivityTypeDelete(bodyJson: JsValue) = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val typeId = (bodyJson \ "custom_type_id").as[Int]
    Await.result(customActivityTypeService.updateIsDelete(typeId), Duration.Inf)
  }

  //사용자 정의 활동 타입 리스트
  def customActivityTypeList(bodyJson: JsValue): List[CustomActivityTypeList] = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val customActivityTypeList: Seq[CustomActivityTypeList] = Await.result(customActivityTypeService.findByStudentId(userId), Duration.Inf)
    customActivityTypeList.toList
  }

  //공부이외 활동 삭제
  def activityDelete(bodyJson: JsValue) = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val activityId = (bodyJson \ "activity_id").as[Int]
    val activity = Await.result(activitiesService.findById(activityId), Duration.Inf).get
    if (activity.studentId.equals(userId)) {
      Await.result(activitiesService.updateIsDeletedTrue(activityId), Duration.Inf)
      val today = new java.sql.Date(new java.util.Date().getTime)
      val startTime = getStartTimeStamp(today)
      val endTime = getEndTimeStamp(today)
      val activityRecord = Await.result(activityRecordService.findByActivityIdAndDate(activityId, startTime, endTime), Duration.Inf)
      //활동에 기록된 시간이 있을 경우 시간 삭제
      if(activityRecord.isDefined){
        val row = activityRecord.get
        val duration = row.duration.get * -1
        Await.result(activityRecordService.delete(row.id), Duration.Inf)
        updateStudyTimeActivity(activityId, duration, today)
      }
    } else {
      throw new Exception
    }
  }

  //공부 활동 삭제
  def studyDelete(bodyJson: JsValue) = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val activityId = (bodyJson \ "activity_id").as[Int]
    val study = Await.result(studiesService.findById(activityId), Duration.Inf).get
    if (study.studentId.equals(userId)) {
      Await.result(studiesService.updateIsDeletedTrue(activityId), Duration.Inf)
      val today = new java.sql.Date(new java.util.Date().getTime)
      val startTime = getStartTimeStamp(today)
      val endTime = getEndTimeStamp(today)
      val studyRecord = Await.result(studyRecordService.findByActivityIdAndDate(activityId, startTime, endTime), Duration.Inf)
      //활동에 기록된 시간이 있을 경우 시간 삭제
      if(studyRecord.isDefined){
        val row = studyRecord.get
        val duration = row.duration.get * -1
        Await.result(studyRecordService.delete(row.id), Duration.Inf)
        updateStudyTimeStudy(activityId, duration, today)
      }
    } else {
      throw new Exception
    }
  }

  //사용자 정의 활동 삭제
  def customActivityDelete(bodyJson: JsValue) = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val activityId = (bodyJson \ "activity_id").as[Int]
    val study = Await.result(customActivitiesService.findById(activityId), Duration.Inf).get
    if (study.studentId.equals(userId)) {
      Await.result(customActivitiesService.updateIsDeletedTrue(activityId), Duration.Inf)
      val today = new java.sql.Date(new java.util.Date().getTime)
      val startTime = getStartTimeStamp(today)
      val endTime = getEndTimeStamp(today)
      val activityRecord = Await.result(customActivityRecordsService.findByActivityIdAndDate(activityId, startTime, endTime), Duration.Inf)
      //활동에 기록된 시간이 있을 경우 시간 삭제
      if(activityRecord.isDefined){
        val row = activityRecord.get
        val duration = row.duration.get * -1
        Await.result(customActivityRecordsService.delete(row.id), Duration.Inf)
        val activity = Await.result(customActivitiesService.findById(activityId), Duration.Inf).get
        var studyTime = getStudyTime(activity.studentId, today)
        studyTime = studyTime.copy(etc = Option(studyTime.etc.get + duration))
        Await.result(studyTimeService.update(studyTime), Duration.Inf)
      }
    } else {
      throw new Exception
    }
  }

  //공부 활동 시간 저장
  def studyTimeSave(bodyJson: JsValue) = {
    val cal = Calendar.getInstance()
    val createdAt = new Timestamp(cal.getTime.getTime)
    val activityId = (bodyJson \ "activity_id").as[Int]
    val start = (bodyJson \ "start_time").as[String]
    val end = (bodyJson \ "end_time").as[String]
    val duration = (bodyJson \ "duration").as[String].split(":")
    val durationTime = duration(0).toInt * 3600 + duration(1).toInt * 60 + duration(2).toInt
    val startPage = (bodyJson \ "start_page").as[Int]
    val endPage = (bodyJson \ "end_page").as[Int]
    val studyRecord = StudyRecordsRow(id = 0, createdAt = Option(createdAt), startTime = Option(start), endTime = Option(end), duration = Option(durationTime), studyId = activityId, startPage = Option(startPage), endPage = Option(endPage))
    //    val studyRecord = StudyRecordsRow(id = 0, createdAt = Option(createdAt), startTime = Option(start), endTime = Option(end), duration = Option(durationTime), studyId = activityId)
    Await.result(studyRecordService.insert(studyRecord), Duration.Inf)

    val today = getToday(start.split(":")(0).toInt)
    updateStudyTimeStudy(activityId, durationTime, today)
  }

  //공부 활동 시간 업데이트
  def studyTimeUpdate(bodyJson: JsValue) = {

    val activityId = (bodyJson \ "activity_id").as[Int]
    val start = (bodyJson \ "start_time").as[String]
    val end = (bodyJson \ "end_time").as[String]
    val duration = (bodyJson \ "duration").as[String].split(":")
    val durationTime = duration(0).toInt * 3600 + duration(1).toInt * 60 + duration(2).toInt
    val startPage = (bodyJson \ "start_page").as[Int]
    val endPage = (bodyJson \ "end_page").as[Int]
    val format = new java.text.SimpleDateFormat("yyyy.MM.dd")
    val date = format.parse((bodyJson \ "date").as[String])
    val createdAt = new java.sql.Date(date.getTime)
    val startTime = getStartTimeStamp(createdAt)
    val endTime = getEndTimeStamp(createdAt)
    var studyRecord = Await.result(studyRecordService.findByActivityIdAndDate(activityId, startTime, endTime), Duration.Inf).get
    val existDuration = studyRecord.duration.get
    studyRecord = studyRecord.copy(startTime = Option(start), endTime = Option(end), duration = Option(durationTime), startPage = Option(startPage), endPage = Option(endPage))
    Await.result(studyRecordService.update(studyRecord), Duration.Inf)
    updateStudyTimeStudy(activityId, (durationTime - existDuration), createdAt)
  }

  //공부 활동 시간 저장 및 수정 시 통계 데이터 업데이트
  def updateStudyTimeStudy(activityId: Int, duration: Long, toDay: java.sql.Date) = {
    val activity = Await.result(studiesService.findById(activityId), Duration.Inf).get
    var studyTime = getStudyTime(activity.studentId, toDay)
    if(activity.customTextbookId.get != 0){
      studyTime = studyTime.copy(etc = Option(studyTime.etc.get + duration))
    }else{
      val subject = Await.result(studiesService.findSubjectByStudyId(activityId), Duration.Inf).get

      var studyTimeSubject = getStudyTimeSubject(activity.studentId, toDay, subject)

      val activityType = activity.activityTypeId

      //교재별 분석 데이터 업데이트
      studyTimeSubject = studyTimeSubject.copy(totalStudy = Option(studyTimeSubject.totalStudy.get + duration))
      activityType match {
        case StaticValues.ACTIVITY_TYPE_INDEPENDENTLY => studyTimeSubject = studyTimeSubject.copy(typeIndependently = Option(studyTimeSubject.typeIndependently.get + duration))
        case StaticValues.ACTIVITY_TYPE_LECTURE => studyTimeSubject = studyTimeSubject.copy(typeLecture = Option(studyTimeSubject.typeLecture.get + duration))
        case StaticValues.ACTIVITY_TYPE_SCHOOL => studyTimeSubject = studyTimeSubject.copy(typeSchool = Option(studyTimeSubject.typeSchool.get + duration))
        case StaticValues.ACTIVITY_TYPE_PRIVATE_EDU => studyTimeSubject = studyTimeSubject.copy(typePrivateEdu = Option(studyTimeSubject.typePrivateEdu.get + duration))
        case StaticValues.ACTIVITY_TYPE_PRIVATE_TEACHER => studyTimeSubject = studyTimeSubject.copy(typePrivateTeacher = Option(studyTimeSubject.typePrivateTeacher.get + duration))
        case StaticValues.ACTIVITY_TYPE_CHECK => studyTimeSubject = studyTimeSubject.copy(typeCheck = Option(studyTimeSubject.typeCheck.get + duration))
        case StaticValues.ACTIVITY_TYPE_EXAM => studyTimeSubject = studyTimeSubject.copy(typeExam = Option(studyTimeSubject.typeExam.get + duration))
      }

      val analysisCategory = (Await.result(studiesService.findAnalysisTypeByStudyId(activityId), Duration.Inf).get / 100) * 100
      //방법별 분석 데이터 업데이트
      analysisCategory match {
        case StaticValues.ANALYSIS_CATEGORY_BASIC => studyTimeSubject = studyTimeSubject.copy(contentsBasic = Option(studyTimeSubject.contentsBasic.get + duration))
        case StaticValues.ANALYSIS_CATEGORY_SOLUTION => studyTimeSubject = studyTimeSubject.copy(contentsSolution = Option(studyTimeSubject.contentsSolution.get + duration))
        case StaticValues.ANALYSIS_CATEGORY_EBS => studyTimeSubject = studyTimeSubject.copy(contentsEbs = Option(studyTimeSubject.contentsEbs.get + duration))
        case StaticValues.ANALYSIS_CATEGORY_REAL => studyTimeSubject = studyTimeSubject.copy(contentsReal = Option(studyTimeSubject.contentsReal.get + duration))
      }

      Await.result(studyTimeSubjectService.update(studyTimeSubject), Duration.Inf)

      //탐구이외의 과목의 경우 내용별 분석 데이터 업데이트
      if (subject.id < 60000) {
        var studyTimeSubSubject = getStudyTimeSubSubject(studyTimeSubject.id, subject.id)
        studyTimeSubSubject = studyTimeSubSubject.copy(time = Option(studyTimeSubSubject.time.get + duration))
        Await.result(studyTimeSubSubjectService.update(studyTimeSubSubject), Duration.Inf)
      }

      val studyType = activity.studyTypeId
      //총 공부시간 데이터 업데이트
      studyTime = studyTime.copy(totalStudy = Option(studyTime.totalStudy.get + duration))

      //교과, 비교과 데이터 업데이트
      val isSubject = Await.result(studyTypeService.isSubject(studyType), Duration.Inf).get.get
      if (isSubject) {
        studyTime = studyTime.copy(subject = Option(studyTime.subject.get + duration))
      } else {
        studyTime = studyTime.copy(nonsubject = Option(studyTime.subject.get + duration))
      }

      //혼자공부시간 데이터 업데이트
      val isAlone = Await.result(activityTypeService.isAlone(activity.activityTypeId), Duration.Inf).get.get
      if (isAlone) {
        studyTime = studyTime.copy(alone = Option(studyTime.alone.get + duration))
      }

      //과목별 공부시간 업데이트
      studyType match {
        case StaticValues.ACTIVITY_STUDY_TYPE_KOREA => studyTime = studyTime.copy(korea = Option(studyTime.korea.get + duration))
        case StaticValues.ACTIVITY_STUDY_TYPE_MATH => studyTime = studyTime.copy(math = Option(studyTime.math.get + duration))
        case StaticValues.ACTIVITY_STUDY_TYPE_ENGLISH => studyTime = studyTime.copy(english = Option(studyTime.english.get + duration))
        case StaticValues.ACTIVITY_STUDY_TYPE_SCIENCE => studyTime = studyTime.copy(socialScience = Option(studyTime.socialScience.get + duration))
        case StaticValues.ACTIVITY_STUDY_TYPE_SOCIAL => studyTime = studyTime.copy(socialScience = Option(studyTime.socialScience.get + duration))
      }
    }
    Await.result(studyTimeService.update(studyTime), Duration.Inf)
  }

  //과목별 통계 데이터 저장 및 업데이트를 위해 해당 날짜에 맞는 컬럼 조회 혹은 생성
  def getStudyTimeSubject(studentId: Int, toDay: java.sql.Date, subject: SubjectsRow): StudyTimeSubjectRow = {
    var subjectId = subject.id
    var typeId = (subjectId / 10000) * 10000
    if (typeId == 10000 || typeId == 50000) {
      subjectId = typeId
    } else if (typeId == 20000 || typeId == 30000 || typeId == 40000) {
      typeId = 30000
      subjectId = typeId
    } else {
      typeId = 70000
    }
    var title = ""
    typeId match {
      case 10000 => title = "국어"
      case 30000 => title = "수학"
      case 50000 => title = "영어"
      case 70000 => title = subject.title.get
    }
    val cal = Calendar.getInstance()
    cal.setFirstDayOfWeek(Calendar.MONDAY)
    cal.setTime(toDay)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    val monday = new java.sql.Date(new java.util.Date(cal.getTimeInMillis()).getTime)
    var studyTimeSubject = Await.result(studyTimeSubjectService.findByStudentIdAndCreatedAtAndSubjectIdAndTypeId(studentId, monday, subjectId, typeId), Duration.Inf)
    if (!studyTimeSubject.isDefined) {
      Await.result(studyTimeSubjectService.insert(StudyTimeSubjectRow(id = 0, studentId = Option(studentId), subjectId = subjectId, contentsBasic = Option(0), contentsSolution = Option(0), contentsEbs = Option(0), contentsReal = Option(0), typeIndependently = Option(0), typeLecture = Option(0), typeSchool = Option(0), typePrivateEdu = Option(0), typePrivateTeacher = Option(0), typeCheck = Option(0), typeExam = Option(0), createdAt = Option(monday), title = Option(title), typeId = Option(typeId), totalStudy = Option(0))), Duration.Inf)
      studyTimeSubject = Await.result(studyTimeSubjectService.findByStudentIdAndCreatedAtAndSubjectIdAndTypeId(studentId, monday, subjectId, typeId), Duration.Inf)
    }
    studyTimeSubject.get
  }

  //과목별 내용별 통계 데이터 저장 및 업데이트를 위해 해당 날짜에 맞는 컬럼 조회 혹은 생성
  def getStudyTimeSubSubject(studyTimeSubjectId: Int, subjectId: Int): StudyTimeSubSubjectRow = {
    var depth1 = (subjectId / 10000)
    val depth2 = (subjectId - (depth1 * 10000)) / 100
    if (subjectId >= 20000 && subjectId < 50000) {
      depth1 = 0
    }
    val newSubjectId = depth1 * 10000 + depth2 * 100

    var studyTimeSubSubject = Await.result(studyTimeSubSubjectService.findBySTSIdAndSubjectId(studyTimeSubjectId, newSubjectId), Duration.Inf)
    if (!studyTimeSubSubject.isDefined) {
      if (subjectId >= 20000 && subjectId < 50000) {
        depth1 = subjectId / 10000
        if (depth1 == 4) depth1 = 2
      }
      val subjectIds = Await.result(subjectsService.getSubjectByDepth1(depth1), Duration.Inf)
      subjectIds.foreach { row =>
        var tempSubjectId = row
        if (subjectId >= 20000 && subjectId < 50000) tempSubjectId = row - (row / 10000) * 10000
        val temp = Await.result(studyTimeSubSubjectService.findBySTSIdAndSubjectId(studyTimeSubjectId, tempSubjectId), Duration.Inf)
        if (!temp.isDefined) {
          Await.result(studyTimeSubSubjectService.insert(StudyTimeSubSubjectRow(id = 0, studyTimeSubjectId = Option(studyTimeSubjectId), subjectId = Option(tempSubjectId), time = Option(0))), Duration.Inf)
        }
      }
      studyTimeSubSubject = Await.result(studyTimeSubSubjectService.findBySTSIdAndSubjectId(studyTimeSubjectId, newSubjectId), Duration.Inf)
    }
    studyTimeSubSubject.get
  }

  //공부이외 활동 시간 저장
  def activityTimeSave(bodyJson: JsValue) = {
    val cal = Calendar.getInstance()
    val createdAt = new Timestamp(cal.getTime.getTime)
    val activityId = (bodyJson \ "activity_id").as[Int]
    val start = (bodyJson \ "start_time").as[String]
    val end = (bodyJson \ "end_time").as[String]
    val duration = (bodyJson \ "duration").as[String].split(":")
    val durationTime = duration(0).toInt * 3600 + duration(1).toInt * 60 + duration(2).toInt
    val activityRecord = ActivityRecordsRow(id = 0, createdAt = Option(createdAt), startTime = Option(start), endTime = Option(end), duration = Option(durationTime), activityId = activityId)
    Await.result(activityRecordService.insert(activityRecord), Duration.Inf)

    val today = getToday(start.split(":")(0).toInt)
    updateStudyTimeActivity(activityId, durationTime, today)
  }

  //공부이외 활동 시간 업데이트
  def activityTimeUpdate(bodyJson: JsValue) = {

    val activityId = (bodyJson \ "activity_id").as[Int]
    val start = (bodyJson \ "start_time").as[String]
    val end = (bodyJson \ "end_time").as[String]
    val duration = (bodyJson \ "duration").as[String].split(":")
    val format = new java.text.SimpleDateFormat("yyyy.MM.dd")
    val date = format.parse((bodyJson \ "date").as[String])
    val createdAt = new java.sql.Date(date.getTime)
    val startTime = getStartTimeStamp(createdAt)
    val endTime = getEndTimeStamp(createdAt)
    val durationTime = duration(0).toInt * 3600 + duration(1).toInt * 60 + duration(2).toInt
    var activityRecord = Await.result(activityRecordService.findByActivityIdAndDate(activityId, startTime, endTime), Duration.Inf).get
    val existDuration = activityRecord.duration.get
    activityRecord = activityRecord.copy(startTime = Option(start), endTime = Option(end), duration = Option(durationTime))
    Await.result(activityRecordService.update(activityRecord), Duration.Inf)

    updateStudyTimeActivity(activityId, (durationTime - existDuration), createdAt)
  }

  //공부이외 활동 시간 저장 및 수정 시 통계 데이터 업데이트
  def updateStudyTimeActivity(activityId: Int, duration: Long, toDay: java.sql.Date) = {
    val activity = Await.result(activitiesService.findById(activityId), Duration.Inf).get
    var studyTime = getStudyTime(activity.studentId, toDay)
    val activityType = activity.typeId

    activityType match {
      case StaticValues.ACTIVITY_SUB_TYPE_EXERCISE => studyTime = studyTime.copy(etc = Option(studyTime.etc.get + duration))
      case StaticValues.ACTIVITY_SUB_TYPE_READING => studyTime = studyTime.copy(etc = Option(studyTime.etc.get + duration))
      case StaticValues.ACTIVITY_SUB_TYPE_EAT => studyTime = studyTime.copy(eat = Option(studyTime.eat.get + duration))
      case StaticValues.ACTIVITY_SUB_TYPE_SLEEP => studyTime = studyTime.copy(sleep = Option(studyTime.sleep.get + duration))
      case StaticValues.ACTIVITY_SUB_TYPE_LEISURE => studyTime = studyTime.copy(etc = Option(studyTime.etc.get + duration))
      case StaticValues.ACTIVITY_SUB_TYPE_REST => studyTime = studyTime.copy(rest = Option(studyTime.rest.get + duration))
    }

    Await.result(studyTimeService.update(studyTime), Duration.Inf)
  }

  //사용자 정의 활동 시간 저장
  def customActivityTimeSave(bodyJson: JsValue) = {
    val cal = Calendar.getInstance()
    val createdAt = new Timestamp(cal.getTime.getTime)
    val activityId = (bodyJson \ "activity_id").as[Int]
    val start = (bodyJson \ "start_time").as[String]
    val end = (bodyJson \ "end_time").as[String]
    val duration = (bodyJson \ "duration").as[String].split(":")
    val durationTime = duration(0).toInt * 3600 + duration(1).toInt * 60 + duration(2).toInt
    val activityRecord = CustomActivityRecordsRow(id = 0, createdAt = Option(createdAt), startTime = Option(start), endTime = Option(end), duration = Option(durationTime), customActivityId = activityId)
    Await.result(customActivityRecordsService.insert(activityRecord), Duration.Inf)

    val today = getToday(start.split(":")(0).toInt)
    val activity = Await.result(customActivitiesService.findById(activityId), Duration.Inf).get
    var studyTime = getStudyTime(activity.studentId, today)
    studyTime = studyTime.copy(etc = Option(studyTime.etc.get + durationTime))
    Await.result(studyTimeService.update(studyTime), Duration.Inf)
  }

  //사용자 정의 활동 시간 업데이트
  def customActivityTimeUpdate(bodyJson: JsValue) = {
    val activityId = (bodyJson \ "activity_id").as[Int]
    val start = (bodyJson \ "start_time").as[String]
    val end = (bodyJson \ "end_time").as[String]
    val duration = (bodyJson \ "duration").as[String].split(":")
    val durationTime = duration(0).toInt * 3600 + duration(1).toInt * 60 + duration(2).toInt
    val format = new java.text.SimpleDateFormat("yyyy.MM.dd")
    val date = format.parse((bodyJson \ "date").as[String])
    val createdAt = new java.sql.Date(date.getTime)
    val startTime = getStartTimeStamp(createdAt)
    val endTime = getEndTimeStamp(createdAt)
    var activityRecord = Await.result(customActivityRecordsService.findByActivityIdAndDate(activityId, startTime, endTime), Duration.Inf).get
    val existDuration = activityRecord.duration.get
    activityRecord = activityRecord.copy(startTime = Option(start), endTime = Option(end), duration = Option(durationTime))
    Await.result(customActivityRecordsService.update(activityRecord), Duration.Inf)

    val activity = Await.result(customActivitiesService.findById(activityId), Duration.Inf).get
    var studyTime = getStudyTime(activity.studentId, createdAt)
    studyTime = studyTime.copy(etc = Option(studyTime.etc.get + (durationTime - existDuration)))
    Await.result(studyTimeService.update(studyTime), Duration.Inf)
  }

  //학습분석 전체 통계 데이터 저장 및 업데이트를 위해 해당 날짜에 맞는 컬럼 조회 혹은 생성
  def getStudyTime(studentId: Int, toDay: java.sql.Date): StudyTimeRow = {
    var studyTime = Await.result(studyTimeService.findByStudentIdAndCreatedAt(studentId, toDay), Duration.Inf)
    if (!studyTime.isDefined) {
      Await.result(studyTimeService.insert(StudyTimeRow(id = 0, studentId = Option(studentId), createdAt = Option(toDay), totalStudy = Option(0), subject = Option(0), nonsubject = Option(0), alone = Option(0), korea = Option(0), math = Option(0), english = Option(0), socialScience = Option(0), sleep = Option(0), eat = Option(0), rest = Option(0), etc = Option(0), waste = Option(0))), Duration.Inf)
      studyTime = Await.result(studyTimeService.findByStudentIdAndCreatedAt(studentId, toDay), Duration.Inf)
    }
    studyTime.get
  }

  //3시 이전에는 이전날짜의 데이터 조회를 위해 날짜 설정
  def getToday(startTime: Int): java.sql.Date = {
    var today: java.sql.Date = null
    if (startTime < 3) {
      val cal = Calendar.getInstance()
      cal.add(Calendar.DATE, -1)
      today = new java.sql.Date(new java.util.Date(cal.getTimeInMillis()).getTime)
    } else {
      val date = new java.util.Date()
      today = new java.sql.Date(date.getTime)
    }
    today
  }

  //내신성적 입력
  def recordSchool(bodyJson: JsValue) = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val schoolExam = Await.result(schoolExamService.findActiveExam(), Duration.Inf)
    if (schoolExam.isDefined) {
      val record = Await.result(schoolExamRecordsService.findByStudentIdAndExamId(userId, schoolExam.get.id), Duration.Inf)
      if (record.isDefined) {
        Await.result(schoolExamRecordsService.update(record.get.copy(korean = Option((bodyJson \ "korean").as[Int]), math = Option((bodyJson \ "math").as[Int]), english = Option((bodyJson \ "english").as[Int]), social = Option((bodyJson \ "social").as[Int]), science = Option((bodyJson \ "science").as[Int]))), Duration.Inf)
      } else {
        Await.result(schoolExamRecordsService.insert(SchoolExamRecordRow(id = 0, schoolExamId = schoolExam.get.id, studentId = userId, korean = Option((bodyJson \ "korean").as[Int]), math = Option((bodyJson \ "math").as[Int]), english = Option((bodyJson \ "english").as[Int]), social = Option((bodyJson \ "social").as[Int]), science = Option((bodyJson \ "science").as[Int]))), Duration.Inf)
      }
    }
  }

  //목표대학 입력
  def universitySave(bodyJson: JsValue) = {
    val user_id = (bodyJson \ "user_id").as[String].toInt
    val university = (bodyJson \ "target_university").as[String]

    Await.result(studentsService.updateTargetUniversity(user_id, university), Duration.Inf)
  }

  //새로운 답변내역 존재 여부 확인
  def hasNewAnswer(id: Int): Boolean = {
    Await.result(postingsService.checkHasNewAnswerByUserId(id), Duration.Inf)
  }

  //입력가능 모의고사 리스트 조회
  def examRecordList(bodyJson: JsValue): List[ExamList] = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val exams = Await.result(examService.findByAllActiveExamList(), Duration.Inf)
    val subjects = getSubject(userId)
    val examCriteria = Await.result(subjectsService.findBySubjectIdIn(subjects), Duration.Inf)
    val examList = new ListBuffer[ExamList]()
    exams.foreach { exam =>
      val title = exam.year.get + "년 " + exam.month.get + "월 " + exam.title.get
      examList.append(ExamList(id = exam.id, title = title, exam_subject_score = examCriteria.toList, exam_subject_percentile = examCriteria.toList, exam_subject_standard = examCriteria.toList))
    }
    examList.toList
  }

  //학생별 선택과목 조회
  def getSubject(userId: Int): List[Int] = {
    val userMathType = Await.result(studentsService.getMathTypeByStudentId(userId), Duration.Inf).get
    val foreignLanguage = Await.result(studentsService.getForeignByStudentId(userId), Duration.Inf).get
    val optionalSubject = Await.result(optionalSubjectsService.getSubjectIdByStudentId(userId), Duration.Inf)
    val subjects = new ListBuffer[Int]()
    subjects.append(StaticValues.SUBJECT_ID_KOREAN)
    if (userMathType == 1) subjects.append(StaticValues.SUBJECT_ID_MATH_A) else if (userMathType == 2) subjects.append(StaticValues.SUBJECT_ID_MATH_B)
    subjects.append(StaticValues.SUBJECT_ID_ENGLISH)
    subjects.appendAll(optionalSubject)
    if(foreignLanguage != 0) subjects.append(foreignLanguage)
    subjects.toList
  }

  //모의고사 점수 저장
  def recordExam(bodyJson: JsValue) = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val examId = (bodyJson \ "exam_id").as[Int]
    val examScore = (bodyJson \ "score").as[JsArray]
    val examPercentile = (bodyJson \ "percentile").as[JsArray]
    val examStandard = (bodyJson \ "standard").as[JsArray]

    //원점수 저장
    examScore.value.foreach { jValue =>
      val subjectId = (jValue \ "id").as[Int]
      val score = (jValue \ "score").as[Int]
      val criteria = Await.result(examCriteriaService.findOneByExamAndSubjectAndType(examId, subjectId, StaticValues.EXAM_RECORD_TYPE_SCORE), Duration.Inf).get
      val grade = getGrade(score, criteria)
      val scoreRecord = Await.result(examRecordService.findByStudentIdAndExamId(userId, examId, StaticValues.EXAM_RECORD_TYPE_SCORE, subjectId), Duration.Inf)
      if (scoreRecord.isDefined) {
        Await.result(examRecordService.update(scoreRecord.get.copy(score = Option(score))), Duration.Inf)
      } else {
        Await.result(examRecordService.insert(ExamRecordsRow(studentId = userId, score = Option(score), subjectId = subjectId, examId = Option(examId), recordTypeId = Option(StaticValues.EXAM_RECORD_TYPE_SCORE))), Duration.Inf)
      }
      val gradeRecord = Await.result(examRecordService.findByStudentIdAndExamId(userId, examId, StaticValues.EXAM_RECORD_TYPE_GRADE, subjectId), Duration.Inf)
      if (gradeRecord.isDefined) {
        Await.result(examRecordService.update(gradeRecord.get.copy(score = Option(grade))), Duration.Inf)
      } else {
        Await.result(examRecordService.insert(ExamRecordsRow(userId, Option(grade), subjectId, Option(examId), Option(StaticValues.EXAM_RECORD_TYPE_GRADE))), Duration.Inf)
      }
    }
    //백분위 점수 저장
    examPercentile.value.foreach { jValue =>
      val subjectId = (jValue \ "id").as[Int]
      val score = (jValue \ "score").as[Int]
      val percentileRecord = Await.result(examRecordService.findByStudentIdAndExamId(userId, examId, StaticValues.EXAM_RECORD_TYPE_PERCENTILE, subjectId), Duration.Inf)
      if (percentileRecord.isDefined) {
        Await.result(examRecordService.update(percentileRecord.get.copy(score = Option(score))), Duration.Inf)
      } else {
        Await.result(examRecordService.insert(ExamRecordsRow(userId, Option(score), subjectId, Option(examId), Option(StaticValues.EXAM_RECORD_TYPE_PERCENTILE))), Duration.Inf)
      }
    }
    //표준점수 저장
    examStandard.value.foreach { jValue =>
      val subjectId = (jValue \ "id").as[Int]
      val score = (jValue \ "score").as[Int]
      val standardRecord = Await.result(examRecordService.findByStudentIdAndExamId(userId, examId, StaticValues.EXAM_RECORD_TYPE_STANDARD, subjectId), Duration.Inf)
      if (standardRecord.isDefined) {
        Await.result(examRecordService.update(standardRecord.get.copy(score = Option(score))), Duration.Inf)
      } else {
        Await.result(examRecordService.insert(ExamRecordsRow(userId, Option(score), subjectId, Option(examId), Option(StaticValues.EXAM_RECORD_TYPE_STANDARD))), Duration.Inf)
      }
    }
    updateTotalGrade(userId)
  }

  //모의고사 성적 입력시 종합성적 저장
  def updateTotalGrade(studentId: Int) ={
    val latestExamId = Await.result(examRecordService.getLatestExamId(studentId), Duration.Inf)
    val totalGrade = Await.result(examRecordService.getTotalGrade(studentId, latestExamId.get.get), Duration.Inf)
    Await.result(studentsService.updateTotalGrade(studentId, totalGrade.get), Duration.Inf)
  }

  //모의고사 등급별 점수를 기준으로 등급 계산
  def getGrade(score: Int, criteria: ExamCriteriaRow): Int = {
    var grade = 0
    if (score >= criteria.tier1Min.get && score <= criteria.tier1Max.get) {
      grade = 1
    } else if (score >= criteria.tier2Min.get && score <= criteria.tier2Max.get) {
      grade = 2
    } else if (score >= criteria.tier3Min.get && score <= criteria.tier3Max.get) {
      grade = 3
    } else if (score >= criteria.tier4Min.get && score <= criteria.tier4Max.get) {
      grade = 4
    } else if (score >= criteria.tier5Min.get && score <= criteria.tier5Max.get) {
      grade = 5
    } else if (score >= criteria.tier6Min.get && score <= criteria.tier6Max.get) {
      grade = 6
    } else if (score >= criteria.tier7Min.get && score <= criteria.tier7Max.get) {
      grade = 7
    } else if (score >= criteria.tier8Min.get && score <= criteria.tier8Max.get) {
      grade = 8
    } else if (score >= criteria.tier9Min.get && score <= criteria.tier9Max.get) {
      grade = 9
    } else {
      grade = 1
    }

    grade
  }

  //추천 대학 리스트
  def recommendUniversityList(bodyJson: JsValue): List[UniversityList] = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val student = Await.result(studentsService.findById(userId), Duration.Inf)
    val latestExamId = Await.result(examRecordService.getLatestExamId(userId), Duration.Inf)
    val totalScore = Await.result(examRecordService.getTotalScore(userId, latestExamId.get.get), Duration.Inf)
//    println("---------------total score--------------------------")
//    println(totalScore.get + "min" + (totalScore.get * 0.9).toInt + "max" + (totalScore.get * 1.1).toInt)

    val department = student.get.department
    val mpEducation = student.get.mpEducation
    val departmentList = new ListBuffer[Int]()
    departmentList.append(department.get)
    if (mpEducation.get)
      departmentList.append(3)
    val universityList = Await.result(recommendUniversityService.getRecommendUniversity(totalScore.get, departmentList.toList), Duration.Inf)

    universityList.toList
  }

  //활동추가시 선택 교재 추가
  def selectedTextbookAdd(bodyJson: JsValue) = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val studyType = (bodyJson \ "study_type").as[Int]
    val textbookId = (bodyJson \ "textbook_id").as[Int]
    val isCustom = (bodyJson \ "is_custom").as[Boolean]
    Await.result(selectedTextbookService.insert(SelectedTextbookRow(id = 0, studentId = userId, studyType = studyType, textbookId = textbookId, isCustom = Option(isCustom))), Duration.Inf)
  }

  //활동추가시 선택 교재 삭제
  def selectedTextbookDelete(bodyJson: JsValue) = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val studyType = (bodyJson \ "study_type").as[Int]
    val textbookId = (bodyJson \ "textbook_id").as[Int]
    val isCustom = (bodyJson \ "is_custom").as[Boolean]

    Await.result(selectedTextbookService.deleteByStudentAndStudyTypeAndTextbook(userId, studyType, textbookId, isCustom), Duration.Inf)
  }

  //활동추가시 선택 교재 리스트
  def selectedTextbookList(bodyJson: JsValue): List[TextbookList] = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val studyType = (bodyJson \ "study_type").as[Int]
    val textbookList = Await.result(selectedTextbookService.getByStudentIdAndStudyType(userId, studyType), Duration.Inf)
    textbookList.toList
  }

  //활동추가시 교재 리스트
  def textbookList(bodyJson: JsValue): List[TextbookList] = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val studyType = (bodyJson \ "study_type").as[Int]
    val subject = new ListBuffer[Int]()
    //선택한 과목에 따라 세부과목 설정
    if (studyType == 1) {
      subject.appendAll(Await.result(subjectsService.findIdByDepth1(1), Duration.Inf))
    } else if (studyType == 2) {
      val mathType = Await.result(studentsService.getMathTypeByStudentId(userId), Duration.Inf).get
      if (mathType == 1) {
        subject.appendAll(Await.result(subjectsService.findIdByDepth1(2), Duration.Inf))
        subject.appendAll(Await.result(subjectsService.findIdByDepth1(3), Duration.Inf))
        subject.appendAll(Await.result(subjectsService.findIdByDepth1(4), Duration.Inf))
      } else {
        subject.appendAll(Await.result(subjectsService.findIdByDepth1(2), Duration.Inf))
        subject.appendAll(Await.result(subjectsService.findIdByDepth1(4), Duration.Inf))
      }
    } else {
      subject.appendAll(Await.result(subjectsService.findIdByDepth1(studyType + 2), Duration.Inf))
    }
    val textbookList = Await.result(textbooksService.findBySubjectIds(subject.toList), Duration.Inf)
    val customTextbookList = Await.result(customTextbooksService.findBySubjectId(studyType, userId), Duration.Inf)
    textbookList.toList ++ customTextbookList.toList
  }

  //풀어주세요 교재 리스트
  def textbookListForPosting(bodyJson: JsValue): List[TextbookList] = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val subjectId = (bodyJson \ "subject_id").as[Int]
    val subject = new ListBuffer[Int]()
    //선택한 과목에 따라 세부과목 설정
    if (subjectId == 10000 || subjectId == 50000 || subjectId == 130000) {
      subject.appendAll(Await.result(subjectsService.findIdByDepth1(subjectId / 10000), Duration.Inf))
    } else if (subjectId == 30000) {
      val mathType = Await.result(studentsService.getMathTypeByStudentId(userId), Duration.Inf).get
      if (mathType == 1) {
        subject.appendAll(Await.result(subjectsService.findIdByDepth1(2), Duration.Inf))
        subject.appendAll(Await.result(subjectsService.findIdByDepth1(3), Duration.Inf))
        subject.appendAll(Await.result(subjectsService.findIdByDepth1(4), Duration.Inf))
      } else {
        subject.appendAll(Await.result(subjectsService.findIdByDepth1(2), Duration.Inf))
        subject.appendAll(Await.result(subjectsService.findIdByDepth1(4), Duration.Inf))
      }
    } else {
      val depth1 = subjectId / 10000
      val depth2 = (subjectId - (depth1 * 10000)) / 100
      subject.appendAll(Await.result(subjectsService.findIdByDepth1AndDepth2(depth1, depth2), Duration.Inf))
    }
    val result = new ListBuffer[TextbookList]
    val textbookList = Await.result(textbooksService.findBySubjectIds(subject.toList), Duration.Inf)
    val customTextbookList = Await.result(customTextbooksService.findBySubjectIds(subject.toList, userId), Duration.Inf)
    textbookList.toList ++ customTextbookList.toList
  }

  //목표 공부시간 설정
  def studyGoalSave(bodyJson: JsValue) = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val hour = (bodyJson \ "hour").as[Int]
    val minute = (bodyJson \ "minute").as[Int]
    val alarm = (bodyJson \ "alarm").as[Boolean]
    val goal: Long = (hour * 3600) + (minute * 60)
    val monday = Utils.getMonday()
    val studyGoal = Await.result(StudyGoalService.findByStudentIdAndMonday(userId, monday), Duration.Inf)
    if (studyGoal.isDefined) {
      Await.result(StudyGoalService.update(studyGoal.get.copy(goalTime = Option(goal), alarm = Option(alarm))), Duration.Inf)
    } else {
      Await.result(StudyGoalService.insert(StudyGoalRow(id = 0, goalTime = Option(goal), alarm = Option(alarm), monday = Option(monday), studentId = Option(userId))), Duration.Inf)
    }
  }

  //목표 공부시간 조회
  def getStudyGoal(bodyJson: JsValue): Map[String, String] = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    var result: Map[String, String] = new HashMap[String, String]
    result += ("result" -> "success")
    val monday = Utils.getMonday()
    val studyGoal = Await.result(StudyGoalService.findByStudentIdAndMonday(userId, monday), Duration.Inf)
    if (studyGoal.isDefined) { //목표공부시간이 설정되어 있을 경우
      val goal = (studyGoal.get.goalTime.get / 7).toInt
      val hour = (goal / 3600)
      val min = ((goal - (hour * 3600)) / 60)
      val alarm = studyGoal.get.alarm.get.toString
      result += ("hour" -> hour.toString)
      result += ("minute" -> min.toString)
      result += ("alarm" -> alarm)
    } else { //목표공부시간이 설정되지 않았을 경우
      result += ("hour" -> "0")
      result += ("minute" -> "0")
      result += ("alarm" -> "false")
    }
    result
  }

  def customTextbooksService = new CustomTextbooksService

  //사용자정의 교재 추가
  def customTextbookAdd(bodyJson: JsValue): Int = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val subjectId = (bodyJson \ "subject_id").as[Int]
    val title = (bodyJson \ "title").as[String]

    Await.result(customTextbooksService.insert(CustomTextbooksRow(id = 0, title = Option(title), subjectId = Option(subjectId), userId = Option(userId))), Duration.Inf)
  }

  //채택 변경 요청
  def answerChoiceChange(bodyJson: JsValue) = {
    val answerId = (bodyJson \ "answer_id").as[String].toInt
    val userId = (bodyJson \ "user_id").as[String].toInt
    val answerPosting: PostingsRow = Await.result(postingsService.findById(answerId), Duration.Inf).get
    val parentPosting: PostingsRow = Await.result(postingsService.findById(answerPosting.parentId.get), Duration.Inf).get
    if (!parentPosting.userId.equals(userId)) {
      throw new Exception
    } else {
      val existAnswer = Await.result(postingsService.getChoicedAnswerId(parentPosting.id), Duration.Inf).get
      Await.result(changeChoiceHistoryService.insert(ChangeChoiceHistoryRow(id = 0, postingId = Option(parentPosting.id), choicedId = Option(existAnswer), newAnswerId = Option(answerPosting.id))), Duration.Inf)
    }
  }
}
