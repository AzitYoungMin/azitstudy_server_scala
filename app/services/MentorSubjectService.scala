package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MentorSubjectService extends HasDatabaseConfig[JdbcProfile] {

   import slick.driver.MySQLDriver.api._

   val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

   private val mentorSubjects = TableQuery[Tables.MentorSubjects]

   def all(): Future[Seq[MentorSubjectsRow]] = db.run(mentorSubjects.result)

   def insert(mentorSubject: MentorSubjectsRow): Future[Unit] = db.run(mentorSubjects += mentorSubject).map { _ => () }

   def delete(mentorId: Int, subjectId: Int): Future[Int] = db.run(mentorSubjects.filter(row => (row.mentorId === mentorId && row.subjectId === subjectId)).delete)

   def findByMentorId(id: Int): Future[Seq[MentorSubjectsRow]] = db.run(mentorSubjects.filter(_.mentorId === id).result)
 }