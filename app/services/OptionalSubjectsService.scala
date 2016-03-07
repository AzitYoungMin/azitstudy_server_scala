package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OptionalSubjectsService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val optionalSubjects = TableQuery[Tables.OptionalSubjects]

  def all(): Future[Seq[OptionalSubjectsRow]] = db.run(optionalSubjects.result)

  def insert(optionalSubjectsRow: OptionalSubjectsRow): Future[Unit] =  db.run(optionalSubjects += optionalSubjectsRow).map(_ => ())

  def delete(studentId: Int, subjectId: Int): Future[Int] = db.run(optionalSubjects.filter(row => (row.studentId === studentId && row.subjectId === subjectId)).delete)

  def getSubjectIdByStudentId(studentId: Int): Future[Seq[Int]] = db.run(optionalSubjects.filter(_.studentId === studentId).map(_.subjectId).result)
}