package services

import controllers.api.helpers.NoticesRowForList
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NoticesService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val notices = TableQuery[Tables.Notices]

  def all(): Future[Seq[NoticesRow]] = db.run(notices.result)

  def insert(noticesRow: NoticesRow): Future[Unit] = db.run(notices += noticesRow).map { _ => () }

  def delete(id: Int): Future[Int] = db.run(notices.filter(row => (row.id === id)).delete)

  def update(noticesRow: NoticesRow) = db.run(notices.filter(_.id === noticesRow.id).update(noticesRow))

  def findById(id: Int): Future[Option[NoticesRow]] = db.run(notices.filter(_.id === id).result.headOption)

  def getNotices(startIndex: Int, pageSize: Int): Future[Seq[NoticesRow]] = db.run(notices.sortBy(_.createdAt.desc).drop(startIndex).take(pageSize).result)

  def countNotices(): Future[Int] = db.run(notices.length.result)

  val format = new java.text.SimpleDateFormat("yyyy-MM-dd")

  def getAll(): Future[Seq[NoticesRowForList]] = db.run(notices.sortBy(_.createdAt.desc).result).map(rows => rows.map{row =>
    NoticesRowForList(title = row.title, article = row.article, createdAt = format.format(row.createdAt))
  })
}