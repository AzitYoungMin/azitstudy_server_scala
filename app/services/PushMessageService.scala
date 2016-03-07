package services

import controllers.api.helpers.TextbookList
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PushMessageService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val pushMessage = TableQuery[Tables.PushMessage]

  def all(): Future[Seq[PushMessageRow]] = db.run(pushMessage.result)

  def insert(pushMessageRow: PushMessageRow): Future[Unit] =  db.run(pushMessage += pushMessageRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(pushMessage.filter(_.id === id).delete)

  def update(pushMessageRow: PushMessageRow) = db.run(pushMessage.filter(_.id === pushMessageRow.id).update(pushMessageRow))

  def findById(id: Int): Future[Option[PushMessageRow]] = db.run(pushMessage.filter(_.id === id).result.headOption)

  def findByType(typeId: Int): Future[Seq[PushMessageRow]] = db.run(pushMessage.filter(_.`type` === typeId).result)

  def updateMessageQuery(id: Int) = for {u <- pushMessage if u.id === id} yield (u.message, u.day, u.hour, u.minute)

  def updateMessage(id: Int, message: String, day: String, hour: Int, minute: Int) = db.run(updateMessageQuery(id).update(Option(message), Option(day), Option(hour), Option(minute)))
}