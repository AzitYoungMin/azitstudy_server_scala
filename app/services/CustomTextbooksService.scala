package services

import controllers.api.helpers.TextbookList
import play.api.{Logger, Play}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CustomTextbooksService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val customTextbooks = TableQuery[Tables.CustomTextbooks]

  def all(): Future[Seq[CustomTextbooksRow]] = db.run(customTextbooks.result)

//  def insert(customTextbooksRow: CustomTextbooksRow): Future[Unit]  =  db.run(customTextbooks += customTextbooksRow).map(_ => ())

  def insert(customTextbooksRow: CustomTextbooksRow): Future[Int] = {
    val action =(customTextbooks returning customTextbooks.map(_.id)) += customTextbooksRow
    db.run(action).map(id => id)
  }

  def delete(id: Int): Future[Int] = db.run(customTextbooks.filter(_.id === id).delete)

  def update(customTextbooksRow: CustomTextbooksRow) = db.run(customTextbooks.filter(_.id === customTextbooksRow.id).update(customTextbooksRow))

  def findById(id: Int): Future[Option[CustomTextbooksRow]] = db.run(customTextbooks.filter(_.id === id).result.headOption)

  def findBySubjectIds(subjectIds: List[Int], userId: Int): Future[Seq[TextbookList]] = db.run(customTextbooks.filter(_.userId === userId).filter(_.subjectId inSet subjectIds).result).map(rows => rows.map(row =>
    TextbookList(id = row.id, title = row.title.get, isCustom = true)
  ))

  def findBySubjectId(subjectId: Int, userId: Int): Future[Seq[TextbookList]] = db.run(customTextbooks.filter(_.userId === userId).filter(_.subjectId === subjectId).result).map(rows => rows.map(row =>
    TextbookList(id = row.id, title = row.title.get, isCustom = true)
  ))
}