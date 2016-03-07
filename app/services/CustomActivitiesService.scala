package services

import controllers.api.helpers.ActivitiesRowForList
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CustomActivitiesService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val customActivities = TableQuery[Tables.CustomActivities]

  private val customActivityType = TableQuery[Tables.CustomActivityType]


  def all(): Future[Seq[CustomActivitiesRow]] = db.run(customActivities.result)

  def insert(customActivitiesRow: CustomActivitiesRow): Future[Unit] =  db.run(customActivities += customActivitiesRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(customActivities.filter(_.id === id).delete)

  def update(customActivitiesRow: CustomActivitiesRow) = db.run(customActivities.filter(_.id === customActivitiesRow.id).update(customActivitiesRow))

  def findById(id: Int): Future[Option[CustomActivitiesRow]] = db.run(customActivities.filter(_.id === id).result.headOption)


  def findByStudentIdAndCreatedAtQuery(id: Int, createdAt: java.sql.Date) = for{
    activity <- customActivities.filter(row => (row.studentId === id && row.isDeleted === false && (row.createdAt === createdAt || row.isRepeated === true)))
    activityType <- customActivityType.filter(_.id === activity.customActivityTypeId)
  } yield (activity, activityType)

  def findByStudentIdAndCreatedAt(id: Int, createdAt: java.sql.Date): Future[Seq[ActivitiesRowForList]] = db.run(findByStudentIdAndCreatedAtQuery(id, createdAt).result).map(rows => rows.map(row =>
    ActivitiesRowForList(id = row._1.id, title = row._2.title, sub_title = row._1.contents, activity_type = 3, goal = row._1.goal, activity_type_id = row._2.iconId.get)
  ))

  def updateIsDeletedTrueQuery(id: Int) = for {s <- customActivities if s.id === id} yield s.isDeleted

  def updateIsDeletedTrue(id: Int) = db.run(updateIsDeletedTrueQuery(id).update(Option(true)))
}