package services

import controllers.api.helpers.DDayRowForList
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DDayService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val dDay = TableQuery[Tables.DDay]

  def all(): Future[Seq[DDayRow]] = db.run(dDay.result)

  def insert(dDayRow: DDayRow): Future[Unit] = db.run(dDay += dDayRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(dDay.filter(_.id === id).delete)

  def update(dDayRow: DDayRow) = db.run(dDay.filter(_.id === dDayRow.id).update(dDayRow))

  def findById(id: Int): Future[Option[DDayRow]] = db.run(dDay.filter(_.id === id).result.headOption)

  def findByStudentId(id: Int): Future[Seq[DDayRowForList]] = db.run(dDay.filter(_.studentId === id).result).map(rows => rows.map(row =>
    DDayRowForList(id = row.id, title = row.title, date = row.date, isActive = row.isActive)
  ))

  def findByStudentIdAndIsActive(id: Int, isActive: Boolean): Future[Seq[DDayRow]] = db.run(dDay.filter(row => (row.studentId === id && row.isActive === isActive)).result)

  def findByStudentIdAndIsActive(id: Int): Future[Option[DDayRow]] = db.run(dDay.filter(row => (row.studentId === id && row.isActive === true)).result.headOption)

  def updateIsActiveTrueQuery(id: Int) = for {c <- dDay if c.id === id} yield c.isActive

  def updateIsActiveTrue(id: Int) = db.run(updateIsActiveTrueQuery(id).update(Option(true)))

  def updateIsActiveFalseQuery(id: Int) = for {c <- dDay if c.studentId === id} yield c.isActive

  def updateIsActiveFalse(id: Int) = db.run(updateIsActiveFalseQuery(id).update(Option(false)))

  def countByStudentId(id: Int): Future[Int] = db.run(dDay.filter(_.studentId === id).length.result)


}