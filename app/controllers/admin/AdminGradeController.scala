package controllers.admin

import caseClasses.{OtherSubjects, StudentsForGrade, KME}
import controllers.authentication.Secured
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import services._

import scala.collection.immutable.HashMap
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import tables.Tables.ExamRow
import scala.collection.mutable.ListBuffer

class AdminGradeController extends Controller with Secured{

  var pageSize = Properties.PAGE_SIZE

  def usersService = new UsersService

  def examService = new ExamService

  def studentsService = new StudentsService

  def optionalSubjectsService = new OptionalSubjectsService
  //학생 성적관리 리스트
  def list(page: Int, year: Int, month: Int, title: String, searchType: String, keyword: String) = withAuth { user => implicit request =>
    var exam: ExamRow = null
    var yearValue = year
    var monthValue = month
    var titleValue = title
    if(year == 0 && month == 0 && title == ""){ //검색 조건이 없을 경우 가장 최신의 시험을 기준으로 선정
      exam = Await.result(examService.getLatestExam(), Duration.Inf).get
      yearValue = exam.year.get
      monthValue = exam.month.get
      titleValue = exam.title.get
    }else{
      exam = Await.result(examService.searchByYearAndMonthAndTitle(year, month, title), Duration.Inf).get
    }
    val yearList = Await.result(examService.getYearList(), Duration.Inf)
    val monthList = Await.result(examService.getMonthList(exam.year.get), Duration.Inf)
    val titleList = Await.result(examService.getTitleList(exam.year.get, exam.month.get), Duration.Inf)

    //검색 키워드 지정
    var email = ""
    var name = ""
    searchType match {
      case "email" => email = keyword
      case "name" => name = keyword
    }

    val startIndex = (page - 1) * pageSize
    val tempList = Await.result(studentsService.getStudentForGrade(exam.id, email, name, startIndex, pageSize), Duration.Inf).toList
    val totalSize = Await.result(studentsService.countStudentForGrade(exam.id, email, name), Duration.Inf)
    val list = new ListBuffer[StudentsForGrade]()
    //학생별 입력된 성적데이터 추가
    tempList.foreach{row =>
      var tempRow = row
      val kme = Await.result(studentsService.getStudentRecord(row.id, exam.id), Duration.Inf)
      if(kme.isDefined){
        tempRow = row.copy(subject = Option(KME(korean = kme.get._1.get, math = kme.get._2.get, english = kme.get._3.get)), hasScore = Option("입력"))
        val optionalSubject = Await.result(optionalSubjectsService.getSubjectIdByStudentId(row.id), Duration.Inf)
        if(optionalSubject.length > 0){
          val otherSubject = Await.result(studentsService.getExamRecordsForStudent(row.id, exam.id, optionalSubject.toList), Duration.Inf)
          if(otherSubject.length > 0){
            val department = Await.result(studentsService.getDepartmentByStudentId(row.id), Duration.Inf)
            if(department == 1){
              tempRow = tempRow.copy(social = Option(otherSubject.toList))
            }else{
              tempRow = tempRow.copy(science = Option(otherSubject.toList))
            }
          }
        }
        val foreignLanguage = Await.result(studentsService.getForeignByStudentId(row.id), Duration.Inf).get
        if(foreignLanguage != 0){
          val foreignRecord = Await.result(studentsService.getExamRecordForStudent(row.id, exam.id, foreignLanguage), Duration.Inf)
          if(foreignRecord.isDefined){
            tempRow = tempRow.copy(foreign = Option(OtherSubjects(score = foreignRecord.get._1.get, title = foreignRecord.get._2.get)))
          }
        }
      }else{
        tempRow = row.copy(hasScore = Option("미입력"))
      }
      list += tempRow
    }
    Ok(views.html.admin.grade.list(page, pageSize, yearValue, monthValue, titleValue, list.toList, totalSize, searchType, keyword, yearList.toList, monthList.toList, titleList.toList))
  }

  //시험 년도 선택시 해당 년도에 포함되는 월 정보 조회
  def getMonthList() = withAuth { user => implicit request =>
    val bodyJson = request.body.asJson.get
    val year = (bodyJson \ "year").as[String].toInt
    val monthList = Await.result(examService.getMonthList(year), Duration.Inf)
    implicit val formats = Serialization.formats(NoTypeHints)
    var result: Map[String, String] = new HashMap[String, String]
    result += ("result" -> "success")
    result += ("month" -> write(monthList))
    Ok(Json.toJson(result))
  }

  //시험 월 선택시 해당 월에 포함되는 시험 정보 조회
  def getTitleList() = withAuth { user => implicit request =>
    val bodyJson = request.body.asJson.get
    val year = (bodyJson \ "year").as[String].toInt
    val month = (bodyJson \ "month").as[String].toInt
    val titleList = Await.result(examService.getTitleList(year, month), Duration.Inf)
    implicit val formats = Serialization.formats(NoTypeHints)
    var result: Map[String, String] = new HashMap[String, String]
    result += ("result" -> "success")
    result += ("title" -> write(titleList))
    Ok(Json.toJson(result))
  }
}