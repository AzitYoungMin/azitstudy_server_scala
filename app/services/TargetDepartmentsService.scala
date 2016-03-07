package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TargetDepartmentsService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val targetDepartments = TableQuery[Tables.TargetDepartments]

  private val users = TableQuery[Tables.Users]

  def all(): Future[Seq[TargetDepartmentsRow]] = db.run(targetDepartments.result)

  def insert(departmentId: Int, studentId: Int): Future[Unit] =  db.run(targetDepartments += TargetDepartmentsRow(departmentId, studentId)).map(_ => ())

  def delete(departmentId: Int, studentId: Int): Future[Int] = db.run(targetDepartments.filter(row => (row.studentId === studentId && row.departmentId === departmentId)).delete)

  def findByStudentId(id: Int): Future[Seq[TargetDepartmentsRow]] = db.run(targetDepartments.filter(_.studentId === id).result)

}