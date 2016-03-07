package services

import caseClasses.{UserForApproval, UserForList}
import controllers.api.StaticValues
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import play.api.{Logger, Play}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class UsersService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val users = TableQuery[Tables.Users]

  private val eduInst = TableQuery[Tables.EducationalInst]

  private val userEdu = TableQuery[Tables.UserEdu]

  def all(): Future[Seq[UsersRow]] = db.run(users.result)

  def insert(user: UsersRow): Future[Int] = {
    Logger.info("[Users insert] request")
    val action =(users returning users.map(_.id)) += user
    db.run(action).map(id => id)
  }

  def delete(id: Int): Future[Int] = db.run(users.filter(_.id === id).delete)

  def findById(id: Int): Future[Option[UsersRow]] =  db.run(users.filter(_.id === id).result.headOption)

  def findByEmail(email: String): Future[Option[UsersRow]] =  db.run(users.filter(_.email === email).result.headOption)

  def authenticate(email: String, password: String): Option[UsersRow] = {
    Logger.info("[Users authenticate] request")
    val user = Await.result(findByEmail(email), Duration.Inf)
    if (user.isDefined && BCrypt.checkpw(password, user.get.password.getOrElse("password")) && !user.get.isWithdrawal.get) {
      user
    } else {
      None
    }
  }

  def findBySecret(secret: String): Future[Option[UsersRow]] =  db.run(users.filter(_.secret === secret).result.headOption)

  def update(user: UsersRow) = db.run(users.filter(_.secret === user.secret).update(user))

  def findByType(typeId: Int): Future[Seq[UsersRow]] = db.run(users.filter(_.typeId === typeId).result)

  def updatePhoneQuery(id: Int) = for {u <- users if u.id === id} yield u.phone

  def updatePhone(id: Int, phone: String) = db.run(updatePhoneQuery(id).update(Option(phone)))

  def updatePasswordQuery(id: Int) = for {u <- users if u.id === id} yield u.password

  def updatePassword(id: Int, password: String) = db.run(updatePasswordQuery(id).update(Option(BCrypt.hashpw(password, BCrypt.gensalt()))))

  def updatePushQuery(id: Int) = for {u <- users if u.id === id} yield u.push

  def updatePush(id: Int, push: Boolean) = db.run(updatePushQuery(id).update(Option(push)))

  def withdrawalQuery(id: Int) = for {u <- users if u.id === id} yield u.isWithdrawal

  def withdrawal(id: Int) = db.run(withdrawalQuery(id).update(Option(true)))

  def setTokenQuery(id: Int) = for {u <- users if u.id === id} yield u.token

  def setToken(id: Int, token: String) = db.run(setTokenQuery(id).update(Option(token)))

  def getToken(id: Int): Future[Option[Option[String]]]= db.run(users.filter(_.id === id).map(_.token).result.headOption)

  def getTokenByIds(ids: List[Int]): Future[Seq[Option[String]]]= db.run(users.filter(_.id inSet ids).map(_.token).result)

  def getUsersForListQuery(userType: List[Int], email: String, name: String, school: String, isWithdrawal: Boolean) = for {
    (u, ue) <- {var userQuery = users.sortBy(_.id.desc)
      if(!userType.isEmpty) userQuery = userQuery.filter(_.typeId inSet userType)
      if(!email.equals("")) userQuery = userQuery.filter(_.email like email+"%")
      if(!name.equals("")) userQuery = userQuery.filter(_.name like name+"%")
      if(isWithdrawal) userQuery = userQuery.filter(_.isWithdrawal === true)
      userQuery
    } join userEdu.filter(_.isDefault === true) on(_.id === _.userId)
    e <- {var eduInstQuery = eduInst.filter(_.id === ue.eduInstId)
      if(!school.equals("")) eduInstQuery = eduInstQuery.filter(_.name like school+"%")
      eduInstQuery
    }
  } yield (u.id, u.typeId, u.name, u.email, e.name, u.phone, u.createdAt, u.isWithdrawal, u.withdrawalApproval)

  def getUsersForList(userType: List[Int], email: String, name: String, school: String, isWithdrawal: Boolean, startIndex: Int, pageSize: Int): Future[Seq[UserForList]] = db.run(getUsersForListQuery(userType, email, name, school, isWithdrawal).drop(startIndex).take(pageSize).result).map(rows => rows.map{ row =>
    UserForList(id = row._1, userType = StaticValues.USER_TYPE(row._2), name = row._3.getOrElse(""), email = row._4.getOrElse(""), school = row._5.getOrElse(""), phone = row._6.getOrElse(""), createdAt = row._7.get, isWithdrawal = row._8.get, withdrawApproval = row._9.get)
  })

  def countUsersForList(userType: List[Int], email: String, name: String, school: String, isWithdrawal: Boolean): Future[Int] = db.run(getUsersForListQuery(userType, email, name, school, isWithdrawal).length.result)

  def getUsersForWithdrawQuery(email: String, name: String) = for {
    (u, ue) <- {var userQuery = users.sortBy(_.id.desc).filter(row =>(row.isWithdrawal === true && row.withdrawalApproval === false))
      if(!email.equals("")) userQuery = userQuery.filter(_.email like email+"%")
      if(!name.equals("")) userQuery = userQuery.filter(_.name like name+"%")
      userQuery
    } join userEdu.filter(_.isDefault === true) on(_.id === _.userId)
    e <- eduInst.filter(_.id === ue.eduInstId)
  } yield (u.id, u.typeId, u.name, u.email, e.name, u.phone, u.createdAt, u.isWithdrawal, u.withdrawalApproval)

  def getUsersForWithdraw(email: String, name: String, startIndex: Int, pageSize: Int): Future[Seq[UserForList]] = db.run(getUsersForWithdrawQuery(email, name).drop(startIndex).take(pageSize).result).map(rows => rows.map{ row =>
    UserForList(id = row._1, userType = StaticValues.USER_TYPE(row._2), name = row._3.getOrElse(""), email = row._4.getOrElse(""), school = row._5.getOrElse(""), phone = row._6.getOrElse(""), createdAt = row._7.get, isWithdrawal = row._8.get, withdrawApproval = row._9.get)
  })

  def countUsersForWithdraw(email: String, name: String): Future[Int] = db.run(getUsersForWithdrawQuery(email, name).length.result)

  def getUsersForApprovalQuery(userType: List[Int], email: String, name: String, approval: Int) = for {
    (u, ue) <- {var userQuery = users.sortBy(_.id.asc).filter(_.typeId inSet userType).filter(_.isWithdrawal === false)
      if(!email.equals("")) userQuery = userQuery.filter(_.email like email+"%")
      if(!name.equals("")) userQuery = userQuery.filter(_.name like name+"%")
      if(approval == 0) userQuery = userQuery.filter(_.isAuthenticated === approval)
      userQuery
    } join userEdu.filter(_.isDefault === true) on(_.id === _.userId)
    e <- eduInst.filter(_.id === ue.eduInstId)
  } yield (u.id, u.typeId, u.name, u.email, e.name, u.phone, u.createdAt, u.isAuthenticated)

  def mentorsService = new MentorsService

  def getUsersForApproval(userType: List[Int], email: String, name: String, approval: Int, startIndex: Int, pageSize: Int): Future[Seq[UserForApproval]] = db.run(getUsersForApprovalQuery(userType, email, name, approval).drop(startIndex).take(pageSize).result).map(rows => rows.map{ row =>
    var image = ""
    if(row._2 == StaticValues.USER_TYPE_MENTOR){
      image = Await.result(mentorsService.getCardImage(row._1), Duration.Inf).get.getOrElse("")
    }
    UserForApproval(id = row._1, userType = StaticValues.USER_TYPE(row._2), name = row._3.getOrElse(""), email = row._4.getOrElse(""), school = row._5.getOrElse(""), phone = row._6.getOrElse(""), createdAt = row._7.get, image = image, approval = row._8.get)
  })

  def countUsersForApproval(userType: List[Int], email: String, name: String, approval: Int): Future[Int] = db.run(getUsersForApprovalQuery(userType, email, name, approval).length.result)

  def accept(id: Int) = db.run(users.filter(_.id === id).map(_.isAuthenticated).update(Option(1)))

  def deny(id: Int) = db.run(users.filter(_.id === id).map(_.isAuthenticated).update(Option(2)))

  def approvalWithdraw(id: Int) = db.run(users.filter(_.id === id).map(_.withdrawalApproval).update(Option(true)))
}