package services

import java.sql.Timestamp

import caseClasses.{OtherSubjects, KME, StudentsForGrade, StudentForPushList}
import controllers.api.StaticValues
import controllers.api.helpers.StudentsForPush
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StudentsService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val students = TableQuery[Tables.Students]

  private val eduInst = TableQuery[Tables.EducationalInst]

  private val userEdu = TableQuery[Tables.UserEdu]

  private val users = TableQuery[Tables.Users]

  private val studyTime = TableQuery[Tables.StudyTime]

  private val studyGoal = TableQuery[Tables.StudyGoal]

  private val examRecords = TableQuery[Tables.ExamRecords]

  private val exams = TableQuery[Tables.Exam]

  private val subjects = TableQuery[Tables.Subjects]

  def all(): Future[Seq[StudentsRow]] = db.run(students.result)

  def insert(student: StudentsRow): Future[Unit] =  db.run(students += student).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(students.filter(_.id === id).delete)

  def update(student: StudentsRow) = db.run(students.filter(_.id === student.id).update(student))

  def findById(id: Int): Future[Option[StudentsRow]] = db.run(students.filter(_.id === id).result.headOption)

  def findByNickname(nickname: String): Future[Option[StudentsRow]] = db.run(students.filter(_.nickname === nickname).result.headOption)

  def updateTargetUniversityQuery(id: Int) = for {s <- students if s.id === id} yield s.targetUniversity

  def updateTargetUniversity(id: Int, university: String) = db.run(updateTargetUniversityQuery(id).update(Option(university)))

  def getMathTypeByStudentId(id: Int): Future[Option[Int]] = db.run(students.filter(_.id === id).map(_.mathType).result.head)

  def getYearByStudentId(id: Int): Future[Option[Int]] = db.run(students.filter(_.id === id).map(_.year).result.head)

  def updateTotalGradeQuery(id: Int) = for {s <- students if s.id === id} yield s.totalgrade

  def updateTotalGrade(id: Int, totalGrade: Int) = db.run(updateTotalGradeQuery(id).update(Option(totalGrade)))

  def getStudentIdsForAutoPush(): Future[Seq[Int]] = db.run(users.filter(row => (row.typeId === StaticValues.USER_TYPE_STUDENT && !row.token.isEmpty && row.push === true && row.withdrawalApproval === false)).map(_.id).result)

  def getStudentsForPushQuery(totalGrade: List[Int], year: Int, school: String) = for {
    (s, ue) <- {var studentQuery = students.sortBy(_.id.desc)
      if(!totalGrade.isEmpty) studentQuery = studentQuery.filter(_.totalgrade inSet totalGrade)
      if(year != 0) studentQuery = studentQuery.filter(_.year === year)
      studentQuery
    } join userEdu.filter(_.isDefault === true) on(_.id === _.userId)
    u <- users.filter(row => (row.id === s.id && !row.token.isEmpty && row.push === true && row.withdrawalApproval === false))
    e <- {var eduInstQuery = eduInst.filter(_.id === ue.eduInstId)
      if(school != "") eduInstQuery = eduInstQuery.filter(_.name like school+"%")
      eduInstQuery
    }
  } yield (u.id, u.name, e.name, s.year, s.totalgrade, u.createdAt)

  def getStudentsForPush(totalGrade: List[Int], year: Int, school: String, startIndex: Int, pageSize: Int): Future[Seq[StudentForPushList]] = db.run(getStudentsForPushQuery(totalGrade, year, school).result).map(rows => rows.map{row =>
    val year = row._4.get
    var yearString = ""
    if(year.equals(4)){
      yearString = "N수생"
    }else{
      yearString = "고" + row._4.get
    }
    StudentForPushList(id = row._1, name = row._2.get, school = row._3.get, year = yearString, totalGrade = row._5.get, createdAt = row._6.get)
  })

  def countStudentsForPush(totalGrade: List[Int], year: Int, school: String): Future[Int] = db.run(getStudentsForPushQuery(totalGrade, year, school).length.result)

  def getStudentsForPushByDateQuery(totalGrade: List[Int], year: Int, school: String, start: Timestamp, end: Timestamp) = for {
    (s, ue) <- {var studentQuery = students.sortBy(_.id.desc)
      if(!totalGrade.isEmpty) studentQuery = studentQuery.filter(_.totalgrade inSet totalGrade)
      if(year != 0) studentQuery = studentQuery.filter(_.year === year)
      studentQuery
    } join userEdu.filter(_.isDefault === true) on(_.id === _.userId)
    u <- users.filter(row => (row.id === s.id && !row.token.isEmpty && row.push === true && (row.createdAt >= start && row.createdAt <= end) && row.withdrawalApproval === false))
    e <- {var eduInstQuery = eduInst.filter(_.id === ue.eduInstId)
      if(school != "") eduInstQuery = eduInstQuery.filter(_.name like school+"%")
      eduInstQuery
    }
  } yield (u.id, u.name, e.name, s.year, s.totalgrade, u.createdAt)

  def getStudentsForPushByDate(totalGrade: List[Int], year: Int, school: String, start: Timestamp, end: Timestamp, startIndex: Int, pageSize: Int): Future[Seq[StudentForPushList]] = db.run(getStudentsForPushByDateQuery(totalGrade, year, school, start, end).result).map(rows => rows.map{row =>
    val year = row._4.get
    var yearString = ""
    if(year.equals(4)){
      yearString = "N수생"
    }else{
      yearString = "고" + row._4.get
    }
    StudentForPushList(id = row._1, name = row._2.get, school = row._3.get, year = yearString, totalGrade = row._5.get, createdAt = row._6.get)
  })

  def countStudentsForPushByDate(totalGrade: List[Int], year: Int, school: String, start: Timestamp, end: Timestamp): Future[Int] = db.run(getStudentsForPushByDateQuery(totalGrade, year, school, start, end).length.result)

