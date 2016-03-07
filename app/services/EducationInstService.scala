package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EducationInstService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val eduInst = TableQuery[Tables.EducationalInst]

  def all(): Future[Seq[EducationalInstRow]] = db.run(eduInst.result)

  def insert(educationalInstRow: EducationalInstRow): Future[Unit] =  db.run(eduInst += educationalInstRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(eduInst.filter(_.id === id).delete)

  def update(educationalInstRow: EducationalInstRow) = db.run(eduInst.filter(_.id === educationalInstRow.id).update(educationalInstRow))

  def findById(id: Int): Future[Option[EducationalInstRow]] = db.run(eduInst.filter(_.id === id).result.headOption)

  def findNameById(id: Int): Future[Option[Option[String]]] = db.run(eduInst.filter(_.id === id).map(_.name).result.headOption)
}