package controllers.admin

import caseClasses.{AnswerContentsForAdmin, PostingContentsForAdmin, PostingImagesForAdmin}
import common.PushHelper
import controllers.api.StaticValues
import controllers.api.helpers.CommonHelper
import controllers.authentication.Secured
import play.api.mvc._
import services._
import tables.Tables.{PostingsRow, SaveHistoryRow}

import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class AdminBoardsController extends Controller with Secured {

  var pageSize = Properties.PAGE_SIZE

  def memberService = new MemberService

  def mentorsService = new MentorsService

  def tsMatchingService = new TSMatchingService

  def usersService = new UsersService

  def examService = new ExamService

  def postingsForAdminService = new PostingsForAdminService

  def postingImagesService = new PostingImagesService

  def repliesService = new RepliesService

  def replyReportService = new ReplyReportService

  def postingReportService = new PostingReportService

  //멘토만나기 리스트
  def mentoringList(p: Int, types: String, key: String, id: Int) = withAuth { user => implicit request =>

    if (id != 0) { //멘토만나기 삭제 요청 처리
      Await.result(postingsForAdminService.deletePosting(id), Duration.Inf)
    }

    val startIndex = (p - 1) * pageSize

    //검색 키워드 지정
    var nickname = ""
    var contents = ""
    var title = ""
    var contentsTitle = ""
    types match {
      case "nickname" => nickname = key
      case "contents" => contents = key
      case "title" => title = key
      case "contentsTitle" => contentsTitle = key
    }

    val list = Await.result(postingsForAdminService.getPostingsForList(StaticValues.MENTORING_TYPE_ID, nickname, contents, title, contentsTitle, List(), startIndex, pageSize), Duration.Inf)
    val totalSize = Await.result(postingsForAdminService.countPostingsForList(StaticValues.MENTORING_TYPE_ID, nickname, contents, title, contentsTitle, List()), Duration.Inf)
    Ok(views.html.admin.boards.mentoring_list(totalSize, list, p, pageSize, key, types))
  }

  //멘토만나기 상세페이지
  def mentoringDetail(id: Int, deletePosting: Int, deleteReply: Int, choiceCancel: Int) = withAuth { user => implicit request =>

    if (deletePosting != 0) { //답변 삭제
      Await.result(postingsForAdminService.deletePosting(deletePosting), Duration.Inf)
    }
    if (deleteReply != 0) { //댓글 삭제
      Await.result(repliesService.deleteReply(deleteReply), Duration.Inf)
    }
    if (choiceCancel != 0) { //채택 취소
      cancelChoice(choiceCancel)
    }
    val posting = getPosting(id)

    Ok(views.html.admin.boards.mentoring_detail(posting))
  }

  //멘토만나기 신고접수 페이지
  def mentoringReport(postingPage: Int, answerPage:Int, replyPage: Int, actionType: Int, posting: Int, userId: Int) = withAuth { user => implicit request =>

    if (actionType != 0) {
      actionType match{
        case 1 => Await.result(postingsForAdminService.deletePosting(posting), Duration.Inf) //게시물 삭제
        case 2 => Await.result(repliesService.deleteReply(posting), Duration.Inf) //댓글 삭제
        case 3 => Await.result(postingReportService.delete(posting, userId), Duration.Inf) //게시물 신고 삭제
        case 4 => Await.result(replyReportService.delete(posting, userId), Duration.Inf) //댓글 신고 삭제
      }
    }

    val pageSize = 5
    val pStartIndex = (postingPage - 1) * pageSize
    val aStartIndex = (answerPage - 1) * pageSize
    val rStartIndex = (replyPage - 1) * pageSize

    val pList = Await.result(postingsForAdminService.getPostingForReport(StaticValues.MENTORING_TYPE_ID, pStartIndex, pageSize), Duration.Inf)
    val pSize = Await.result(postingsForAdminService.countPostingForReport(StaticValues.MENTORING_TYPE_ID), Duration.Inf)
    val aList = Await.result(postingsForAdminService.getAnswerForReport(StaticValues.MENTORING_TYPE_ID, aStartIndex, pageSize), Duration.Inf)
    val aSize = Await.result(postingsForAdminService.countAnswerForReport(StaticValues.MENTORING_TYPE_ID), Duration.Inf)
    val rList = Await.result(postingsForAdminService.getRepliesForReport(StaticValues.MENTORING_TYPE_ID, rStartIndex, pageSize), Duration.Inf)
    val rSize = Await.result(postingsForAdminService.countRepliesForReport(StaticValues.MENTORING_TYPE_ID), Duration.Inf)

    Ok(views.html.admin.boards.mentoring_report(postingPage, answerPage, replyPage, pageSize, pList, pSize, aList, aSize, rList, rSize))
  }

  //풀어주세요 리스트
  def questionList(p: Int, types: String, key: String, id: Int, subjects: List[Int]) = withAuth { user => implicit request =>

    if (id != 0) { //풀어주세요 삭제 요청 처리
      Await.result(postingsForAdminService.deletePosting(id), Duration.Inf)
    }

    val startIndex = (p - 1) * pageSize

    //검색 키워드 지정
    var nickname = ""
    var contents = ""
    var title = ""
    var contentsTitle = ""
    types match {
      case "nickname" => nickname = key
      case "contents" => contents = key
      case "title" => title = key
      case "contentsTitle" => contentsTitle = key
    }

    val list = Await.result(postingsForAdminService.getPostingsForList(StaticValues.CLINIC_TYPE_ID, nickname, contents, title, contentsTitle, subjects, startIndex, pageSize), Duration.Inf)
    val totalSize = Await.result(postingsForAdminService.countPostingsForList(StaticValues.CLINIC_TYPE_ID, nickname, contents, title, contentsTitle, subjects), Duration.Inf)
    Ok(views.html.admin.boards.question_list(totalSize, list, p, pageSize, key, types, subjects))
  }

  //풀어주세요 상세페이지
  def questionDetail(id: Int, deletePosting: Int, deleteReply: Int, choiceCancel: Int) = withAuth { user => implicit request =>

    if (deletePosting != 0) { //게시물 삭제
      Await.result(postingsForAdminService.deletePosting(deletePosting), Duration.Inf)
    }
    if (deleteReply != 0) { //댓글 삭제
      Await.result(repliesService.deleteReply(deleteReply), Duration.Inf)
    }
    if (choiceCancel != 0) { //채택 취소
      cancelChoice(choiceCancel)
    }

    val posting = getPosting(id)

    Ok(views.html.admin.boards.question_detail(posting))
  }

  //풀어주세요 신고접수 페이지
  def questionReport(postingPage: Int, answerPage:Int, replyPage: Int, actionType: Int, posting: Int, userId: Int) = withAuth { user => implicit request =>

    if (actionType != 0) {
      actionType match{
        case 1 => Await.result(postingsForAdminService.deletePosting(posting), Duration.Inf) //게시물 삭제
        case 2 => Await.result(repliesService.deleteReply(posting), Duration.Inf) //댓글 삭제
        case 3 => Await.result(postingReportService.delete(posting, userId), Duration.Inf) //게시물 신고 삭제
        case 4 => Await.result(replyReportService.delete(posting, userId), Duration.Inf) //댓글 신고 삭제
      }
    }

    val pageSize = 5
    val pStartIndex = (postingPage - 1) * pageSize
    val aStartIndex = (answerPage - 1) * pageSize
    val rStartIndex = (replyPage - 1) * pageSize

    val pList = Await.result(postingsForAdminService.getPostingForReport(StaticValues.CLINIC_TYPE_ID, pStartIndex, pageSize), Duration.Inf)
    val pSize = Await.result(postingsForAdminService.countPostingForReport(StaticValues.CLINIC_TYPE_ID), Duration.Inf)
    val aList = Await.result(postingsForAdminService.getAnswerForReport(StaticValues.CLINIC_TYPE_ID, aStartIndex, pageSize), Duration.Inf)
    val aSize = Await.result(postingsForAdminService.countAnswerForReport(StaticValues.CLINIC_TYPE_ID), Duration.Inf)
    val rList = Await.result(postingsForAdminService.getRepliesForReport(StaticValues.CLINIC_TYPE_ID, rStartIndex, pageSize), Duration.Inf)
    val rSize = Await.result(postingsForAdminService.countRepliesForReport(StaticValues.CLINIC_TYPE_ID), Duration.Inf)


    Ok(views.html.admin.boards.question_report(postingPage, answerPage, replyPage, pageSize, pList, pSize, aList, aSize, rList, rSize))
  }

  //게시물 상세 페이지 정보 조회
  def getPosting(postingId: Int): PostingContentsForAdmin = {
    val posting = Await.result(postingsForAdminService.getPostingContents(postingId), Duration.Inf)
    val images = getImagesByPostingId(postingId)
    val replies = Await.result(postingsForAdminService.getRepliesByPostingId(postingId), Duration.Inf).toList
    val answers = Await.result(postingsForAdminService.getAnswerContents(postingId), Duration.Inf).toList
    var answersList = new ListBuffer[AnswerContentsForAdmin]()
    answers.foreach { row =>
      answersList += row.copy(replies = Option(Await.result(postingsForAdminService.getRepliesByPostingId(row.answer_id.get), Duration.Inf).toList))
    }
    posting.copy(images = Option(images), replies = Option(replies), answers = Option(answersList.toList))
  }

  //게시물 이미지 조회
  def getImagesByPostingId(id: Int): List[PostingImagesForAdmin] = {
    val imageRows = Await.result(postingImagesService.findByPostingId(id), Duration.Inf)
    var postingImages = new ListBuffer[PostingImagesForAdmin]()
    imageRows.foreach { row =>
      postingImages += PostingImagesForAdmin(id = row.id, image_url = row.imageUrl)
    }
    postingImages.toList
  }

  def postingsService = new PostingsService

  def saveHistoryService = new SaveHistoryService

  //채택 취소
  def cancelChoice(answerId: Int) = {
    var answerPosting: PostingsRow = Await.result(postingsService.findById(answerId), Duration.Inf).get
    var parentPosting: PostingsRow = Await.result(postingsService.findById(answerPosting.parentId.get), Duration.Inf).get
    answerPosting = answerPosting.copy(isChosen = Option(false))
    Await.result(postingsService.update(answerPosting), Duration.Inf)
    parentPosting = parentPosting.copy(isChosen = Option(false))
    Await.result(postingsService.update(parentPosting), Duration.Inf)
    //채택 취소된 답변 작성 멘토 포인트 삭감
    var mentor = Await.result(mentorsService.findById(answerPosting.userId), Duration.Inf).get
    val balance = mentor.point.get - StaticValues.ANSWER_POINT
    mentor = mentor.copy(point = Option(balance))
    Await.result(mentorsService.update(mentor), Duration.Inf)
    val today = new java.sql.Date(new java.util.Date().getTime)
    //적립 내역 추가
    Await.result(saveHistoryService.insert(SaveHistoryRow(id = 0, mentorId = Option(mentor.id), postingId = Option(parentPosting.id), balance = Option(balance), createdAt = Option(today), addedPoint = Option(-StaticValues.ANSWER_POINT))), Duration.Inf)
    //push 메세지 전송
    PushHelper.sendPushMessage(List(answerPosting.userId), Properties.PUSH_MESSAGE_ID_MENTOR_CHOICE_CANCEL, Properties.PUSH_CODE_MENTOR_ANSWER)
  }


}