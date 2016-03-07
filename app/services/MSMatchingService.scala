package services

import controllers.api.StaticValues
import controllers.api.helpers.StudentsForList
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MSMatchingService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val msMatching = TableQuery[Tables.MSMatching]

  private val users = TableQuery[Tables.Users]

  def all(): Future[Seq[MSMatchingRow]] = db.run(msMatching.result)

  def insert(msMatchingRow: MSMatchingRow): Future[Unit] =  db.run(msMatching += msMatchingRow).map(_ => ())

  def delete(mentorId: Int, studentId: Int): Future[Int] = db.run(msMatching.filter(row => (row.mentorId === mentorId && row.studentId === studentId)).delete)

  def findByTeacherId(id: Int): Future[Seq[MSMatchingRow]] = db.run(msMatching.filter(_.mentorId === id).result)

  def getStudentsForMentorQuery(mentorId: Int) = for{
    u <- users.filter(_.typeId === StaticValues.USER_TYPE_STUDENT)
    if msMatching.filter(row => (row.mentorId === mentorId && row.studentId === u.id)).exists
  } yield (u.id, u.name, u.profileImage)

  def getStudentsForMentor(mentorId: Int): Future[Seq[StudentsForList]] = db.run(getStudentsForMentorQuery(mentorId).result).map(rows => rows.map { row =>
    StudentsForList(row._1, row._2.getOrElse(""), row._3.getOrElse(""))
  })
}