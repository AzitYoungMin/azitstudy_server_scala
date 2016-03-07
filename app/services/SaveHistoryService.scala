package services

import caseClasses.PointManageList
import controllers.api.helpers.SaveHistoryList
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SaveHistoryService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val saveHistory = TableQuery[Tables.SaveHistory]

  private val postings = TableQuery[Tables.Postings]

  private val users = TableQuery[Tables.Users]

  private val mentors = TableQuery[Tables.Mentors]

  private val students = TableQuery[Tables.Students]

  private val eduInst = TableQuery[Tables.EducationalInst]

  private val userEdu = TableQuery[Tables.UserEdu]

  def all(): Future[Seq[SaveHistoryRow]] = db.run(saveHistory.result)

  def insert(saveHistoryRow: SaveHistoryRow): Future[Int] = {
    val action =(saveHistory returning saveHistory.map(_.id)) += saveHistoryRow
    db.run(action).map(id => id)
  }
  def delete(id: Int): Future[Int] = db.run(saveHistory.filter(_.id === id).delete)

  def update(saveHistoryRow: SaveHistoryRow) = db.run(saveHistory.filter(_.id === saveHistoryRow.id).update(saveHistoryRow))

  def findById(id: Int): Future[Option[SaveHistoryRow]] = db.run(saveHistory.filter(_.id === id).result.headOption)

  val format = new java.text.SimpleDateFormat("yyyy/MM/dd")

  def getSaveHistoryQuery(id: Int) = for {
    history <- saveHistory.filter(_.mentorId === id)
    posting <- postings.filter(_.id === history.postingId)
  } yield (history, posting.title)

  def getSaveHistory(id: Int): Future[Seq[SaveHistoryList]] = db.run(getSaveHistoryQuery(id).result).map(rows => rows.map(row =>
    SaveHistoryList(row._2.get, format.format(row._1.createdAt.get), row._1.balance.get, row._1.addedPoint.get)
  ))

  def getSaveHistoryForListQuery(searchType: String, keyword: String, startDate: java.sql.Date, endDate: java.sql.Date) = for{
    history <- saveHistory.filter(row => (row.createdAt >= startDate && row.createdAt <= endDate)).sortBy(_.id.desc)
    mentor <- {
      var query = mentors.filter(_.id === history.mentorId)
      if(searchType.equals("nickname") && !keyword.equals("")) query = query.filter(_.nickname like keyword+"%")
      query
    }
    u1 <- {var userQuery = users.filter(_.id === mentor.id)
      if(searchType.equals("email") && !keyword.equals("")) userQuery = userQuery.filter(_.email like keyword+"%")
      if(searchType.equals("name") && !keyword.equals("")) userQuery = userQuery.filter(_.name like keyword+"%")
      userQuery
    }
    ue <- userEdu.filter(row=>(row.userId === u1.id && row.isDefault === true))
    e <- eduInst.filter(_.id === ue.eduInstId)
    posting <- postings.filter(_.id === history.postingId)
    student <- students.filter(_.id === posting.userId)
    u2 <- users.filter(_.id === posting.userId)
  } yield (posting.typeId, u1.name, mentor.nickname, u1.email, e.name, u1.phone, history.addedPoint, u2.name, student.nickname, u2.email, history.postingId)

  def getSaveHistoryForList(searchType: String, keyword: String, startDate: java.sql.Date, endDate: java.sql.Date, startIndex: Int, pageSize: Int): Future[Seq[PointManageList]] = db.run(getSaveHistoryForListQuery(searchType, keyword, startDate, endDate).drop(startIndex).take(pageSize).result).map(rows => rows.map { row =>
    PointManageList(typeId = row._1, name = row._2.get, nickName = row._3, email = row._4.get, school = row._5.get, phone = row._6.get, point = row._7.get, studentName = row._8.get, studentNickName = row._9, studentEmail = row._10.get, postingId = row._11.get)
  })

  def countSaveHistoryForList(searchType: String, keyword: String, startDate: java.sql.Date, endDate: java.sql.Date): Future[Int] = db.run(getSaveHistoryForListQuery(searchType, keyword, startDate, endDate).length.result)


}