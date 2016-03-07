package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TeachersService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val teachers = TableQuery[Tables.Teachers]

  def all(): Future[Seq[TeachersRow]] = db.run(teachers.result)

  def insert(teacher: TeachersRow): Future[Unit] = db.run(teachers += teacher).map { _ => () }

  def delete(id: Int): Future[Int] = db.run(teachers.filter(_.id === id).delete)

  def findById(id: Int): Future[Option[TeachersRow]] = db.run(teachers.filter(_.id === id).result.headOption)

  def updateLastSchoolQuery(teacherId: Int) = for {t <- teachers if(t.id === teacherId)} yield (t.lastSchool, t.isGraduated)

  def updateLastSchool(teacherId: Int, lastSchool: String, isGraduated: Boolean) = db.run(updateLastSchoolQuery(teacherId).update(Option(lastSchool), Option(isGraduated)))

  def updateApprovalQuery(id: Int) = for {m <- teachers if m.id === id} yield m.isAuthenticated

  def updateApproval(id: Int) = db.run(updateApprovalQuery(id).update(Option(true)))

}