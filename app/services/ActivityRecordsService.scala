package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ActivityRecordsService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val activityRecords = TableQuery[Tables.ActivityRecords]

  def all(): Future[Seq[ActivityRecordsRow]] = db.run(activityRecords.result)

  def insert(activityRecordsRow: ActivityRecordsRow): Future[Unit] =  db.run(activityRecords += activityRecordsRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(activityRecords.filter(_.id === id).delete)

  def update(activityRecordsRow: ActivityRecordsRow) = db.run(activityRecords.filter(_.id === activityRecordsRow.id).update(activityRecordsRow))

  def findById(id: Int): Future[Option[ActivityRecordsRow]] = db.run(activityRecords.filter(_.id === id).result.headOption)

  def findByActivityIdAndDate(id: Int, startTime: java.sql.Timestamp, endTime: java.sql.Timestamp): Future[Option[ActivityRecordsRow]] = db.run(activityRecords.filter(row => (row.activityId === id && (row.createdAt >= startTime && row.createdAt <= endTime))).sortBy(_.createdAt.desc).result.headOption)
}