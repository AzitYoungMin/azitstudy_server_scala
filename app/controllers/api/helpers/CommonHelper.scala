package controllers.api.helpers


import java.sql.Timestamp
import java.util.{Calendar, UUID}

import common.{PushHelper, SecretGenerator}
import controllers.admin.Properties
import controllers.api.StaticValues
import org.apache.commons.mail.SimpleEmail
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.Files
import play.api.libs.json.JsValue
import play.api.mvc.MultipartFormData
import services._
import tables.Tables._

import scala.collection.immutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration.Duration


object CommonHelper {
  def usersService = new UsersService

  def postingsService = new PostingsService

  def postingImagesService = new PostingImagesService

  def repliesService = new RepliesService

  def replyLikeService = new ReplyLikeService

  def replyReportService = new ReplyReportService

  def textbooksService = new TextbooksService

  def messagesService = new MessagesService

  def studentsService = new StudentsService

  def mentorsService = new MentorsService

  def userEduService = new UserEduService

  def examCriteriaService = new ExamCriteriaService

  def examRecordService = new ExamRecordService

  def studyTimeService = new StudyTimeService

  def StudyGoalService = new StudyGoalService

  def studyTimeSubjectService = new StudyTimeSubjectService

  def studyTimeSubSubjectService = new StudyTimeSubSubjectService

  def postingReportService = new PostingReportService

  def subjectsService = new SubjectsService

  def optionalSubjectsService = new OptionalSubjectsService

  def pushMessageService = new PushMessageService

  def customTextbooksService = new CustomTextbooksService

  def noticesService = new NoticesService

  //이메일 중복 체크
  def emailIsDuplicated(email: String): Boolean = {
    val userCheck = Await.result(usersService.findByEmail(email), Duration.Inf)
    if (userCheck.isDefined) true else false
  }

  //유저 생성
  def createUser(bodyJson: JsValue, typeId: Int): Int = {
    val secret = SecretGenerator.getSecret()
    val createdAt = new Timestamp(new java.util.Date().getTime)
    val newUser = UsersRow(id = 0, name = Option((bodyJson \ "name").as[String]), email = Option((bodyJson \ "email").as[String]), password = Option(BCrypt.hashpw((bodyJson \ "password").as[String], BCrypt.gensalt())), phone = Option((bodyJson \ "phone").as[String]), gender = Option((bodyJson \ "gender").as[String]), profileImage = None, secret = Option(secret), typeId = typeId, isWithdrawal = Option(false), createdAt = Option(createdAt))
    val eduInstId = (bodyJson \ "edu_inst_id").as[String].toInt
    val userId = Await.result(usersService.insert(newUser), Duration.Inf)
    Await.result(userEduService.insert(UserEduRow(eduInstId, userId, Option(true))), Duration.Inf)
    userId
  }

  //유저 삭제
  def deleteUser(userId: Int) = {
    Await.result(usersService.delete(userId), Duration.Inf)
  }

  //secret check
  def secretIsValid(bodyJson: JsValue): Option[UsersRow] = {
    val secret = (bodyJson \ "secret").as[String]
    val user = Await.result(usersService.findBySecret(secret), Duration.Inf)
    if (user.isDefined && user.get.id == (bodyJson \ "user_id").as[String].toInt) user else None
  }

  //secret check
  def secretIsValid(id: Int, secret: String): Option[UsersRow] = {
    val user = Await.result(usersService.findBySecret(secret), Duration.Inf)
    if (user.isDefined && user.get.id == id) user else Option(null)
  }

  //게시물 리스트 조회
  def getPostings(bodyJson: JsValue): List[PostingsRowForList] = {
    val postingType = (bodyJson \ "posting_type").as[String].toInt
    var postingRows: Seq[PostingsRow] = null
    if (postingType.equals(StaticValues.MENTORING_TYPE_ID)) {
      postingRows = Await.result(postingsService.findByTypeId(postingType, (bodyJson \ "page_number").as[Int], (bodyJson \ "page_size").as[String].toInt), Duration.Inf)
    } else {
      postingRows = Await.result(postingsService.findByTypeIdAndSubjectId(postingType, (bodyJson \ "page_number").as[Int], (bodyJson \ "page_size").as[String].toInt, (bodyJson \ "subject_id").as[String].toInt), Duration.Inf)
    }
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

  //게시물 마지막 페이지 번호 조회
  def getPostingLastPageNumber(bodyJson: JsValue): Int = {
    val postingType = (bodyJson \ "posting_type").as[String].toInt
    if (postingType.equals(StaticValues.MENTORING_TYPE_ID)) {
      math.ceil(Await.result(postingsService.countByTypeId((bodyJson \ "posting_type").as[String].toInt), Duration.Inf) / (bodyJson \ "page_size").as[String].toDouble).toInt
    } else {
      math.ceil(Await.result(postingsService.countByTypeIdAndSubjectId((bodyJson \ "posting_type").as[String].toInt, (bodyJson \ "subject_id").as[String].toInt), Duration.Inf) / (bodyJson \ "page_size").as[String].toDouble).toInt
    }
  }

  //게시물 세부 정보 조회
  def getPosting(bodyJson: JsValue): PostingContents = {
    val id = (bodyJson \ "posting_id").as[String].toInt
    val user = Await.result(usersService.findBySecret((bodyJson \ "secret").as[String]), Duration.Inf).get
    val posting = Await.result(postingsService.getPostingContents(id), Duration.Inf)
    //게시물 작성자가 해당 게시물 조회시 새로운 답변을 읽은 것 변경
    if (user.id.equals(posting.user_id.get)) {
      Await.result(postingsService.updateHasNewAnswerFalse(id), Duration.Inf)
    }
    val images = getImagesByPostingId(id)
    val replies = getReplies(id, user.id)
    val answers = getAnswer(id)
    posting.copy(num_of_images = Option(images.size), images = Option(images), replies = Option(replies), answers = Option(answers))
  }

  //답변 조회
  def getAnswer(posting_id: Int): List[AnswerContents] = {
    val answers = Await.result(postingsService.getAnswerContents(posting_id), Duration.Inf)
    answers.toList
  }

  //댓글 조회
  def getReplies(id: Int, userId: Int): List[PostingReplies] = {
    val replies = Await.result(repliesService.getRepliesByPostingId(id), Duration.Inf)
    var repliesList = new ListBuffer[PostingReplies]()
    //게시물 조회자가 해당 댓글에 좋아요 클릭 여부 데이터 입력
    replies.map { row =>
      val clickLike: Boolean = Await.result(replyLikeService.checkExist(row.reply_id, userId), Duration.Inf)
      repliesList += row.copy(click_like = Option(clickLike))
    }
    repliesList.toList
  }

  //댓글 좋아요
  def addReplyLike(bodyJson: JsValue) = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val replyId = (bodyJson \ "reply_id").as[String].toInt
    val replyLike = Await.result(replyLikeService.findByReplyIdAndUserId(replyId, userId), Duration.Inf)
    //이미 좋아요를 클릭했을 경우 좋아요 취소
    if (replyLike.isDefined) {
      Await.result(replyLikeService.delete(replyId, userId), Duration.Inf)
    } else {
      Await.result(replyLikeService.insert(ReplyLikeRow(replyId, userId)), Duration.Inf)
    }

    updateReplyLike(replyId)
  }