//  def getStudentIdsForLessPushQuery(today: java.sql.Date, monday: java.sql.Date) = for{
//    user <- users.filter(row => (row.isWithdrawal === false && row.typeId === StaticValues.USER_TYPE_STUDENT && !row.token.isEmpty))
//    goal <- studyGoal.filter(row => (row.alarm === true && row.monday === monday))
////    time <- studyTime.filter(row => (row.createdAt === today && row.studentId === user.id && row.totalStudy < (goal.goalTime.get/7.toLong)))
//    time <- studyTime.filter(row => (row.createdAt === today && row.studentId === user.id))
//    if((goal.goalTime / 7.toLong) > time.totalStudy)
//  } yield user.id
//
//  def getStudentIdsForLessPush(today: java.sql.Date, monday: java.sql.Date): Future[Seq[Int]] = db.run(getStudentIdsForLessPushQuery(today, monday).result)
//
//  def getStudentIdsForMorePushQuery(today: java.sql.Date, monday: java.sql.Date) = for{
//    user <- users.filter(row => (row.isWithdrawal === false && row.typeId === StaticValues.USER_TYPE_STUDENT && !row.token.isEmpty))
//    goal <- studyGoal.filter(row => (row.alarm === true && row.monday === monday))
////    time <- studyTime.filter(row => (row.createdAt === today && row.studentId === user.id && row.totalStudy < (goal.goalTime.get/7.toLong)))
//    time <- studyTime.filter(row => (row.createdAt === today && row.studentId === user.id))
//    if((goal.goalTime / 7.toLong) < time.totalStudy)
//  } yield user.id
//
//  def getStudentIdsForMorePush(today: java.sql.Date, monday: java.sql.Date): Future[Seq[Int]] = db.run(getStudentIdsForMorePushQuery(today, monday).result)

  def getStudentIdsForPushQuery(today: java.sql.Date, monday: java.sql.Date) = for{
    user <- users.filter(row => (row.isWithdrawal === false && row.typeId === StaticValues.USER_TYPE_STUDENT && !row.token.isEmpty))
    goal <- studyGoal.filter(row => (row.studentId === user.id && row.alarm === true && row.monday === monday))
    time <- studyTime.filter(row => (row.studentId === user.id && row.createdAt === today))
  } yield (user.id, goal.goalTime, time.totalStudy)

  def getStudentIdsForPush(today: java.sql.Date, monday: java.sql.Date): Future[Seq[StudentsForPush]] = db.run(getStudentIdsForPushQuery(today, monday).result).map(rows => rows.map{row =>
    StudentsForPush(id = row._1, goalTime = row._2, totalStudy = row._3)
  })

