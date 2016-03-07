package services

import controllers.api.helpers.ActivitiesRowForList
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ActivitiesService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val activities = TableQuery[Tables.Activities]

  private val activityType = TableQuery[Tables.ActivityType]

  private val activityRecord = TableQuery[Tables.ActivityRecords]

  def all(): Future[Seq[ActivitiesRow]] = db.run(activities.result)

  def insert(activitiesRow: ActivitiesRow): Future[Unit] = db.run(activities += activitiesRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(activities.filter(_.id === id).delete)

  def update(activitiesRow: ActivitiesRow) = db.run(activities.filter(_.id === activitiesRow.id).update(activitiesRow))

  def findById(id: Int): Future[Option[ActivitiesRow]] = db.run(activities.filter(_.id === id).result.headOption)

  def findByStudentIdAndCreatedAtQuery(id: Int, createdAt: java.sql.Date) = for {
    activity <- activities.filter(row => (row.studentId === id && row.isDeleted === false && (row.createdAt === createdAt || row.isRepeated === true)))
    activityType <- activityType.filter(_.id === activity.typeId)
  } yield (activity, activityType)

  def findByStudentIdAndCreatedAt(id: Int, createdAt: java.sql.Date): Future[Seq[ActivitiesRowForList]] = db.run(findByStudentIdAndCreatedAtQuery(id, createdAt).result).map(rows => rows.map(row =>
    ActivitiesRowForList(id = row._1.id, title = row._2.title, sub_title = row._1.contents, activity_type = 2, goal = row._1.goal, activity_type_id = row._1.typeId)
  ))

  def updateIsDeletedTrueQuery(id: Int) = for {s <- activities if s.id === id} yield s.isDeleted

  def updateIsDeletedTrue(id: Int) = db.run(updateIsDeletedTrueQuery(id).update(Option(true)))
}