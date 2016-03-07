package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StudyGoalService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val studyGoal = TableQuery[Tables.StudyGoal]

  def all(): Future[Seq[StudyGoalRow]] = db.run(studyGoal.result)

  def insert(studyGoalRow: StudyGoalRow): Future[Unit] =  db.run(studyGoal += studyGoalRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(studyGoal.filter(_.id === id).delete)

  def update(studyGoalRow: StudyGoalRow) = db.run(studyGoal.filter(_.id === studyGoalRow.id).update(studyGoalRow))

  def findById(id: Int): Future[Option[StudyGoalRow]] = db.run(studyGoal.filter(_.id === id).result.headOption)

  def findByStudentIdAndMonday(studentId: Int, monday: java.sql.Date): Future[Option[StudyGoalRow]] = db.run(studyGoal.filter(row => (row.studentId === studentId && row.monday === monday)).result.headOption)

}