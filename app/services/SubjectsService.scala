package services

import controllers.api.helpers.ExamCriteria
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubjectsService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val subjects = TableQuery[Tables.Subjects]

  def all(): Future[Seq[SubjectsRow]] = db.run(subjects.result)

  def insert(subjectsRow: SubjectsRow): Future[Unit] =  db.run(subjects += subjectsRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(subjects.filter(_.id === id).delete)

  def update(subjectsRow: SubjectsRow) = db.run(subjects.filter(_.id === subjectsRow.id).update(subjectsRow))

  def findById(id: Int): Future[Option[SubjectsRow]] = db.run(subjects.filter(_.id === id).result.headOption)


  def findIdByDepth1(depth1: Int): Future[Seq[Int]] = db.run(subjects.filter(_.depth1 === depth1).map(_.id).result)

  def findIdByDepth1AndDepth2(depth1: Int, depth2: Int): Future[Seq[Int]] = db.run(subjects.filter(row =>(row.depth1 === depth1 && row.depth2 === depth2)).map(_.id).result)

  def getSubjectByDepth1(depth1: Int): Future[Seq[Int]] = db.run(subjects.filter(row =>(row.depth1 === depth1 && row.depth2 =!= 0 && row.depth3 === 0)).map(_.id).result)

  def findBySubjectIdIn(subjectIds: List[Int]): Future[Seq[ExamCriteria]] = db.run(subjects.filter(_.id inSet subjectIds).result).map(rows => rows.map{ row =>
    ExamCriteria(id = row.id, title = row.examTitle.get)
  })

  def getTitleBySubjectId(subjectId: Int): Future[Option[Option[String]]] = db.run(subjects.filter(_.id === subjectId).map(_.title).result.headOption)

  def getSubjectsByDepth1(depth1: Int): Future[Seq[Int]] = db.run(subjects.filter(row =>(row.depth1 === depth1 && row.depth3 === 0)).map(_.id).result)
}