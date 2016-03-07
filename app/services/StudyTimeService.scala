package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StudyTimeService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val studyTime = TableQuery[Tables.StudyTime]

  private val studyGoal = TableQuery[Tables.StudyGoal]

  def all(): Future[Seq[StudyTimeRow]] = db.run(studyTime.result)

//  def countAll(): Future[Int] = db.run(studyTime.filter())
//  def insert(studyTimeRow: StudyTimeRow): Future[Unit] =  db.run(studyTime += studyTimeRow).map(_ => ())

  def insert(studyTimeRow: StudyTimeRow): Future[Int] = {
    val action =(studyTime returning studyTime.map(_.id)) += studyTimeRow
    db.run(action).map(id => id)
  }
  def delete(id: Int): Future[Int] = db.run(studyTime.filter(_.id === id).delete)

  def update(studyTimeRow: StudyTimeRow) = db.run(studyTime.filter(_.id === studyTimeRow.id).update(studyTimeRow))

  def findById(id: Int): Future[Option[StudyTimeRow]] = db.run(studyTime.filter(_.id === id).result.headOption)

  def findByStudentIdAndCreatedAt(studentId: Int, createdAt: java.sql.Date): Future[Option[StudyTimeRow]] = db.run(studyTime.filter(row => (row.studentId === studentId && row.createdAt === createdAt)).result.headOption)

  def findByStudentIdAndStartAndEnd(studentId: Int, start: java.sql.Date, end: java.sql.Date): Future[Seq[StudyTimeRow]] = db.run(studyTime.filter(row => (row.studentId === studentId && row.createdAt >= start && row.createdAt <= end)).result)

  def findHighRankByDate(date: java.sql.Date, offset: Int): Future[Option[Long]] = db.run(studyTime.filter(row => (row.createdAt === date)).sortBy(_.totalStudy.desc).drop(0).take(offset).map(_.totalStudy).avg.result)

  def findHighRankStudentByDateQuery(date: java.sql.Date, monday: java.sql.Date, offset: Int) = for{
    studentId <- studyTime.filter(row => (row.createdAt === date)).sortBy(_.totalStudy.desc).drop(0).take(offset).map(_.studentId)
    goalTime <- studyGoal.filter(row =>(row.studentId === studentId && row.monday === monday))
  }yield (goalTime.goalTime)

  def findHighRankStudentByDate(date: java.sql.Date, monday: java.sql.Date, offset: Int): Future[Option[Long]] = db.run(findHighRankStudentByDateQuery(date, monday, offset).avg.result)

  def countByDate(date: java.sql.Date): Future[Int] = db.run(studyTime.filter(row => (row.createdAt === date)).length.result)

  def findHighRankByDateAll(date: java.sql.Date, offset: Int): Future[Seq[StudyTimeRow]] = db.run(studyTime.filter(row => (row.createdAt === date)).sortBy(_.totalStudy.desc).drop(0).take(offset).result)

  def getTotalStudyForStudent(studentId: Int, start: java.sql.Date, end: java.sql.Date): Future[Seq[Option[Long]]] = db.run(studyTime.filter(row => (row.studentId === studentId && row.createdAt >= start && row.createdAt <= end)).map(_.totalStudy).result)
}