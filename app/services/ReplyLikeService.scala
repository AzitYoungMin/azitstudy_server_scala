package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReplyLikeService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val replyLikes = TableQuery[Tables.ReplyLike]

  def all(): Future[Seq[ReplyLikeRow]] = db.run(replyLikes.result)

  def insert(replyLike: ReplyLikeRow): Future[Unit] = db.run(replyLikes += replyLike).map { _ => () }

  def delete(replyId: Int, userId: Int): Future[Int] = db.run(replyLikes.filter(row => (row.replyId === replyId && row.userId === userId)).delete)

  def countByReplyId(id: Int): Future[Int] = db.run(replyLikes.filter(_.replyId === id).length.result)

  def checkExist(replyId: Int, userId: Int): Future[Boolean] = db.run(replyLikes.filter(row => (row.replyId === replyId && row.userId === userId)).exists.result)

  def findByReplyIdAndUserId(replyId: Int, userId: Int): Future[Option[ReplyLikeRow]] = db.run(replyLikes.filter(row => (row.replyId === replyId && row.userId === userId)).result.headOption)
}