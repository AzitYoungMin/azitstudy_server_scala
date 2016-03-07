package services

import controllers.api.StaticValues
import controllers.api.helpers.PostingReplies
import play.api.{Logger, Play}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class RepliesService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val replies = TableQuery[Tables.Replies]

  private val users = TableQuery[Tables.Users]

  private val replyLikes = TableQuery[Tables.ReplyLike]

  val format2 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")

  def all(): Future[Seq[RepliesRow]] = db.run(replies.result)

  def insert(reply: RepliesRow): Future[Int] = db.run((replies returning replies.map(_.id)) += reply).map(id => id)

  def delete(id: Int): Future[Int] = db.run(replies.filter(_.id === id).delete)

  def update(reply: RepliesRow) = db.run(replies.filter(_.id === reply.id).update(reply))

  def findById(id: Int): Future[Option[RepliesRow]] = db.run(replies.filter(_.id === id).result.headOption)

  def findByIdAndUserId(id: Int, userId: Int): Future[Option[RepliesRow]] = db.run(replies.filter(row => (row.id === id && row.userId === userId)).result.headOption)

  def getRepliesByPostingIdQuery(id: Int) = for {
    r <- replies.filter(row => (row.postingId === id && row.id === row.parentId && row.isDeleted === false))
    u <- users.filter(_.id === r.userId)
//    l <- replyLikes.filter(row => (row.replyId === r.id && row.userId === u.id))
  } yield (r, u)

  def getRepliesOfReplyByParentIdQuery(id: Int) = for {
    (r, u) <- replies.filter(_.parentId === id) join users on (_.userId === _.id)
    if r.id =!= r.parentId
  } yield (r, u)

  def getRepliesByPostingId(id: Int): Future[Seq[PostingReplies]] = db.run(getRepliesByPostingIdQuery(id).result).map(rows => rows.map { row =>
    PostingReplies(reply_id = row._1.id, reply_article = row._1.reply, reply_images = row._1.imageUrl, writer = row._2.name, profile_image = row._2.profileImage, user_id = row._2.id, role_of_writer = StaticValues.USER_TYPE.lift(row._2.typeId), created_at = Option(format2.format(row._1.createdAt.get)), num_of_like = row._1.numOfLike, num_of_reply = row._1.numOfReply)
  })

  def getRepliesOfReplyByParentId(id: Int): Future[Seq[PostingReplies]] = db.run(getRepliesOfReplyByParentIdQuery(id).result).map(rows => rows.map { row =>
    PostingReplies(row._1.id, row._1.reply, row._1.imageUrl, row._2.name, row._2.profileImage, row._2.id, StaticValues.USER_TYPE.lift(row._2.typeId), Option(format2.format(row._1.createdAt.get)))
  })

  def countByPostingId(id: Int): Future[Int] = db.run(replies.filter(_.postingId === id).length.result)

  def deleteReply(id: Int) = db.run(replies.filter(_.id === id).map(_.isDeleted).update(Option(true)))
}