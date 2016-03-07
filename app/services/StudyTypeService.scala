package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StudyTypeService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val studyType = TableQuery[Tables.StudyType]

  def all(): Future[Seq[StudyTypeRow]] = db.run(studyType.result)

  def insert(studyTypeRow: StudyTypeRow): Future[Unit] =  db.run(studyType += studyTypeRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(studyType.filter(_.id === id).delete)

  def update(studyTypeRow: StudyTypeRow) = db.run(studyType.filter(_.id === studyTypeRow.id).update(studyTypeRow))

  def findById(id: Int): Future[Option[StudyTypeRow]] = db.run(studyType.filter(_.id === id).result.headOption)

  def isSubject(id: Int): Future[Option[Option[Boolean]]] = db.run(studyType.filter(_.id === id).map(_.isStudy).result.headOption)
}