package controllers.api.helpers

import java.sql.Timestamp
import java.util.Calendar

import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import play.api.libs.json.{JsError, JsSuccess, JsValue}
import services._
import tables.Tables.{UserEduRow, PostingImagesRow, TSMatchingRow, TeachersRow}

import scala.collection.immutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object TeacherHelper {
  def teachersService = new TeachersService

  def tsMatchingService = new TSMatchingService

  def userEduService = new UserEduService

  def examService = new ExamService

  def studyGoalService = new StudyGoalService

  def studyTimeService = new StudyTimeService

  //선생 추가
  def insert(teacher: TeachersRow): Option[TeachersRow] = {
    Await.result(teachersService.insert(teacher), Duration.Inf)
    Await.result(teachersService.findById(teacher.id), Duration.Inf)
  }

  //학생 검색
  def searchStudents(bodyJson: JsValue): List[StudentsForSearch] = {
    val teacherId = (bodyJson \ "user_id").as[String].toInt
    val email = (bodyJson \ "email").as[String]
    val name = (bodyJson \ "name").as[String]
    val eduInstId = (bodyJson \ "edu_inst_id").as[Int]

    val studentList = Await.result(tsMatchingService.getStudentsForSearch(teacherId, email, name, eduInstId), Duration.Inf)
    studentList.toList
  }

  //학생 추가 요청
  def addStudent(bodyJson: JsValue)= {
    val teacherId = (bodyJson \ "user_id").as[String].toInt
    val studentId  = (bodyJson \ "student_id").as[String].toInt
    val createdAt = new Timestamp(new java.util.Date().getTime)
    Await.result(tsMatchingService.insert(TSMatchingRow(teacherId = teacherId, studentId = studentId, isApproval = Option(false), createdAt = Option(createdAt))), Duration.Inf)
  }

  //추가된 학생 리스트 요청
  def getStudents(bodyJson: JsValue): Map[String, String] = {
    val teacherId = (bodyJson \ "user_id").as[String].toInt
    val students = Await.result(tsMatchingService.getStudentsForTeacher(teacherId), Duration.Inf)
    val examList = Await.result(examService.getAllActiveExamList(), Duration.Inf)
    var studentsInfo = new ListBuffer[StudentsForList]
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    val monday = new java.sql.Date(new java.util.Date(cal.getTimeInMillis()).getTime)
    //학생별 시험 점수 입력 및 목표달성 여부 데이터 추가
    students.foreach{ row =>
      val studentExam = Await.result(examService.getAllActiveExamAndHasScore(row.student_id), Duration.Inf)
      val studyGoal = Await.result(studyGoalService.findByStudentIdAndMonday(row.student_id, monday), Duration.Inf)
      var goalTime = 0l
      if(studyGoal.isDefined){
        goalTime = studyGoal.get.goalTime.get / 7
      }
      val goalAchieve = getGoalAchieve(row.student_id, goalTime, monday)
      studentsInfo += row.copy(student_exam = Option(studentExam.toList), goal_achieve = Option(goalAchieve))
    }

    implicit val formats = Serialization.formats(NoTypeHints)
    var result: Map[String, String] = new HashMap[String, String]
    result += ("result" -> "success")
    result += ("student_list" -> write(studentsInfo.toList))
    result += ("num_of_student" -> studentsInfo.toList.length.toString)
    result += ("exam_list" -> write(examList))
    result
  }

  //학생 리스트 요청 시 공부시간 달성 데이터 조회
  def getGoalAchieve(studentId: Int, goalTime: Long, monday: java.sql.Date): String ={
    var goalAchieveList = Array(0,0,0,0,0,0,0)
    val today = new java.sql.Date(new java.util.Date().getTime)
    val studyTime = Await.result(studyTimeService.getTotalStudyForStudent(studentId, monday, today), Duration.Inf)
    for(i <- 0 until studyTime.length){
      var temp = 2
      if(studyTime(i).get >= goalTime){
        temp = 1
      }
      goalAchieveList(i) = temp
    }
    goalAchieveList.mkString(",")
  }
  def findById(id: Int): Option[TeachersRow] = Await.result(teachersService.findById(id), Duration.Inf)

  //최종학력 수정
  def updateLastSchool(bodyJson: JsValue)= {
    val teacherId = (bodyJson \ "user_id").as[String].toInt
    val lastSchool = (bodyJson \ "last_school").as[String]
    val isGraduated = (bodyJson \ "is_graduated").as[Boolean]

    Await.result(teachersService.updateLastSchool(teacherId, lastSchool, isGraduated), Duration.Inf)
  }

  //소속 학교/학원 명 조회
  def getEduInst(id: Int): String = Await.result(userEduService.getTitleByUserId(id), Duration.Inf).get.get

  //소속 학교/학원 업데이트
  def updateEduInst(bodyJson: JsValue)= {
    val teacherId = (bodyJson \ "user_id").as[String].toInt
    val eduInstId = (bodyJson \ "edu_inst_id").as[Int]

    Await.result(userEduService.deleteByUserId(teacherId), Duration.Inf)
    Await.result(userEduService.insert(UserEduRow(eduInstId, teacherId, Option(true))), Duration.Inf)
  }

  //승인 여부 조회
  def isAuthenticated(id: Int): Boolean = {
    val teacher = findById(id).get
    if (teacher.isAuthenticated.get) true else false
  }
}
