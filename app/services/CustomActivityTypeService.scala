package services

import controllers.api.helpers.{CustomActivityTypeList, ActivitiesRowForList}
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CustomActivityTypeService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val customActivityType = TableQuery[Tables.CustomActivityType]


  def all(): Future[Seq[CustomActivityTypeRow]] = db.run(customActivityType.result)

  def insert(customActivityTypeRow: CustomActivityTypeRow): Future[Unit] =  db.run(customActivityType += customActivityTypeRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(customActivityType.filter(_.id === id).delete)

  def update(customActivityTypeRow: CustomActivityTypeRow) = db.run(customActivityType.filter(_.id === customActivityTypeRow.id).update(customActivityTypeRow))

  def findByStudentId(studentId: Int): Future[Seq[CustomActivityTypeList]] = db.run(customActivityType.filter(row => (row.studentId === studentId && row.isDelete === false)).result).map(rows => rows.map(row =>
    CustomActivityTypeList(id= row.id, title=row.title.get, icon_id = row.iconId.get)
  ))

  def updateIsDeleteQuery(id: Int) = for {m <- customActivityType if m.id === id} yield m.isDelete

  def updateIsDelete(id: Int) = db.run(updateIsDeleteQuery(id).update(Option(true)))
}