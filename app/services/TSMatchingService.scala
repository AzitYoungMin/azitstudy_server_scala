package services

import caseClasses.UnreceivedTSMatching
import controllers.api.StaticValues
import controllers.api.helpers.{StudentsForList, StudentsForSearch}
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.collection.heterogeneous.Zero.+
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class TSMatchingService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val tsMatching = TableQuery[Tables.TSMatching]

  private val users = TableQuery[Tables.Users]

  private val students = TableQuery[Tables.Students]

  private val eduInst = TableQuery[Tables.EducationalInst]

  private val userEdu = TableQuery[Tables.UserEdu]

  def all(): Future[Seq[TSMatchingRow]] = db.run(tsMatching.result)

  def insert(tsMatchingRow: TSMatchingRow): Future[Unit] =  db.run(tsMatching += tsMatchingRow).map(_ => ())

  def delete(teacherId: Int, studentId: Int): Future[Int] = db.run(tsMatching.filter(row => (row.teacherId === teacherId && row.studentId === studentId)).delete)

  def findByTeacherId(id: Int): Future[Seq[TSMatchingRow]] = db.run(tsMatching.filter(_.teacherId === id).result)

  def getStudentsForSearchQuery(teacherId: Int, email: String, name: String, eduInstId: Int) = for {
    (u, ue) <- {var userQuery = users.filter(_.typeId === StaticValues.USER_TYPE_STUDENT)
      if(!name.equals("")) userQuery = userQuery.filter(_.name like name+"%")
      if(!email.equals("")) userQuery = userQuery.filter(_.email like email+"%")
      userQuery
    } join { var userEduQuery = userEdu.filter(_.isDefault === true)
      if(eduInstId != 0 ) userEduQuery = userEduQuery.filter(_.eduInstId === eduInstId)
      userEduQuery
    } on(_.id === _.userId)
    s <- students.filter(_.id === u.id)
    e <- eduInst.filter(_.id === ue.eduInstId)
    if !tsMatching.filter(row => (row.teacherId === teacherId && row.studentId === u.id)).exists
  } yield (u.id, u.name, u.email, e.name, s.year)

  def getStudentsForSearch(teacherId: Int, email: String, name: String, eduInstId: Int): Future[Seq[StudentsForSearch]] = db.run(getStudentsForSearchQuery(teacherId, email, name, eduInstId).result).map(rows => rows.map{row =>
      var school = row._4.get + " "
      val year = row._5.get
      if(year.equals(0)){
        school += "N수생"
      }else{
        school +=  row._5.get + "학년"
      }
      StudentsForSearch(row._1, row._2.get, school, row._3.get)
  })

  def getStudentsForTeacherQuery(teacherId: Int) = for{
    u <- users.filter(_.typeId === StaticValues.USER_TYPE_STUDENT)
    if tsMatching.filter(row => (row.teacherId === teacherId && row.studentId === u.id && row.isApproval === true)).exists
  } yield (u.id, u.name, u.profileImage)

  def examService = new ExamService

  def getStudentsForTeacher(teacherId: Int): Future[Seq[StudentsForList]] = db.run(getStudentsForTeacherQuery(teacherId).result).map(rows => rows.map { row =>
    StudentsForList(row._1, row._2.getOrElse(""), row._3.getOrElse(""))
  })

  def getUnreceivedMatchingQuery(teacherName: String, teacherEmail: String, studentName: String, studentEmail: String) = for{
    m <- tsMatching.sortBy(_.createdAt.desc)
    t <- {
      var teacher = users.filter(_.id === m.teacherId)
      if(!teacherName.equals("")) teacher = teacher.filter(_.name like teacherName+"%")
      if(!teacherEmail.equals("")) teacher = teacher.filter(_.email like teacherEmail+"%")
      teacher
    }
    ue1 <- userEdu.filter(row => (row.userId === t.id && row.isDefault === true))
    e1 <- eduInst.filter(_.id === ue1.eduInstId)
    s <- {
      var student = users.filter(_.id === m.studentId)
      if(!studentName.equals("")) student = student.filter(_.name like studentName+"%")
      if(!studentEmail.equals("")) student = student.filter(_.email like studentEmail+"%")
      student
    }
    ue2 <- userEdu.filter(row => (row.userId === s.id && row.isDefault === true))
    e2 <- eduInst.filter(_.id === ue2.eduInstId)
  } yield (t.id, t.name, t.email, e1.name, s.id, s.name, s.email, e2.name, m.createdAt, m.isAuthenticated)

  def getUnreceivedMatching(teacherName: String, teacherEmail: String, studentName: String, studentEmail: String, startIndex: Int, pageSize: Int): Future[Seq[UnreceivedTSMatching]] = db.run(getUnreceivedMatchingQuery(teacherName, teacherEmail, studentName, studentEmail).drop(startIndex).take(pageSize).result).map(rows => rows.map { row =>
    UnreceivedTSMatching(row._1, row._2.getOrElse(""), row._3.getOrElse(""), row._4.getOrElse(""), row._5, row._6.getOrElse(""), row._7.getOrElse(""), row._8.getOrElse(""), row._9.get, row._10.get)
  })

  def countUnreceivedMatching(teacherName: String, teacherEmail: String, studentName: String, studentEmail: String): Future[Int] = db.run(getUnreceivedMatchingQuery(teacherName, teacherEmail, studentName, studentEmail).length.result)

//  def updateApprovalQuery(teacherId: Int, studentId: Int) = for {m <- tsMatching if(m.teacherId === teacherId && m.studentId === studentId)} yield m.isApproval
//
//  def updateApproval(teacherId: Int, studentId: Int) = db.run(updateApprovalQuery(teacherId, studentId).update(Option(true)))

  def updateApprovalQuery(teacherId: Int, studentId: Int) = for {m <- tsMatching if(m.teacherId === teacherId && m.studentId === studentId)} yield (m.isApproval, m.isAuthenticated)

  def updateApproval(teacherId: Int, studentId: Int, isApproval: Boolean, isAuthenticated: Int) = db.run(updateApprovalQuery(teacherId, studentId).update(Option(isApproval), Option(isAuthenticated)))


}