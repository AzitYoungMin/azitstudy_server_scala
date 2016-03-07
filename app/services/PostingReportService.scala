package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PostingReportService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val postingReports = TableQuery[Tables.PostingReport]

  def all(): Future[Seq[PostingReportRow]] = db.run(postingReports.result)

  def insert(postingReportRow: PostingReportRow): Future[Unit] = db.run(postingReports += postingReportRow).map { _ => () }

  def delete(postingId: Int, userId: Int): Future[Int] = db.run(postingReports.filter(row => (row.postingId === postingId && row.userId === userId)).delete)

  def countByPostingId(id: Int): Future[Int] = db.run(postingReports.filter(_.postingId === id).length.result)

  def findByPostingIdAndUserId(postingId: Int, userId: Int): Future[Option[PostingReportRow]] = db.run(postingReports.filter(row => (row.postingId === postingId && row.userId === userId)).result.headOption)
}