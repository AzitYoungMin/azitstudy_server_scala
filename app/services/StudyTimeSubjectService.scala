package services

import controllers.api.helpers.StudyTimeSubjectSum
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StudyTimeSubjectService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val studyTimeSubject = TableQuery[Tables.StudyTimeSubject]

  private val studyGoal = TableQuery[Tables.StudyGoal]

  def all(): Future[Seq[StudyTimeSubjectRow]] = db.run(studyTimeSubject.result)

  //  def countAll(): Future[Int] = db.run(studyTime.filter())
  //  def insert(studyTimeRow: StudyTimeRow): Future[Unit] =  db.run(studyTime += studyTimeRow).map(_ => ())

  def insert(studyTimeSubjectRow: StudyTimeSubjectRow): Future[Int] = {
    val action = (studyTimeSubject returning studyTimeSubject.map(_.id)) += studyTimeSubjectRow
    db.run(action).map(id => id)
  }

  def delete(id: Int): Future[Int] = db.run(studyTimeSubject.filter(_.id === id).delete)

  def update(studyTimeSubjectRow: StudyTimeSubjectRow) = db.run(studyTimeSubject.filter(_.id === studyTimeSubjectRow.id).update(studyTimeSubjectRow))

  def findById(id: Int): Future[Option[StudyTimeSubjectRow]] = db.run(studyTimeSubject.filter(_.id === id).result.headOption)

  def findByStudentIdAndCreatedAtAndSubjectIdAndTypeId(studentId: Int, createdAt: java.sql.Date, subjectId: Int, typeId: Int): Future[Option[StudyTimeSubjectRow]] = db.run(studyTimeSubject.filter(row => (row.studentId === studentId && row.createdAt === createdAt && row.subjectId === subjectId && row.typeId === typeId)).result.headOption)

  def findByStudentIdAndMondayAndTypeId(studentId: Int, monday: java.sql.Date, typeId: Int): Future[Seq[StudyTimeSubjectRow]] = db.run(studyTimeSubject.filter(row => (row.studentId === studentId && row.createdAt === monday && row.typeId === typeId)).result)

  def countByDateAndTypeId(monday: java.sql.Date, typeId: Int): Future[Int] = db.run(studyTimeSubject.filter(row =>(row.createdAt === monday && row.typeId === typeId)).groupBy(_.studentId).map(_._1).length.result)

  def getMyRankQuery(studentId: Int, monday: java.sql.Date, typeId: Int, myScore: Long) = for{
    sum <- studyTimeSubject.filter(row =>(row.createdAt === monday && row.typeId === typeId)).groupBy(_.studentId).map { case (typeId, c) =>
      typeId -> c.map(_.totalStudy).sum
    }
    if sum._2 > myScore
  } yield sum

  def getMyRank(studentId: Int, monday: java.sql.Date, typeId: Int, myScore: Long): Future[Int] = db.run(getMyRankQuery(studentId, monday, typeId, myScore).length.result)





//  def findSubjectIdByStudentIdAndStartAndEndAndTypeId(studentId: Int, start: java.sql.Date, end: java.sql.Date, typeId: Int): Future[Seq[Int]] = db.run(studyTimeSubject.filter(row => (row.studentId === studentId && row.createdAt >= start && row.createdAt <= end && row.typeId === typeId)).groupBy(_.subjectId).map(_._1).result)

//  def findByStudentIdAndStartAndEnd(studentId: Int, start: java.sql.Date, end: java.sql.Date, typeId: Int): Future[Seq[StudyTimeSubjectSum]] = db.run(studyTimeSubject.filter(row => (row.studentId === studentId && row.createdAt >= start && row.createdAt <= end && row.typeId === typeId)).groupBy(_.subjectId).map {
//    case ((subjectId), things) => {
//      (subjectId.get, things.map(_.contentsBasic).sum.get, things.map(_.contentsSolution).sum.get, things.map(_.contentsEbs).sum.get, things.map(_.contentsReal).sum.get, things.map(_.typeIndependently).sum.get, things.map(_.typeLecture).sum.get, things.map(_.typeSchool).sum.get, things.map(_.typePrivateEdu).sum.get, things.map(_.typePrivateTeacher).sum.get, things.map(_.typeCheck).sum.get, things.map(_.typeExam).sum.get, things.map(_.title))
//    }
//  }.result)

  //  map(rows => rows.map(row =>
  //    StudyTimeSubjectSum(subjectId = row., title = row., contentsBasic: Long, contentsSolution: Long, contentsEbs: Long, contentsReal: Long, typeIndependently: Long, typeLecture: Long, typeSchool: Long, typePrivateEdu: Long, typePrivateTeacher: Long, typeCheck: Long, typeExam: Long)
  //  ))

  //  def findHighRankByDate(date: java.sql.Date, offset: Int): Future[Option[Long]] = db.run(studyTimesubject.filter(row => (row.createdAt === date)).sortBy(_.totalStudy.desc).drop(0).take(offset).map(_.totalStudy).avg.result)
  //
  //  def findHighRankStudentByDateQuery(date: java.sql.Date, monday: java.sql.Date, offset: Int) = for{
  //    studentId <- studyTimesubject.filter(row => (row.createdAt === date)).sortBy(_.totalStudy.desc).drop(0).take(offset).map(_.studentId)
  //    goalTime <- studyGoal.filter(row =>(row.studentId === studentId && row.monday === monday))
  //  }yield (goalTime.goalTime)
  //
  //  def findHighRankStudentByDate(date: java.sql.Date, monday: java.sql.Date, offset: Int): Future[Option[Long]] = db.run(findHighRankStudentByDateQuery(date, monday, offset).avg.result)
  //
  //  def countByDate(date: java.sql.Date): Future[Int] = db.run(studyTime.filter(row => (row.createdAt === date)).length.result)
  //
  //  def findHighRankByDateAll(date: java.sql.Date, offset: Int): Future[Seq[StudyTimeRow]] = db.run(studyTime.filter(row => (row.createdAt === date)).sortBy(_.totalStudy.desc).drop(0).take(offset).result)
}