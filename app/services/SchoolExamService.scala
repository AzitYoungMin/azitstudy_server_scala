package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SchoolExamService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val schoolExam = TableQuery[Tables.SchoolExam]

  private val users = TableQuery[Tables.Users]

  def all(): Future[Seq[SchoolExamRow]] = db.run(schoolExam.result)

  def insert(schoolExamRow: SchoolExamRow): Future[Unit] =  db.run(schoolExam += schoolExamRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(schoolExam.filter(_.id === id).delete)

  def findActiveExam(): Future[Option[SchoolExamRow]] = db.run(schoolExam.filter(_.isActive === true).result.headOption)

}