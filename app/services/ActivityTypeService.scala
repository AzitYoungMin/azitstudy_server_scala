package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ActivityTypeService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val activityType = TableQuery[Tables.ActivityType]

  def all(): Future[Seq[ActivityTypeRow]] = db.run(activityType.result)

  def insert(activityTypeRow: ActivityTypeRow): Future[Unit] =  db.run(activityType += activityTypeRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(activityType.filter(_.id === id).delete)

  def update(activityTypeRow: ActivityTypeRow) = db.run(activityType.filter(_.id === activityTypeRow.id).update(activityTypeRow))

  def findById(id: Int): Future[Option[ActivityTypeRow]] = db.run(activityType.filter(_.id === id).result.headOption)

  def isAlone(id: Int): Future[Option[Option[Boolean]]] = db.run(activityType.filter(_.id === id).map(_.isAlone).result.headOption)
}