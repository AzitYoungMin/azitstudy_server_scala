package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StudyRecordsService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val studyRecords = TableQuery[Tables.StudyRecords]

  def all(): Future[Seq[StudyRecordsRow]] = db.run(studyRecords.result)

  def insert(studyRecordsRow: StudyRecordsRow): Future[Unit] =  db.run(studyRecords += studyRecordsRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(studyRecords.filter(_.id === id).delete)

  def update(studyRecordsRow: StudyRecordsRow) = db.run(studyRecords.filter(_.id === studyRecordsRow.id).update(studyRecordsRow))

  def findById(id: Int): Future[Option[StudyRecordsRow]] = db.run(studyRecords.filter(_.id === id).result.headOption)

  def findByActivityIdAndDate(id: Int, startTime: java.sql.Timestamp, endTime: java.sql.Timestamp): Future[Option[StudyRecordsRow]] = db.run(studyRecords.filter(row => (row.studyId === id && (row.createdAt >= startTime && row.createdAt <= endTime))).sortBy(_.createdAt.desc).result.headOption)

}