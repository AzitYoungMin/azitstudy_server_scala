package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReplyReportService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val replyReports = TableQuery[Tables.ReplyReport]

  def all(): Future[Seq[ReplyReportRow]] = db.run(replyReports.result)

  def insert(replyLike: ReplyReportRow): Future[Unit] = db.run(replyReports += replyLike).map { _ => () }

  def delete(replyId: Int, userId: Int): Future[Int] = db.run(replyReports.filter(row => (row.replyId === replyId && row.userId === userId)).delete)

  def countByReplyId(id: Int): Future[Int] = db.run(replyReports.filter(_.replyId === id).length.result)

  def findByReplyIdAndUserId(replyId: Int, userId: Int): Future[Option[ReplyReportRow]] = db.run(replyReports.filter(row => (row.replyId === replyId && row.userId === userId)).result.headOption)
}