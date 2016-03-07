package controllers.admin

import caseClasses.ExamCriteriaDetail
import common.PushHelper
import controllers.api.StaticValues
import controllers.admin.Forms._
import controllers.authentication.Secured
import play.api.Logger
import play.api.mvc._
import services._
import tables.Tables.{ExamCriteriaRow, ExamRow}

import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class AdminExamController extends Controller with Secured{

  def examService = new ExamService

  def examCriteriaService = new ExamCriteriaService

  def subjectsService = new SubjectsService

  def studentsService = new StudentsService

  //모의고사 리스트 페이지
  def list(activePage: Int, examPage: Int, inActiveId: Int, activeId: Int, deleteId: Int) = withAuth { user => implicit request =>

    if(inActiveId != 0){ //모의고사 선택 취소
      Await.result(examService.updateIsActive(inActiveId, false), Duration.Inf)
    }
    if(activeId != 0){ //모의고사 선택
      Await.result(examService.updateIsActive(activeId, true), Duration.Inf)
      //학생들에게 새로운 모의고사 등록 알림
      val studentIds = Await.result(studentsService.getStudentIdsForAutoPush(), Duration.Inf)
      PushHelper.sendPushMessage(studentIds.toList, Properties.PUSH_MESSAGE_ID_STUDENT_EXAM, Properties.PUSH_CODE_STUDENT)
    }
    if(deleteId != 0){ //모의고사 삭제
      Await.result(examService.updateIsDelete(deleteId, true), Duration.Inf)
    }

    val pageSize = 5
    val aStartIndex = (activePage - 1) * pageSize
    val eStartIndex = (examPage - 1) * pageSize

    val activeList = Await.result(examService.getActiveExamList(aStartIndex, pageSize), Duration.Inf)
    val activeSize = Await.result(examService.countActiveExamList(), Duration.Inf)
    val examList = Await.result(examService.getExamList(eStartIndex, pageSize), Duration.Inf)
    val examSize = Await.result(examService.countExamList(), Duration.Inf)

    Ok(views.html.admin.exam.list(pageSize, activePage, activeList, activeSize, examPage, examList, examSize))
  }

  //모의고사 상세 페이지
  def detail(id: Int, subject: Int) = withAuth { user => implicit request =>
    val subjects = getSubjects(subject)
    val examCriteriaDetail = getExamDetail(id, subjects)

    Ok(views.html.admin.exam.detail(id, subject, examCriteriaDetail))
  }

  //과목 분류별 세부 과목 정보 조회
  def getSubjects(subject: Int): List[Int] = {
    val subjects = new ListBuffer[Int]()
    if(subject == 10000 || subject == 50000){
      subjects.append(subject)
    }else if(subject == 30000){
      subjects.append(20000)
      subjects.append(30000)
    }else{
      val depth1 = subject / 10000
      val tempSubjects = Await.result(subjectsService.getSubjectsByDepth1(depth1), Duration.Inf)
      subjects.appendAll(tempSubjects)
    }
    subjects.toList
  }

  //모의고사 등급별 점수를 입력을 위한 항목 조회
  def getExamDetail(examId: Int, subjects: List[Int]): List[ExamCriteriaDetail] = {
    val examDetails = new ListBuffer[ExamCriteriaDetail]()
    subjects.foreach { row =>
      val title = Await.result(subjectsService.getTitleBySubjectId(row), Duration.Inf)
      val score = Await.result(examCriteriaService.findOneByExamAndSubjectAndType(examId, row, StaticValues.EXAM_RECORD_TYPE_SCORE), Duration.Inf)
      val standard = Await.result(examCriteriaService.findOneByExamAndSubjectAndType(examId, row, StaticValues.EXAM_RECORD_TYPE_STANDARD), Duration.Inf)
      val percentile = Await.result(examCriteriaService.findOneByExamAndSubjectAndType(examId, row, StaticValues.EXAM_RECORD_TYPE_PERCENTILE), Duration.Inf)
      if(score.isDefined && standard.isDefined && percentile.isDefined){
        examDetails += ExamCriteriaDetail(title = title.get.get, score = score.get, standard = standard.get, percentile = percentile.get)
      }
    }
    examDetails.toList
  }

  //모의고사 등급별 점수 저장
  def saveExamDetail = Action { implicit request =>
    examCriteriaForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("save exam detail")
        println(formWithErrors)
        BadRequest("fail")
      },
      examDetail => {
        val score = Await.result(examCriteriaService.findById(examDetail.score.id), Duration.Inf)
        val standard = Await.result(examCriteriaService.findById(examDetail.standard.id), Duration.Inf)
        val percentile = Await.result(examCriteriaService.findById(examDetail.percentile.id), Duration.Inf)
        if(score.isDefined){
          Await.result(examCriteriaService.update(score.get.copy(tier1Max = examDetail.score.tier1Max, tier1Min = examDetail.score.tier1Min, tier2Max = examDetail.score.tier2Max, tier2Min = examDetail.score.tier2Min, tier3Max = examDetail.score.tier3Max, tier3Min = examDetail.score.tier3Min, tier4Max = examDetail.score.tier4Max, tier4Min = examDetail.score.tier4Min, tier5Max = examDetail.score.tier5Max, tier5Min = examDetail.score.tier5Min, tier6Max = examDetail.score.tier6Max, tier6Min = examDetail.score.tier6Min, tier7Max = examDetail.score.tier7Max, tier7Min = examDetail.score.tier7Min, tier8Max = examDetail.score.tier8Max, tier8Min = examDetail.score.tier8Min, tier9Max = examDetail.score.tier9Max, tier9Min = examDetail.score.tier9Min)), Duration.Inf)
        }
        if(standard.isDefined){
          Await.result(examCriteriaService.update(standard.get.copy(tier1Max = examDetail.standard.tier1Max, tier1Min = examDetail.standard.tier1Min, tier2Max = examDetail.standard.tier2Max, tier2Min = examDetail.standard.tier2Min, tier3Max = examDetail.standard.tier3Max, tier3Min = examDetail.standard.tier3Min, tier4Max = examDetail.standard.tier4Max, tier4Min = examDetail.standard.tier4Min, tier5Max = examDetail.standard.tier5Max, tier5Min = examDetail.standard.tier5Min, tier6Max = examDetail.standard.tier6Max, tier6Min = examDetail.standard.tier6Min, tier7Max = examDetail.standard.tier7Max, tier7Min = examDetail.standard.tier7Min, tier8Max = examDetail.standard.tier8Max, tier8Min = examDetail.standard.tier8Min, tier9Max = examDetail.standard.tier9Max, tier9Min = examDetail.standard.tier9Min)), Duration.Inf)
        }
        if(percentile.isDefined){
          Await.result(examCriteriaService.update(percentile.get.copy(tier1Max = examDetail.percentile.tier1Max, tier1Min = examDetail.percentile.tier1Min, tier2Max = examDetail.percentile.tier2Max, tier2Min = examDetail.percentile.tier2Min, tier3Max = examDetail.percentile.tier3Max, tier3Min = examDetail.percentile.tier3Min, tier4Max = examDetail.percentile.tier4Max, tier4Min = examDetail.percentile.tier4Min, tier5Max = examDetail.percentile.tier5Max, tier5Min = examDetail.percentile.tier5Min, tier6Max = examDetail.percentile.tier6Max, tier6Min = examDetail.percentile.tier6Min, tier7Max = examDetail.percentile.tier7Max, tier7Min = examDetail.percentile.tier7Min, tier8Max = examDetail.percentile.tier8Max, tier8Min = examDetail.percentile.tier8Min, tier9Max = examDetail.percentile.tier9Max, tier9Min = examDetail.percentile.tier9Min)), Duration.Inf)
        }
        Ok("success")
      }
    )
  }

  //모의고사 추가
  def addExam() = withAuth { user => implicit request =>
    val bodyJson = request.body.asJson.get
    val title = (bodyJson \ "title").as[String]
    val year = (bodyJson \ "year").as[String].toInt
    val month = (bodyJson \ "month").as[String].toInt
    val result = "success"
    val examId = Await.result(examService.insert(ExamRow(id=0, title = Option(title), year = Option(year), month = Option(month), isActive = Option(false), isDelete = Option(false))), Duration.Inf)
    //모든과목에 대한 시험 점수 기준 추가
    val subjects = List(10000, 20000, 30000, 50000, 60100, 60200, 60300, 60400, 60500, 60600, 60700, 60800, 60900, 61000, 70100, 70200, 70300, 70400, 70500, 70600, 70700, 70800, 80100, 80200, 80300, 80400, 80500, 80600, 80700, 80800, 80900)
    subjects.foreach{row =>
      Await.result(examCriteriaService.insert(ExamCriteriaRow(id=0, examId = examId, subjectId = row, examRecordTypeId = StaticValues.EXAM_RECORD_TYPE_SCORE)), Duration.Inf)
      Await.result(examCriteriaService.insert(ExamCriteriaRow(id=0, examId = examId, subjectId = row, examRecordTypeId = StaticValues.EXAM_RECORD_TYPE_PERCENTILE)), Duration.Inf)
      Await.result(examCriteriaService.insert(ExamCriteriaRow(id=0, examId = examId, subjectId = row, examRecordTypeId = StaticValues.EXAM_RECORD_TYPE_STANDARD)), Duration.Inf)
    }
    Ok(result)
  }

}