  //댓글 좋아요 추가 시 정보 저장
  def updateReplyLike(id: Int) = {
    val numOfLike = Await.result(replyLikeService.countByReplyId(id), Duration.Inf)
    val existReply = Await.result(repliesService.findById(id), Duration.Inf).get
    val newReply = existReply.copy(numOfLike = Option(numOfLike))
    Await.result(repliesService.update(newReply), Duration.Inf)
  }

  //댓글 신고
  def replyReport(bodyJson: JsValue): String = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val replyId = (bodyJson \ "reply_id").as[String].toInt
    val replyReport = Await.result(replyReportService.findByReplyIdAndUserId(replyId, userId), Duration.Inf)
    replyReport match {
      case Some(result) => {
        //이미 신고를 했을 경우 신고 취소
        Await.result(replyReportService.delete(replyId, userId), Duration.Inf)
        "reply report cancel success"
      }
      case _ => {
        Await.result(replyReportService.insert(ReplyReportRow(replyId, userId)), Duration.Inf)
        val existReply = Await.result(repliesService.findById(replyId), Duration.Inf).get
        val user = Await.result(usersService.findById(existReply.userId), Duration.Inf).get
        //해당 게시물 작성자에게 신고 push 전송
        if(user.typeId == StaticValues.USER_TYPE_STUDENT){
          PushHelper.sendPushMessage(List(existReply.userId), Properties.PUSH_MESSAGE_ID_STUDENT_REPORT, Properties.PUSH_CODE_STUDENT)
        }else if(user.typeId == StaticValues.USER_TYPE_MENTOR){
          PushHelper.sendPushMessage(List(existReply.userId), Properties.PUSH_MESSAGE_ID_MENTOR_REPORT, Properties.PUSH_CODE_MENTOR_ANSWER)
        }
        "reply report success"
      }
    }
  }

  //댓글 삭제
  def deleteReply(bodyJson: JsValue) = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val replyId = (bodyJson \ "reply_id").as[String].toInt
    val reply = Await.result(repliesService.findByIdAndUserId(replyId, userId), Duration.Inf)
    if (reply.isDefined) {
      val postingId = reply.get.postingId
      val posting = Await.result(postingsService.findById(postingId), Duration.Inf).get
      //게시물의 댓글 갯수 변경
      val numOfReply = posting.numOfReply.get - 1
      Await.result(repliesService.deleteReply(replyId), Duration.Inf)
      Await.result(postingsService.update(posting.copy(numOfReply = Option(numOfReply))), Duration.Inf)
    } else {
      throw new Exception
    }
  }

  //이미지 저장 후 경로 리턴
  def imageUpload(file: MultipartFormData.FilePart[Files.TemporaryFile], id: Int, path: String): String = {
    import java.io.File
    val date = new java.util.Date()
    val format = new java.text.SimpleDateFormat("yyyyMMdd")
    val subPath = "/images/" + path + format.format(date) + "/"
    //    val filename = file.filename
    //    val extension = filename.substring(filename.lastIndexOf("."), filename.length)
    val extension = ".jpg"
    val newFilename = UUID.randomUUID().toString + extension
    val uploadPath = StaticValues.FILE_UPLOAD_BASE_PATH + subPath + id.toString + "/"
    val directory = new File(uploadPath)
    if (!directory.exists()) {
      directory.mkdirs()
    }
    file.ref.moveTo(new File(uploadPath + newFilename))
    val returnValue = subPath + id.toString + "/" + newFilename
    println(returnValue)
    returnValue
  }

  //댓글 저장
  def replySave(bodyJson: JsValue, imageUrl: String) = {
    val date = new java.util.Date()
    val createdAt = new Timestamp(date.getTime)
    val postingId = (bodyJson \ "posting_id").as[String].toInt
    val reply: RepliesRow = RepliesRow(id = 0, reply = Option((bodyJson \ "reply").as[String]), userId = (bodyJson \ "user_id").as[String].toInt, postingId = postingId, createdAt = Option(createdAt), numOfLike = Option(0), numOfReply = Option(0), imageUrl = Option(imageUrl), isDeleted = Option(false))
    val replyId = Await.result(repliesService.insert(reply), Duration.Inf)
    var existReply: RepliesRow = Await.result(repliesService.findById(replyId), Duration.Inf).get
    var parentId = (bodyJson \ "parent_id").as[String].toInt
    if (parentId.equals(0)) {
      parentId = replyId
      var posting: PostingsRow = Await.result(postingsService.findById(postingId), Duration.Inf).get
      posting = posting.copy(numOfReply = Option(posting.numOfReply.get + 1))
      Await.result(postingsService.update(posting), Duration.Inf)
    } else {
      //댓글의 댓글을 다는경우, 현재 사용하지 않음
      var parentReply: RepliesRow = Await.result(repliesService.findById(parentId), Duration.Inf).get
      parentReply = parentReply.copy(numOfReply = Option(parentReply.numOfReply.get + 1))
      Await.result(repliesService.update(parentReply), Duration.Inf)
    }
    existReply = existReply.copy(parentId = Option(parentId))
    Await.result(repliesService.update(existReply), Duration.Inf)
    val posting = Await.result(postingsService.findById(postingId), Duration.Inf).get

    //멘토의 답변에 댓글이 달릴경우 멘토에게 push 전송
    if(posting.typeId == StaticValues.MENTORING_TYPE_ID){
      val user = Await.result(usersService.findById(posting.userId), Duration.Inf).get
      if(user.typeId == StaticValues.USER_TYPE_MENTOR){
        PushHelper.sendPushMessage(List(existReply.userId), Properties.PUSH_MESSAGE_ID_MENTOR_MENTORING_REPLY, Properties.PUSH_CODE_MENTOR_ANSWER)
      }
    }

  }

  //메세지 전송
  def messageSend(bodyJson: JsValue) = {
    val date = new java.util.Date()
    val createdAt = new java.sql.Timestamp(date.getTime)
    val message = (bodyJson \ "message").as[String]
    val title = (bodyJson \ "title").as[String]
    val senderId = (bodyJson \ "user_id").as[String].toInt
    val receiver = (bodyJson \ "receiver_id").as[String]
    if (!receiver.equals("")) {
      val splitReceiver = receiver.split(",")
      splitReceiver.foreach(receiverId =>
        Await.result(messagesService.insert(MessagesRow(id = 0, message = Option(message), senderId = senderId, receiverId = receiverId.toInt, createdAt = Option(createdAt), isNew = Option(true), title = Option(title))), Duration.Inf)
      )
    }
  }

  //메세지 리스트
  def messageList(bodyJson: JsValue): Seq[MessagesRowForList] = {
    val receiverId = (bodyJson \ "user_id").as[String].toInt
    Await.result(messagesService.findByReceiverId(receiverId), Duration.Inf)
  }

  //메세지 세부 정보 조회
  def getMessage(bodyJson: JsValue): Map[String, String] = {
    val messageId = (bodyJson \ "message_id").as[String].toInt
    val originMessage = Await.result(messagesService.findById(messageId), Duration.Inf)
    val message = Await.result(messagesService.getMessage(messageId), Duration.Inf)
    val receiverId = (bodyJson \ "user_id").as[String].toInt
    if (originMessage.isDefined && originMessage.get.receiverId.equals(receiverId)) {
      Await.result(messagesService.updateIsNewFalse(messageId), Duration.Inf)
    }

    var result: Map[String, String] = new HashMap[String, String]
    result += ("result" -> "success")
    result += ("message_id" -> message.get.message_id.toString)
    result += ("message" -> message.get.message.get)
    result += ("title" -> message.get.title.get)
    result += ("name" -> message.get.name.get)
    result += ("created_at" -> message.get.created_at.get)
    result
  }

  //메세지 삭제
  def messageDelete(bodyJson: JsValue) = {
    val messageId = (bodyJson \ "message_id").as[Int]
    Await.result(messagesService.delete(messageId), Duration.Inf)
  }

  //닉네임 중복 체크
  def nicknameCheck(bodyJson: JsValue): Boolean = {
    val nickname = (bodyJson \ "nickname").as[String]
    val student = Await.result(studentsService.findByNickname(nickname), Duration.Inf)
    val mentor = Await.result(mentorsService.findByNickname(nickname), Duration.Inf)
    if (student.isDefined || mentor.isDefined) true else false
  }

  //새로운 메세지 존재 여부 확인
  def hasNewMessage(id: Int): Boolean = {
    Await.result(messagesService.checkIsNewExistByReceiverId(id), Duration.Inf)
  }

  //비밀번호 변경
  def passwordChange(bodyJson: JsValue) = {
    val user_id = (bodyJson \ "user_id").as[String].toInt
    val password = (bodyJson \ "new_password").as[String]

    Await.result(usersService.updatePassword(user_id, password), Duration.Inf)
  }

  //기존 비밀번호 맞는지 여부 확인
  def passwordCheck(bodyJson: JsValue): Boolean = {
    val user_id = (bodyJson \ "user_id").as[String].toInt
    val password = (bodyJson \ "exist_password").as[String]
    val user = Await.result(usersService.findById(user_id), Duration.Inf)
    if (user.isDefined && BCrypt.checkpw(password, user.get.password.get)) {
      true
    } else {
      false
    }
  }

  //전화번호 변경
  def phoneChange(bodyJson: JsValue) = {
    val user_id = (bodyJson \ "user_id").as[String].toInt
    val phone = (bodyJson \ "phone").as[String]

    Await.result(usersService.updatePhone(user_id, phone), Duration.Inf)
  }

  //과목별 성적 분석
  def getAnalysisData(bodyJson: JsValue): Map[String, String] = {
    val studentId = (bodyJson \ "student_id").as[Int]
    val subjectId = (bodyJson \ "subject_id").as[Int]
    var data = new ListBuffer[AnalysisGradeData]

    if (subjectId == 70000) { //탐구의 경우 해당 학생별로 선택한 선택과목에 대해서만 정보 조회
      val optionalSubject = Await.result(optionalSubjectsService.getSubjectIdByStudentId(studentId), Duration.Inf)
      optionalSubject.foreach { row =>
        val title = Await.result(subjectsService.getTitleBySubjectId(row), Duration.Inf).get.get
        val ratings = Await.result(examRecordService.getAnalysisGradeRating(studentId, row), Duration.Inf)
        val percentiles = Await.result(examRecordService.getAnalysisGradePercentile(studentId, row), Duration.Inf)
        data +== AnalysisGradeData(title = title, ratings = ratings.toList, percentiles = percentiles.toList)
      }
    } else {
      val ratings = Await.result(examRecordService.getAnalysisGradeRating(studentId, subjectId), Duration.Inf)
      val percentiles = Await.result(examRecordService.getAnalysisGradePercentile(studentId, subjectId), Duration.Inf)
      data +== AnalysisGradeData(title = "", ratings = ratings.toList, percentiles = percentiles.toList)
    }

    implicit val formats = Serialization.formats(NoTypeHints)
    var result: Map[String, String] = new HashMap[String, String]
    result += ("result" -> "success")
    result += ("data" -> write(data.toList))
    result
  }

  //성적분석 전체
  def analysisStudyAll(bodyJson: JsValue): Map[String, String] = {
    val studentId = (bodyJson \ "student_id").as[Int]
    val today = new java.sql.Date(new java.util.Date().getTime)
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    val monday = new java.sql.Date(new java.util.Date(cal.getTimeInMillis()).getTime)

    //일별로 기록된 통계자료를 월요일 부터 현재 날짜까지 합산
    val studyTimeThisWeek = Await.result(studyTimeService.findByStudentIdAndStartAndEnd(studentId, monday, today), Duration.Inf)
    val dataForAnalysisStudy = new DataForAnalysisStudy
    studyTimeThisWeek.foreach { row =>
      dataForAnalysisStudy.addTotalStudy(row.totalStudy.get)
      dataForAnalysisStudy.addThisWeekKorea(row.korea.get)
      dataForAnalysisStudy.addThisWeekMath(row.math.get)
      dataForAnalysisStudy.addThisWeekEnglish(row.english.get)
      dataForAnalysisStudy.addThisWeekSocialScience(row.socialScience.get)
      dataForAnalysisStudy.addThisWeekNonsubject(row.nonsubject.get)
      dataForAnalysisStudy.addSubject(row.subject.get)
      dataForAnalysisStudy.addAlone(row.alone.get)
      val calTemp = Calendar.getInstance()
      calTemp.setTime(row.createdAt.get)
      calTemp.get(Calendar.DAY_OF_WEEK) match {
        case 1 => dataForAnalysisStudy.addSunday(row.totalStudy.get)
        case 2 => dataForAnalysisStudy.addMonday(row.totalStudy.get)
        case 3 => dataForAnalysisStudy.addTuesday(row.totalStudy.get)
        case 4 => dataForAnalysisStudy.addWednesday(row.totalStudy.get)
        case 5 => dataForAnalysisStudy.addThursday(row.totalStudy.get)
        case 6 => dataForAnalysisStudy.addFriday(row.totalStudy.get)
        case 7 => dataForAnalysisStudy.addSaturday(row.totalStudy.get)
      }
    }
    //상위 10%의 데이터 추출
    var numOfDate = 0
    var tempDate = new java.sql.Date(new java.util.Date(cal.getTimeInMillis()).getTime)
    while (tempDate.getTime <= today.getTime) {
      val count = Await.result(studyTimeService.countByDate(tempDate), Duration.Inf).toFloat
      val offset = math.ceil(count / 10).toInt
      val highRankStudy = Await.result(studyTimeService.findHighRankByDate(tempDate, offset), Duration.Inf)
      val highRankGoalTime = Await.result(studyTimeService.findHighRankStudentByDate(tempDate, monday, offset), Duration.Inf)
      val calTemp = Calendar.getInstance()
      calTemp.setTime(tempDate)
      if (highRankStudy.isDefined) {
        val highRankStudyTime = highRankStudy.get
        dataForAnalysisStudy.addHighRankTotalStudy(highRankStudyTime)
        println("highRankStudyTime: " + highRankStudyTime)
        calTemp.get(Calendar.DAY_OF_WEEK) match {
          case 1 => dataForAnalysisStudy.highRankSunday = highRankStudyTime
          case 2 => dataForAnalysisStudy.highRankMonday = highRankStudyTime
          case 3 => dataForAnalysisStudy.highRankTuesday = highRankStudyTime
          case 4 => dataForAnalysisStudy.highRankWednesday = highRankStudyTime
          case 5 => dataForAnalysisStudy.highRankThursday = highRankStudyTime
          case 6 => dataForAnalysisStudy.highRankFriday = highRankStudyTime
          case 7 => dataForAnalysisStudy.highRankSaturday = highRankStudyTime
        }
      }
      if (highRankGoalTime.isDefined) {
        dataForAnalysisStudy.addHighRankGoalTime(highRankGoalTime.get)
        println("highRankGoalTime: " + highRankGoalTime.get)
      }
      numOfDate = numOfDate + 1
      calTemp.add(Calendar.DATE, 1)
      tempDate = new java.sql.Date(new java.util.Date(calTemp.getTimeInMillis()).getTime)
    }

    //지난주의 데이터 추출
    cal.add(Calendar.DATE, -7)
    val lastMonday = new java.sql.Date(new java.util.Date(cal.getTimeInMillis()).getTime)
    cal.add(Calendar.DATE, 6)
    val lastSunday = new java.sql.Date(new java.util.Date(cal.getTimeInMillis()).getTime)
    val studyTimeLastWeek = Await.result(studyTimeService.findByStudentIdAndStartAndEnd(studentId, lastMonday, lastSunday), Duration.Inf)
    studyTimeLastWeek.foreach { row =>
      dataForAnalysisStudy.addLastWeekKorea(row.korea.get)
      dataForAnalysisStudy.addLastWeekMath(row.math.get)
      dataForAnalysisStudy.addLastWeekEnglish(row.english.get)
      dataForAnalysisStudy.addLastWeekSocialScience(row.socialScience.get)
      dataForAnalysisStudy.addLastWeekNonsubject(row.nonsubject.get)
    }

    //데이터 전송을 위해 형식 변경
    var result: Map[String, String] = new HashMap[String, String]
    result += ("result" -> "success")

    val totalStudyString = getStringFormat(dataForAnalysisStudy.totalStudy)
    val highRankTotalStudyString = getStringFormat(dataForAnalysisStudy.highRankTotalStudy)
    val gapString = getStringFormat(dataForAnalysisStudy.highRankTotalStudy - dataForAnalysisStudy.totalStudy)
    val highRankGoalTime = getStringFormat(dataForAnalysisStudy.getHighRankGoalTime(numOfDate))

    result += ("total_study_time" -> totalStudyString)
    result += ("high_rank_study_time" -> highRankTotalStudyString)
    //    result += ("high_rank_study_time" -> "70:00:00")
    result += ("gap" -> gapString)
    result += ("hign_rank_goal_time" -> highRankGoalTime)
    result += ("this_week_korean" -> dataForAnalysisStudy.thisWeekKorea.toString)
    result += ("this_week_math" -> dataForAnalysisStudy.thisWeekMath.toString)
    result += ("this_week_english" -> dataForAnalysisStudy.thisWeekEnglish.toString)
    result += ("this_week_social_science" -> dataForAnalysisStudy.thisWeekSocialScience.toString)
    result += ("this_week_nonsubject" -> dataForAnalysisStudy.thisWeekNonsubject.toString)
    result += ("daily_sunday" -> dataForAnalysisStudy.sunday.toString)
    result += ("daily_monday" -> dataForAnalysisStudy.monday.toString)
    result += ("daily_tuesday" -> dataForAnalysisStudy.tuesday.toString)
    result += ("daily_wednesday" -> dataForAnalysisStudy.wednesday.toString)
    result += ("daily_thursday" -> dataForAnalysisStudy.thursday.toString)
    result += ("daily_friday" -> dataForAnalysisStudy.friday.toString)
    result += ("daily_saturday" -> dataForAnalysisStudy.saturday.toString)
    result += ("daily_high_rank_sunday" -> dataForAnalysisStudy.highRankSunday.toString)
    result += ("daily_high_rank_monday" -> dataForAnalysisStudy.highRankMonday.toString)
    result += ("daily_high_rank_tuesday" -> dataForAnalysisStudy.highRankTuesday.toString)
    result += ("daily_high_rank_wednesday" -> dataForAnalysisStudy.highRankWednesday.toString)
    result += ("daily_high_rank_thursday" -> dataForAnalysisStudy.highRankThursday.toString)
    result += ("daily_high_rank_friday" -> dataForAnalysisStudy.highRankFriday.toString)
    result += ("daily_high_rank_saturday" -> dataForAnalysisStudy.highRankSaturday.toString)
    result += ("last_week_korean" -> dataForAnalysisStudy.lastWeekKorea.toString)
    result += ("last_week_math" -> dataForAnalysisStudy.lastWeekMath.toString)
    result += ("last_week_english" -> dataForAnalysisStudy.lastWeekEnglish.toString)
    result += ("last_week_social_science" -> dataForAnalysisStudy.lastWeekSocialScience.toString)
    result += ("last_week_nonsubject" -> dataForAnalysisStudy.lastWeekNonsubject.toString)

    if (dataForAnalysisStudy.subject != 0 || dataForAnalysisStudy.thisWeekNonsubject != 0) {
      val sum = dataForAnalysisStudy.subject + dataForAnalysisStudy.thisWeekNonsubject
      val subject = (10 * dataForAnalysisStudy.subject / sum).toInt
      val nonsubject = (10 * dataForAnalysisStudy.thisWeekNonsubject / sum).toInt
      val value = subject.toString + " : " + nonsubject.toString
      result += ("subject_nonsubject" -> value)
    } else {
      result += ("subject_nonsubject" -> "0 : 0")
    }
    if (dataForAnalysisStudy.totalStudy == 0) {
      result += ("study_alone" -> "0%")
    } else {
      val studyAlone = (100 * dataForAnalysisStudy.alone / dataForAnalysisStudy.totalStudy).toString + "%"
      result += ("study_alone" -> studyAlone)
    }


    val studyGoal = Await.result(StudyGoalService.findByStudentIdAndMonday(studentId, monday), Duration.Inf)
    if (studyGoal.isDefined && studyGoal.get.goalTime.get != 0) {
      val goalTime = studyGoal.get.goalTime.get
      result += ("goal_time" -> getStringFormat(goalTime))
      val goalAchieveTime = (dataForAnalysisStudy.totalStudy / goalTime) * 100
      result += ("goal_achieve_ratio" -> goalAchieveTime.toInt.toString)
    } else {
      result += ("goal_time" -> "00:00:00")
      result += ("goal_achieve_ratio" -> "100")
    }
    result
  }

  //시간 String으로 변경
  def getStringFormat(time: Long): String = {
    var totalStudyString = "00:00:00"
    if (time != 0) {
      val hour = (time / 3600).toInt
      val min = ((time - (hour * 3600)) / 60).toInt
      val second = time - (hour * 3600) - (min * 60)
      totalStudyString = "%02d".format(hour) + ":" + "%02d".format(min) + ":" + "%02d".format(second)
    }
    totalStudyString
  }

  //하루일기 데이터 조회
  def analysisStudyDaily(bodyJson: JsValue): Map[String, String] = {
    val studentId = (bodyJson \ "student_id").as[Int]
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd")
    val date = format.parse((bodyJson \ "date").as[String])
    val createdAt = new java.sql.Date(date.getTime)

    //해당 학생의 하루일기 데이터 추출
    val myStudyTime = Await.result(studyTimeService.findByStudentIdAndCreatedAt(studentId, createdAt), Duration.Inf)

    var result: Map[String, String] = new HashMap[String, String]
    result += ("result" -> "success")
    val dataForAnalysisStudy = new DataForAnalysisStudy

    if (myStudyTime.isDefined) {
      val myStudyTimeValue = myStudyTime.get
      dataForAnalysisStudy.subject = myStudyTimeValue.subject.get
      dataForAnalysisStudy.thisWeekNonsubject = myStudyTimeValue.nonsubject.get
      dataForAnalysisStudy.sleep = myStudyTimeValue.sleep.get
      dataForAnalysisStudy.eat = myStudyTimeValue.eat.get
      dataForAnalysisStudy.rest = myStudyTimeValue.rest.get
      dataForAnalysisStudy.etc = myStudyTimeValue.etc.get
    }

    result += ("subject" -> dataForAnalysisStudy.subject.toString)
    result += ("nonsubject" -> dataForAnalysisStudy.thisWeekNonsubject.toString)
    result += ("sleep" -> dataForAnalysisStudy.sleep.toString)
    result += ("eat" -> dataForAnalysisStudy.eat.toString)
    result += ("rest" -> dataForAnalysisStudy.rest.toString)
    result += ("etc" -> dataForAnalysisStudy.etc.toString)
    result += ("waste" -> dataForAnalysisStudy.getWasteTime(1).toString)

    //상위 10%의 하루일기 데이터 추출
    val highRankData = new DataForAnalysisStudy
    val count = Await.result(studyTimeService.countByDate(createdAt), Duration.Inf).toFloat
    val offset = math.ceil(count / 10).toInt
    val highRankStudy = Await.result(studyTimeService.findHighRankByDateAll(createdAt, offset), Duration.Inf)
    highRankStudy.foreach { row =>
      highRankData.addSubject(row.subject.get)
      highRankData.addThisWeekNonsubject(row.nonsubject.get)
      highRankData.addSleep(row.sleep.get)
      highRankData.addEat(row.eat.get)
      highRankData.addRest(row.rest.get)
      highRankData.addEtc(row.etc.get)
    }

    result += ("high_rank_subject" -> highRankData.subject.toString)
    result += ("high_rank_nonsubject" -> highRankData.thisWeekNonsubject.toString)
    result += ("high_rank_sleep" -> highRankData.sleep.toString)
    result += ("high_rank_eat" -> highRankData.eat.toString)
    result += ("high_rank_rest" -> highRankData.rest.toString)
    result += ("high_rank_etc" -> highRankData.etc.toString)
    if (offset == 0) {
      result += ("high_rank_waste" -> highRankData.getWasteTime(1).toString)
    } else {
      result += ("high_rank_waste" -> highRankData.getWasteTime(offset).toString)
    }
    result
  }

  //학습분석 과목별 데이터 조회
  def analysisStudySubject(bodyJson: JsValue): Map[String, String] = {
    val studentId = (bodyJson \ "student_id").as[Int]
    val subjectId = (bodyJson \ "subject_id").as[Int]

    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    val monday = new java.sql.Date(new java.util.Date(cal.getTimeInMillis()).getTime)

    var contentsAnalysis = new ListBuffer[TitleTimes]()
    var typeAnalysis = new ListBuffer[TitleTimes]()
    var partAnalysis = new ListBuffer[TitleTimes]()
    var totalStudy: Long = 0

    val studyTimeSubject = Await.result(studyTimeSubjectService.findByStudentIdAndMondayAndTypeId(studentId, monday, subjectId), Duration.Inf)


    studyTimeSubject.foreach { row =>

      totalStudy = totalStudy + row.totalStudy.get
      //방법별 데이터 추출
      var titleTimeContents = new ListBuffer[TitleTime]()
      titleTimeContents += TitleTime(title = "기본", time = row.contentsBasic.get)
      titleTimeContents += TitleTime(title = "문제풀이", time = row.contentsSolution.get)
      titleTimeContents += TitleTime(title = "실전연습", time = row.contentsReal.get)
      titleTimeContents += TitleTime(title = "EBS", time = row.contentsEbs.get)
      contentsAnalysis += TitleTimes(title = row.title.get, times = titleTimeContents.toList)

      //교재별 데이터 추출
      var titleTimeType = new ListBuffer[TitleTime]()
      titleTimeType += TitleTime(title = "자습", time = row.typeIndependently.get)
      titleTimeType += TitleTime(title = "학원", time = row.typePrivateEdu.get)
      titleTimeType += TitleTime(title = "학교", time = row.typeSchool.get)
      titleTimeType += TitleTime(title = "과외", time = row.typePrivateTeacher.get)
      titleTimeType += TitleTime(title = "인강", time = row.typeLecture.get)
      titleTimeType += TitleTime(title = "오답", time = row.typeCheck.get)
      titleTimeType += TitleTime(title = "모의고사", time = row.typeExam.get)
      typeAnalysis += TitleTimes(title = row.title.get, times = titleTimeType.toList)

      val studyTimeSubSubject = Await.result(studyTimeSubSubjectService.findByStudyTimeSubjectId(row.id), Duration.Inf)
      //내용별 데이터 추출
      var titleTimePart = new ListBuffer[TitleTime]()
      studyTimeSubSubject.foreach { row =>
        var subjectId = row.subjectId.get
        if (subjectId < 10000) {
          subjectId = subjectId + 30000
        }
        val title = Await.result(subjectsService.getTitleBySubjectId(subjectId), Duration.Inf).get.get
        titleTimePart += TitleTime(title = title, time = row.time.get)
      }

      partAnalysis += TitleTimes(title = row.title.get, times = titleTimePart.toList)
    }

    //방법별 데이터가 없을경우 0으로 초기화
    if (contentsAnalysis.toList.length == 0) {
      var titleTimeContents = new ListBuffer[TitleTime]()
      titleTimeContents += TitleTime(title = "기본", time = 0)
      titleTimeContents += TitleTime(title = "문제풀이", time = 0)
      titleTimeContents += TitleTime(title = "실전연습", time = 0)
      titleTimeContents += TitleTime(title = "EBS", time = 0)
      contentsAnalysis += TitleTimes(title = "", times = titleTimeContents.toList)
    }
    //교재별 데이터가 없을경우 0으로 초기화
    if (typeAnalysis.toList.length == 0) {
      var titleTimeType = new ListBuffer[TitleTime]()
      titleTimeType += TitleTime(title = "자습", time = 0)
      titleTimeType += TitleTime(title = "학원", time = 0)
      titleTimeType += TitleTime(title = "학교", time = 0)
      titleTimeType += TitleTime(title = "과외", time = 0)
      titleTimeType += TitleTime(title = "인강", time = 0)
      titleTimeType += TitleTime(title = "오답", time = 0)
      titleTimeType += TitleTime(title = "모의고사", time = 0)
      typeAnalysis += TitleTimes(title = "", times = titleTimeType.toList)
    }
    if (partAnalysis.toList.length == 0) {
      var titleTimePart = new ListBuffer[TitleTime]()
      //과목별 데이터 분류
      subjectId match {
        case 10000 => { //국어
          val temp = new DataForAnalysisStudySubjectKorean
          titleTimePart += TitleTime(title = temp.getTitle1(), time = temp.subject1)
          titleTimePart += TitleTime(title = temp.getTitle2(), time = temp.subject2)
          titleTimePart += TitleTime(title = temp.getTitle3(), time = temp.subject3)
          titleTimePart += TitleTime(title = temp.getTitle4(), time = temp.subject4)
        }
        case 30000 => { //수학
          val mathType = Await.result(studentsService.getMathTypeByStudentId(studentId), Duration.Inf).get
          mathType match { //가형
            case 1 => {
              val temp = new DataForAnalysisStudySubjectMathGa
              titleTimePart += TitleTime(title = temp.getTitle1(), time = temp.subject1)
              titleTimePart += TitleTime(title = temp.getTitle2(), time = temp.subject2)
              titleTimePart += TitleTime(title = temp.getTitle3(), time = temp.subject3)
              titleTimePart += TitleTime(title = temp.getTitle4(), time = temp.subject4)
              titleTimePart += TitleTime(title = temp.getTitle5(), time = temp.subject5)
              titleTimePart += TitleTime(title = temp.getTitle6(), time = temp.subject6)
            }
            case 2 => { //나형
              val temp = new DataForAnalysisStudySubjectMathNa
              titleTimePart += TitleTime(title = temp.getTitle1(), time = temp.subject1)
              titleTimePart += TitleTime(title = temp.getTitle2(), time = temp.subject2)
              titleTimePart += TitleTime(title = temp.getTitle3(), time = temp.subject3)
              titleTimePart += TitleTime(title = temp.getTitle4(), time = temp.subject4)
            }
          }

        }
        case 50000 => { //영어
          val temp = new DataForAnalysisStudySubjectEnglish
          titleTimePart += TitleTime(title = temp.getTitle1(), time = temp.subject1)
          titleTimePart += TitleTime(title = temp.getTitle2(), time = temp.subject2)
          titleTimePart += TitleTime(title = temp.getTitle3(), time = temp.subject3)
          titleTimePart += TitleTime(title = temp.getTitle4(), time = temp.subject4)
          titleTimePart += TitleTime(title = temp.getTitle5(), time = temp.subject5)
        }
        case _ =>
      }
      partAnalysis += TitleTimes(title = "", times = titleTimePart.toList)
    }

    //과목별 등수 추출
    var numOfStudent = Await.result(studyTimeSubjectService.countByDateAndTypeId(monday, subjectId), Duration.Inf)
    val myRankNumber = Await.result(studyTimeSubjectService.getMyRank(studentId, monday, subjectId, totalStudy), Duration.Inf)
    if (numOfStudent == 0) numOfStudent = 1
    var myRank = (myRankNumber / numOfStudent) * 100
    if (myRank == 0) myRank = 1

    implicit val formats = Serialization.formats(NoTypeHints)

    var result: Map[String, String] = new HashMap[String, String]
    result += ("result" -> "success")
    val hour = (totalStudy / 3600).toInt
    val min = ((totalStudy - (hour * 3600)) / 60).toInt
    val studyTimeString = hour.toString + "시간 " + min.toString + "분"
    result += ("total_study_time" -> studyTimeString)
    result += ("my_rank" -> myRank.toString)
    result += ("part_analysis" -> write(partAnalysis.toList))
    result += ("contents_analysis" -> write(contentsAnalysis.toList))
    result += ("type_analysis" -> write(typeAnalysis.toList))
    result
  }

  //게시물 삭제
  def deletePosting(bodyJson: JsValue) = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val postingId = (bodyJson \ "posting_id").as[Int]
    val posting = Await.result(postingsService.findById(postingId), Duration.Inf)
    if (posting.isDefined && posting.get.userId == userId) {
      Await.result(postingsService.deletePosting(postingId), Duration.Inf)
    } else {
      throw new Exception
    }
  }

  //게시물 신고
  def postingReport(bodyJson: JsValue): String = {
    val userId = (bodyJson \ "user_id").as[String].toInt
    val postingId = (bodyJson \ "posting_id").as[String].toInt
    val replyReport = Await.result(postingReportService.findByPostingIdAndUserId(postingId, userId), Duration.Inf)
    replyReport match {
      case Some(result) => {
        //이미 신고가 되어있을 경우 신고 삭제
        Await.result(postingReportService.delete(postingId, userId), Duration.Inf)
        "posting report cancel success"
      }
      case _ => {
        Await.result(postingReportService.insert(PostingReportRow(postingId, userId)), Duration.Inf)
        val existPosting = Await.result(postingsService.findById(postingId), Duration.Inf).get
        val user = Await.result(usersService.findById(existPosting.userId), Duration.Inf).get
        //신고를 당한 게시물의 작성자에게 푸쉬메세지 전송
        if(user.typeId == StaticValues.USER_TYPE_STUDENT){
          PushHelper.sendPushMessage(List(existPosting.userId), Properties.PUSH_MESSAGE_ID_STUDENT_REPORT, Properties.PUSH_CODE_STUDENT)
        }else if(user.typeId == StaticValues.USER_TYPE_MENTOR){
          PushHelper.sendPushMessage(List(existPosting.userId), Properties.PUSH_MESSAGE_ID_MENTOR_REPORT, Properties.PUSH_CODE_MENTOR_ANSWER)
        }
        "posting report success"
      }
    }
  }

  //push 설정 변경
  def updatePushSetting(bodyJson: JsValue) = {
    val user_id = (bodyJson \ "user_id").as[String].toInt
    val push = (bodyJson \ "push").as[Boolean]
    Await.result(usersService.updatePush(user_id, push), Duration.Inf)
  }

  //탈퇴
  def withdrawal(bodyJson: JsValue) = {
    val user_id = (bodyJson \ "user_id").as[String].toInt
    Await.result(usersService.withdrawal(user_id), Duration.Inf)
  }

  //push발송을 위한 device token 설정
  def setToken(bodyJson: JsValue) = {
    val user_id = (bodyJson \ "user_id").as[String].toInt
    val token = (bodyJson \ "token").as[String]
    Await.result(usersService.setToken(user_id, token), Duration.Inf)
  }

  //임시 비밀번호 전송
  def sendTempPassword(id: Int, email: String) = {
    val password = SecretGenerator.getTempPassword(10)
    Await.result(usersService.updatePassword(id, password), Duration.Inf)

    val commonsMail = new SimpleEmail()
    commonsMail.setHostName("smtp.gmail.com")
    commonsMail.setSmtpPort(587)
    commonsMail.setStartTLSEnabled(true)
    commonsMail.setAuthentication("coach.azitstudy@gmail.com", "azittrams15")
    commonsMail.setFrom("coach.azitstudy@gmail.com")
    commonsMail.addTo(email)
    commonsMail.setSubject("[Azit study] 임시비밀번호 입니다.")
    commonsMail.setMsg("임시비밀번호: " + password)
    commonsMail.send()
  }

  //공지사항 조회
  def getNotices(): Seq[NoticesRowForList] = {
    val notices = Await.result(noticesService.getAll(), Duration.Inf)
    notices
  }
}
