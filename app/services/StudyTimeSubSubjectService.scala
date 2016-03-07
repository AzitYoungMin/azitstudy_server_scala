package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StudyTimeSubSubjectService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val studyTimeSubSubject = TableQuery[Tables.StudyTimeSubSubject]

  private val subjects = TableQuery[Tables.Subjects]

  def all(): Future[Seq[StudyTimeSubSubjectRow]] = db.run(studyTimeSubSubject.result)

//  def countAll(): Future[Int] = db.run(studyTime.filter())
//  def insert(studyTimeRow: StudyTimeRow): Future[Unit] =  db.run(studyTime += studyTimeRow).map(_ => ())

  def insert(studyTimeSubSubjectRow: StudyTimeSubSubjectRow): Future[Int] = {
    val action =(studyTimeSubSubject returning studyTimeSubSubject.map(_.id)) += studyTimeSubSubjectRow
    db.run(action).map(id => id)
  }
  def delete(id: Int): Future[Int] = db.run(studyTimeSubSubject.filter(_.id === id).delete)

  def update(studyTimeSubSubjectRow: StudyTimeSubSubjectRow) = db.run(studyTimeSubSubject.filter(_.id === studyTimeSubSubjectRow.id).update(studyTimeSubSubjectRow))

  def findById(id: Int): Future[Option[StudyTimeSubSubjectRow]] = db.run(studyTimeSubSubject.filter(_.id === id).result.headOption)

  def findBySTSIdAndSubjectId(STSId: Int, subjectId: Int): Future[Option[StudyTimeSubSubjectRow]] = db.run(studyTimeSubSubject.filter(row => (row.studyTimeSubjectId === STSId && row.subjectId === subjectId)).result.headOption)

  def findByStudyTimeSubjectId(studyTimeSubjectId: Int): Future[Seq[StudyTimeSubSubjectRow]] = db.run(studyTimeSubSubject.filter(_.studyTimeSubjectId === studyTimeSubjectId).result)

//  def findByStudentIdAndCreatedAt(studentId: Int, createdAt: java.sql.Date): Future[Option[StudyTimeRow]] = db.run(studyTime.filter(row => (row.studentId === studentId && row.createdAt === createdAt)).result.headOption)
//
//  def findByStudentIdAndStartAndEnd(studentId: Int, start: java.sql.Date, end: java.sql.Date): Future[Seq[StudyTimeRow]] = db.run(studyTime.filter(row => (row.studentId === studentId && row.createdAt >= start && row.createdAt <= end)).result)
//
//  def findHighRankByDate(date: java.sql.Date, offset: Int): Future[Option[Long]] = db.run(studyTime.filter(row => (row.createdAt === date)).sortBy(_.totalStudy.desc).drop(0).take(offset).map(_.totalStudy).avg.result)
//
//  def findHighRankStudentByDateQuery(date: java.sql.Date, monday: java.sql.Date, offset: Int) = for{
//    studentId <- studyTime.filter(row => (row.createdAt === date)).sortBy(_.totalStudy.desc).drop(0).take(offset).map(_.studentId)
//    goalTime <- studyGoal.filter(row =>(row.studentId === studentId && row.monday === monday))
//  }yield (goalTime.goalTime)
//
//  def findHighRankStudentByDate(date: java.sql.Date, monday: java.sql.Date, offset: Int): Future[Option[Long]] = db.run(findHighRankStudentByDateQuery(date, monday, offset).avg.result)
//
//  def countByDate(date: java.sql.Date): Future[Int] = db.run(studyTime.filter(row => (row.createdAt === date)).length.result)
//
//  def findHighRankByDateAll(date: java.sql.Date, offset: Int): Future[Seq[StudyTimeRow]] = db.run(studyTime.filter(row => (row.createdAt === date)).sortBy(_.totalStudy.desc).drop(0).take(offset).result)
}