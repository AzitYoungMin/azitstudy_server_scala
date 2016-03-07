package services

import controllers.api.helpers.TextbookList
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TextbooksService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val textbooks = TableQuery[Tables.Textbooks]

  def all(): Future[Seq[TextbooksRow]] = db.run(textbooks.result)

  def insert(textbooksRow: TextbooksRow): Future[Unit] =  db.run(textbooks += textbooksRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(textbooks.filter(_.id === id).delete)

  def update(textbooksRow: TextbooksRow) = db.run(textbooks.filter(_.id === textbooksRow.id).update(textbooksRow))

  def findById(id: Int): Future[Option[TextbooksRow]] = db.run(textbooks.filter(_.id === id).result.headOption)

  def findBySubjectIds(subjectIds: List[Int]): Future[Seq[TextbookList]] = db.run(textbooks.filter(_.subjectId inSet subjectIds).result).map(rows => rows.map(row =>
    TextbookList(id = row.id, title = row.title.get)
  ))
}