package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CustomActivityRecordsService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val customActivityRecords = TableQuery[Tables.CustomActivityRecords]

  def all(): Future[Seq[CustomActivityRecordsRow]] = db.run(customActivityRecords.result)

  def insert(customActivityRecordsRow: CustomActivityRecordsRow): Future[Unit] =  db.run(customActivityRecords += customActivityRecordsRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(customActivityRecords.filter(_.id === id).delete)

  def update(customActivityRecordsRow: CustomActivityRecordsRow) = db.run(customActivityRecords.filter(_.id === customActivityRecordsRow.id).update(customActivityRecordsRow))

  def findById(id: Int): Future[Option[CustomActivityRecordsRow]] = db.run(customActivityRecords.filter(_.id === id).result.headOption)

  def findByActivityIdAndDate(id: Int, startTime: java.sql.Timestamp, endTime: java.sql.Timestamp): Future[Option[CustomActivityRecordsRow]] = db.run(customActivityRecords.filter(row => (row.customActivityId === id && (row.createdAt >= startTime && row.createdAt <= endTime))).sortBy(_.createdAt.desc).result.headOption)
}