//  def getStudentForGradeQuery(examId: Int) = for {
//    (user, korean) <- users.filter(row => (row.isWithdrawal === false && row.typeId === StaticValues.USER_TYPE_STUDENT)).sortBy(_.id.desc) joinLeft examRecords.filter(row => (row.examId === examId && row.recordTypeId === StaticValues.EXAM_RECORD_TYPE_SCORE && row.subjectId === StaticValues.SUBJECT_ID_KOREAN)) on (_.id === _.studentId)
//    //    korean <- examRecords.filter(row => (row.examId === examId && row.studentId === user.id && row.recordTypeId === StaticValues.EXAM_RECORD_TYPE_SCORE && row.subjectId === StaticValues.SUBJECT_ID_KOREAN)).map(_.score)
//    //    math <- examRecords.filter(row => (row.examId === examId && row.studentId === user.id && row.recordTypeId === StaticValues.EXAM_RECORD_TYPE_SCORE && row.subjectId === StaticValues.SUBJECT_ID_MATH_A)).map(_.score)
//    //    english <- examRecords.filter(row => (row.examId === examId && row.studentId === user.id && row.recordTypeId === StaticValues.EXAM_RECORD_TYPE_SCORE && row.subjectId === StaticValues.SUBJECT_ID_ENGLISH)).map(_.score)
//    exam <- exams.filter(_.id === examId)
//  //  } yield (user.name, exam, korean, math, english)
//  }yield (user.name, korean.map(_.score), exam)
//
//  def getStudentForGrade(examId: Int, startIndex: Int, pageSize: Int): Future[Seq[StudentsForGrade]] = db.run(getStudentForGradeQuery(examId).drop(startIndex).take(pageSize).result).map{rows => rows.flatMap{ row =>
//    val exam = row._3
//    val examTitle = exam.year.get + "년 " + exam.month.get + "월 " + exam.title.get
//    var hasScore = "미입력"
//    //    if(row._3.isDefined && row._4.isDefined && row._5.isDefined) hasScore = "입력"
//    StudentsForGrade(name = row._1.get, exam = examTitle, korean = row._3.get.score)}, hasScore = hasScore)
//  }}

  def getStudentForGradeQuery(examId: Int, email: String, name: String) = for {
    user <- {
      var userQuery = users.filter(row => (row.isWithdrawal === false && row.typeId === StaticValues.USER_TYPE_STUDENT))
      if(!email.equals("")) userQuery = userQuery.filter(_.email like email+"%")
      if(!name.equals("")) userQuery = userQuery.filter(_.name like name+"%")
      userQuery.sortBy(_.id.desc)
    }
    exam <- exams.filter(_.id === examId)
  } yield (user, exam)

  def getStudentForGrade(examId: Int, email: String, name: String, startIndex: Int, pageSize: Int): Future[Seq[StudentsForGrade]] = db.run(getStudentForGradeQuery(examId, email, name).drop(startIndex).take(pageSize).result).map(rows => rows.map { row =>
    val exam = row._2
    val examTitle = exam.year.get + "년 " + exam.month.get + "월 " + exam.title.get
    StudentsForGrade(id = row._1.id, name = row._1.name.get, exam = examTitle)
  })

  def countStudentForGrade(examId: Int, email: String, name: String): Future[Int] = db.run(getStudentForGradeQuery(examId, email, name).length.result)

  def getStudentRecordQuery(studentId: Int, examId: Int) = for {
    korean <- examRecords.filter(row => (row.examId === examId && row.studentId === studentId && row.recordTypeId === StaticValues.EXAM_RECORD_TYPE_SCORE && row.subjectId === StaticValues.SUBJECT_ID_KOREAN)).map(_.score)
    math <- examRecords.filter(row => (row.examId === examId && row.studentId === studentId && row.recordTypeId === StaticValues.EXAM_RECORD_TYPE_SCORE && row.subjectId === StaticValues.SUBJECT_ID_MATH_A)).map(_.score)
    english <- examRecords.filter(row => (row.examId === examId && row.studentId === studentId && row.recordTypeId === StaticValues.EXAM_RECORD_TYPE_SCORE && row.subjectId === StaticValues.SUBJECT_ID_ENGLISH)).map(_.score)
  } yield (korean, math, english)

  def getStudentRecord(studentId: Int, examId: Int): Future[Option[(Option[Int], Option[Int], Option[Int])]] = db.run(getStudentRecordQuery(studentId, examId).result.headOption)

  def getDepartmentByStudentId(id: Int): Future[Option[Int]] = db.run(students.filter(_.id === id).map(_.department).result.head)

  def getExamRecordsForStudentQuery(studentId: Int, examId: Int, subjectIds: List[Int])= for{
    record <- examRecords.filter(row => (row.examId === examId && row.studentId === studentId && row.recordTypeId === StaticValues.EXAM_RECORD_TYPE_SCORE)).filter(_.subjectId inSet subjectIds)
    subject <- subjects.filter(_.id === record.subjectId)
  }yield (record.score, subject.title)

  def getExamRecordsForStudent(studentId: Int, examId: Int, subjectIds: List[Int]): Future[Seq[OtherSubjects]] = db.run(getExamRecordsForStudentQuery(studentId, examId, subjectIds).result).map(rows => rows.map{row =>
    OtherSubjects(title = row._2.get, score = row._1.get)
  })

  def getForeignByStudentId(id: Int): Future[Option[Int]] = db.run(students.filter(_.id === id).map(_.foreignLanguage).result.head)

  def getExamRecordForStudentQuery(studentId: Int, examId: Int, subjectId: Int)= for{
    record <- examRecords.filter(row => (row.examId === examId && row.studentId === studentId && row.recordTypeId === StaticValues.EXAM_RECORD_TYPE_SCORE && row.subjectId === subjectId))
    subject <- subjects.filter(_.id === record.subjectId)
  }yield (record.score, subject.title)

  def getExamRecordForStudent(studentId: Int, examId: Int, subjectId: Int): Future[Option[(Option[Int], Option[String])]] = db.run(getExamRecordForStudentQuery(studentId, examId, subjectId).result.headOption)
}