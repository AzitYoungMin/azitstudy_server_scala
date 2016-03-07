package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserEduService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val userEdu = TableQuery[Tables.UserEdu]

  private val eduInst = TableQuery[Tables.EducationalInst]

  def all(): Future[Seq[UserEduRow]] = db.run(userEdu.result)

  def insert(userEduRow: UserEduRow): Future[Unit] =  db.run(userEdu += userEduRow).map(_ => ())

  def delete(eduInstId: Int, userId: Int): Future[Int] = db.run(userEdu.filter(row => (row.eduInstId === eduInstId && row.userId === userId)).delete)

  def update(userEduRow: UserEduRow) = db.run(userEdu.filter(row => (row.eduInstId === userEduRow.eduInstId && row.userId === userEduRow.userId)).update(userEduRow))

  def findByUserId(userId: Int): Future[Option[UserEduRow]] = db.run(userEdu.filter(row =>(row.userId === userId && row.isDefault === true)).result.headOption)

  def getTitleByUserIdQuery(userId: Int) = for {
    u <- userEdu.filter(row =>(row.userId === userId && row.isDefault === true))
    e <- eduInst.filter(_.id === u.eduInstId)
  }yield e.name

  def getTitleByUserId(userId: Int): Future[Option[Option[String]]] = db.run(getTitleByUserIdQuery(userId).result.headOption)

  def deleteByUserId(userId: Int): Future[Int] = db.run(userEdu.filter(row => (row.userId === userId && row.isDefault === true)).delete)
}