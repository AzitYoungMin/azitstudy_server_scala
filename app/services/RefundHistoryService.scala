package services

import caseClasses.{RefundCompleteHistory, RefundHistoryForList}
import controllers.api.helpers.RefundHistoryList
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RefundHistoryService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val refundHistory = TableQuery[Tables.RefundHistory]

  private val postings = TableQuery[Tables.Postings]

  private val users = TableQuery[Tables.Users]

  private val eduInst = TableQuery[Tables.EducationalInst]

  private val userEdu = TableQuery[Tables.UserEdu]

  private val mentors = TableQuery[Tables.Mentors]

  def all(): Future[Seq[RefundHistoryRow]] = db.run(refundHistory.result)

  def insert(refundHistoryRow: RefundHistoryRow): Future[Int] = {
    val action =(refundHistory returning refundHistory.map(_.id)) += refundHistoryRow
    db.run(action).map(id => id)
  }
  def delete(id: Int): Future[Int] = db.run(refundHistory.filter(_.id === id).delete)

  def update(refundHistoryRow: RefundHistoryRow) = db.run(refundHistory.filter(_.id === refundHistoryRow.id).update(refundHistoryRow))

  def findById(id: Int): Future[Option[RefundHistoryRow]] = db.run(refundHistory.filter(_.id === id).result.headOption)

  val format = new java.text.SimpleDateFormat("yyyy/MM/dd")

  def getRefundHistory(id: Int): Future[Seq[RefundHistoryList]] = db.run(refundHistory.filter(row => (row.mentorId === id && row.isApproval === Option(true))).result).map(rows => rows.map(row =>
    RefundHistoryList(row.account.get, format.format(row.updatedAt.get), row.amount.get, row.balance.get)
  ))

  def getRefundHistoryForListQuery(searchType: String, keyword: String, startDate: java.sql.Date, endDate: java.sql.Date) = for{
    history <- refundHistory.filter(row => (row.isApproval === false && row.createdAt >= startDate && row.createdAt <= endDate)).sortBy(_.id.desc)
    mentor <- mentors.filter(_.id === history.mentorId)
    u1 <- {var userQuery = users.filter(_.id === history.mentorId)
      if(searchType.equals("email") && !keyword.equals("")) userQuery = userQuery.filter(_.email like keyword+"%")
      if(searchType.equals("name") && !keyword.equals("")) userQuery = userQuery.filter(_.name like keyword+"%")
      userQuery
    }
    ue <- userEdu.filter(row=>(row.userId === u1.id && row.isDefault === true))
    e <- eduInst.filter(_.id === ue.eduInstId)
  } yield (u1.name, u1.email, e.name, u1.phone, mentor.point, history)

  def getRefundHistoryForList(searchType: String, keyword: String, startDate: java.sql.Date, endDate: java.sql.Date, startIndex: Int, pageSize: Int): Future[Seq[RefundHistoryForList]] = db.run(getRefundHistoryForListQuery(searchType, keyword, startDate, endDate).drop(startIndex).take(pageSize).result).map(rows => rows.map { row =>
    val history = row._6
    val account = history.bank.get + ", " + history.name.get + ", " + history.account.get
    RefundHistoryForList(name = row._1.get, email = row._2.get, school = row._3.get, phone = row._4.get, point = row._5.get, amount = history.amount.get, account = account, isApproval = history.isApproval.get, id = history.id)
  })

  def countRefundHistoryForList(searchType: String, keyword: String, startDate: java.sql.Date, endDate: java.sql.Date): Future[Int] = db.run(getRefundHistoryForListQuery(searchType, keyword, startDate, endDate).length.result)

  def getRefundCompleteHistoryQuery(searchType: String, keyword: String, startDate: java.sql.Date, endDate: java.sql.Date) = for{
    history <- refundHistory.filter(row => (row.isApproval === true && row.updatedAt >= startDate && row.updatedAt <= endDate)).sortBy(_.id.desc)
    u1 <- {var userQuery = users.filter(_.id === history.mentorId)
      if(searchType.equals("email") && !keyword.equals("")) userQuery = userQuery.filter(_.email like keyword+"%")
      if(searchType.equals("name") && !keyword.equals("")) userQuery = userQuery.filter(_.name like keyword+"%")
      userQuery
    }
    ue <- userEdu.filter(row=>(row.userId === u1.id && row.isDefault === true))
    e <- eduInst.filter(_.id === ue.eduInstId)
  } yield (u1.name, u1.email, e.name, u1.phone, history)

  def getRefundCompleteHistory(searchType: String, keyword: String, startDate: java.sql.Date, endDate: java.sql.Date, startIndex: Int, pageSize: Int): Future[Seq[RefundCompleteHistory]] = db.run(getRefundCompleteHistoryQuery(searchType, keyword, startDate, endDate).drop(startIndex).take(pageSize).result).map(rows => rows.map { row =>
    val history = row._5
    RefundCompleteHistory(name = row._1.get, email = row._2.get, school = row._3.get, phone = row._4.get, previous = history.amount.get + history.balance.get, refund = history.amount.get, balance = history.balance.get)
  })

  def countRefundCompleteHistory(searchType: String, keyword: String, startDate: java.sql.Date, endDate: java.sql.Date): Future[Int] = db.run(getRefundCompleteHistoryQuery(searchType, keyword, startDate, endDate).length.result)

  def getTotalAmount(startDate: java.sql.Date, endDate: java.sql.Date): Future[Option[Int]] = db.run(refundHistory.filter(row => (row.isApproval === true && row.updatedAt >= startDate && row.updatedAt <= endDate)).map(_.amount).sum.result)
}