package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import controllers.api.helpers.ExamCriteria

class ExamCriteriaService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val examCriteria = TableQuery[Tables.ExamCriteria]

  private val subjects = TableQuery[Tables.Subjects]

  def all(): Future[Seq[ExamCriteriaRow]] = db.run(examCriteria.result)

  def insert(examCriteriaRow: ExamCriteriaRow): Future[Unit] =  db.run(examCriteria += examCriteriaRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(examCriteria.filter(_.id === id).delete)

  def update(examCriteriaRow: ExamCriteriaRow) = db.run(examCriteria.filter(_.id === examCriteriaRow.id).update(examCriteriaRow))

  def findById(id: Int): Future[Option[ExamCriteriaRow]] = db.run(examCriteria.filter(_.id === id).result.headOption)

//  def findExamListForStudentQuery() = for{
//    mentors <- mentors.filter(_.isAuthenticated === false)
//    users <- users.filter(_.id === mentors.id)
//    userEdu <- userEdu.filter(row => (row.userId === users.id && row.isDefault === true))
//    educationalInst <- educationalInst.filter(_.id === userEdu.eduInstId)
//  } yield (mentors, users, educationalInst)
//
//  def findExamListForStudent(): Future[Seq[MentorsForList]] = db.run(findForApprovalQuery().result).map(rows => rows.map (row =>
//    MentorsForList(id = row._1.id, name = row._2.name, nickname = row._1.nickname, email = row._2.email, phone = row._2.phone, university = row._3.name, year = row._1.year, gender = row._2.gender, isAuthenticated = row._1.isAuthenticated)
//  ))
  def findByExamAndSubjectAndTypeQuery(examId: Int, subjectId: List[Int], recordType: Int) = for {
    examCriteria <- examCriteria.filter(row => (row.examId === examId && row.examRecordTypeId === recordType))
    if examCriteria.subjectId inSet subjectId
    subject <- subjects.filter(_.id === examCriteria.subjectId)
  } yield (examCriteria.id, subject)

  def findByExamAndSubjectAndType(examId: Int, subjectId: List[Int], recordType: Int): Future[Seq[ExamCriteria]] =
    db.run(findByExamAndSubjectAndTypeQuery(examId, subjectId, recordType).result).map(rows => rows.map ( row =>
      ExamCriteria(id = row._1, title = row._2.examTitle.get)
    ))

  def findBySubjectAndType(subjectId: Int, examType: Int): Future[Seq[Int]] = db.run(examCriteria.filter(row => (row.subjectId === subjectId && row.examRecordTypeId === examType)).map(_.id).result)

  def findOneByExamAndSubjectAndType(examId: Int, subjectId: Int, examType: Int): Future[Option[ExamCriteriaRow]] = db.run(examCriteria.filter(row => (row.examId === examId && row.subjectId === subjectId && row.examRecordTypeId === examType)).result.headOption)

}