package services

import caseClasses.{ChoiceChangeList, PointManageList}
import controllers.api.helpers.SaveHistoryList
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChangeChoiceHistoryService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val changeChoiceHistory = TableQuery[Tables.ChangeChoiceHistory]

  private val postings = TableQuery[Tables.Postings]

  private val users = TableQuery[Tables.Users]

  def all(): Future[Seq[ChangeChoiceHistoryRow]] = db.run(changeChoiceHistory.result)

  def insert(saveHistoryRow: ChangeChoiceHistoryRow): Future[Int] = {
    val action =(changeChoiceHistory returning changeChoiceHistory.map(_.id)) += saveHistoryRow
    db.run(action).map(id => id)
  }
  def delete(id: Int): Future[Int] = db.run(changeChoiceHistory.filter(_.id === id).delete)

  def update(changeChoiceHistoryRow: ChangeChoiceHistoryRow) = db.run(changeChoiceHistory.filter(_.id === changeChoiceHistoryRow.id).update(changeChoiceHistoryRow))

  def findById(id: Int): Future[Option[ChangeChoiceHistoryRow]] = db.run(changeChoiceHistory.filter(_.id === id).result.headOption)

  val format = new java.text.SimpleDateFormat("yyyy/MM/dd")

  def getHistoryListQuery(keyword: String) = for{
    history <- changeChoiceHistory.sortBy(_.id.desc)
    p1 <- postings.filter(_.id === history.postingId)
    p2 <- postings.filter(_.id === history.choicedId)
    p3 <- postings.filter(_.id === history.newAnswerId)
    u1 <- {var userQuery = users.filter(_.id === p1.userId)
      if(!keyword.equals("")) userQuery = userQuery.filter(_.email like keyword+"%")
      userQuery
    }
    u2 <- users.filter(_.id === p2.userId)
    u3 <- users.filter(_.id === p3.userId)
  } yield (history, u1.name, u2.name, u3.name, p1.typeId)

  def getHistoryList(keyword: String, startIndex: Int, pageSize: Int): Future[Seq[ChoiceChangeList]] = db.run(getHistoryListQuery(keyword).drop(startIndex).take(pageSize).result).map(rows => rows.map { row =>
    val history = row._1
    ChoiceChangeList(id = history.id, owner = row._2.get, postingId = history.postingId.get, choicedUser = row._3.get, newUser = row._4.get, isChanged = history.isChanged, isCanceled = history.isCanceled, typeId = row._5)
  })

  def countHistoryList(keyword: String): Future[Int] = db.run(getHistoryListQuery(keyword).length.result)

  def updateIsCancel(id: Int) = db.run(changeChoiceHistory.filter(_.id === id).map(_.isCanceled).update(true))

  def updateIsChanged(id: Int) = db.run(changeChoiceHistory.filter(_.id === id).map(_.isChanged).update(true))
}