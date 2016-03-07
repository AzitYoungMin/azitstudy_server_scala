package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SchoolExamRecordService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val schoolExamRecords = TableQuery[Tables.SchoolExamRecord]

  private val users = TableQuery[Tables.Users]

  def all(): Future[Seq[SchoolExamRecordRow]] = db.run(schoolExamRecords.result)

  def insert(schoolExamRecordsRow: SchoolExamRecordRow): Future[Unit] =  db.run(schoolExamRecords += schoolExamRecordsRow).map(_ => ())

  def update(schoolExamRecordsRow: SchoolExamRecordRow) = db.run(schoolExamRecords.filter(_.id === schoolExamRecordsRow.id).update(schoolExamRecordsRow))

  def delete(id: Int): Future[Int] = db.run(schoolExamRecords.filter(_.id === id).delete)

  def findByStudentId(id: Int): Future[Seq[SchoolExamRecordRow]] = db.run(schoolExamRecords.filter(_.studentId === id).result)

  def findByStudentIdAndExamId(studentId: Int, examId: Int): Future[Option[SchoolExamRecordRow]] = db.run(schoolExamRecords.filter(row =>(row.studentId === studentId && row.schoolExamId === examId)).result.headOption)
}