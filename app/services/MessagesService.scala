package services

import controllers.api.StaticValues
import controllers.api.helpers.MessagesRowForList
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MessagesService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val messages = TableQuery[Tables.Messages]

  private val users = TableQuery[Tables.Users]

  def all(): Future[Seq[MessagesRow]] = db.run(messages.result)

  def insert(messagesRow: MessagesRow): Future[Unit] =  db.run(messages += messagesRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(messages.filter(_.id === id).delete)

  def update(messagesRow: MessagesRow) = db.run(messages.filter(_.id === messagesRow.id).update(messagesRow))

  def findById(id: Int): Future[Option[MessagesRow]] = db.run(messages.filter(_.id === id).result.headOption)

  val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  def findByReceiverIdQuery(id: Int) = for{
    messages <- messages.filter(_.receiverId === id).sortBy(_.createdAt.desc)
    users <- users.filter(_.id === messages.senderId)
  } yield (messages, users)

  def findByReceiverId(id: Int): Future[Seq[MessagesRowForList]] = db.run(findByReceiverIdQuery(id).result).map(rows => rows.map(row =>
    MessagesRowForList(message_id = row._1.id, message = row._1.message, created_at = Option(format.format(row._1.createdAt.get)), name = Option(row._2.name.get + " " + StaticValues.USER_TYPE(row._2.typeId)), title = row._1.title)
  ))

  def checkIsNewExistByReceiverId(id: Int): Future[Boolean] = db.run(messages.filter(row => (row.receiverId === id && row.isNew === true)).exists.result)

  def getMessageQuery(id: Int) = for{
    messages <- messages.filter(_.id === id)
    users <- users.filter(_.id === messages.senderId)
  } yield (messages, users)

  def getMessage(id: Int): Future[Option[MessagesRowForList]] = db.run(getMessageQuery(id).result.headOption).map(rows => rows.map(row =>
    MessagesRowForList(message_id = row._1.id, message = row._1.message, created_at = Option(format.format(row._1.createdAt.get)), name = Option(row._2.name.get + " " + StaticValues.USER_TYPE(row._2.typeId)), title = row._1.title)
  ))

  def updateIsNewFalseQuery(id: Int) = for {m <- messages if m.id === id} yield m.isNew

  def updateIsNewFalse(id: Int) = db.run(updateIsNewFalseQuery(id).update(Option(false)))
}