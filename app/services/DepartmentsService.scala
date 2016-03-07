package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DepartmentsService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val departments = TableQuery[Tables.Departments]

  private val users = TableQuery[Tables.Users]

  def all(): Future[Seq[DepartmentsRow]] = db.run(departments.result)

  def insert(departmentsRow: DepartmentsRow): Future[Unit] =  db.run(departments += departmentsRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(departments.filter(_.id === id).delete)

//  def findByParentId(id: Int): Future[Seq[DepartmentsRow]] = db.run(departments.filter(row => (row.parentId === id && row.id =!= id)).result)

}