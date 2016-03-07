package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MemberService extends HasDatabaseConfig[JdbcProfile] {
  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val mentors = TableQuery[Tables.Mentors]

  private val users = TableQuery[Tables.Users]

  private val educationalInst = TableQuery[Tables.EducationalInst]

  private val userEdu = TableQuery[Tables.UserEdu]

  def all(): Future[Seq[MentorsRow]] = db.run(mentors.result)

  def insert(mentor: MentorsRow): Future[Unit] = db.run(mentors += mentor).map { _ => () }

  def delete(id: Int): Future[Int] = db.run(mentors.filter(_.id === id).delete)

  def update(mentor: MentorsRow) = db.run(mentors.filter(_.id === mentor.id).update(mentor))

  def findById(id: Int): Future[Option[MentorsRow]] = db.run(mentors.filter(_.id === id).result.headOption)

  def findForApprovalPaging(searchType: String, keyword: String,start: Int, pageSize: Int) = (for{
    mentors <- mentors.filter(_.isAuthenticated === false)
    users <- users.filter(_.id === mentors.id)
    userEdu <- userEdu.filter(row => (row.userId === users.id && row.isDefault === true))
    educationalInst <- educationalInst.filter(_.id === userEdu.eduInstId)
  } yield (mentors, users, educationalInst))
  
  
 def findMemberByMemberTypeTotalQuery(searchType: String, keyword: String,member_type: List[Int],isWithdrawal: Int) = (for{
     users <- (
      if(isWithdrawal != 1 && searchType == "email"){
        users.filter(_.email like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === false)
      }else if(isWithdrawal != 1 && searchType == "name"){
        users.filter(_.name like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === false)
      }else if(isWithdrawal != 1 && searchType == "school"){
        users.filter(_.email like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === false)
      }else if(isWithdrawal == 1 && searchType == "email"){
        users.filter(_.email like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === true)
      }else if(isWithdrawal == 1 && searchType == "name"){
        users.filter(_.name like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === true)
      }else if(isWithdrawal == 1 && searchType == "school"){
        users.filter(_.email like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === true)
       }else{
         users.filter(_.typeId =!= 1)
       }
       )

     if users.typeId inSet member_type
  } yield (users)).length

  def findMemberTotalQuery(searchType: String, keyword: String,isWithdrawal: Int) = (for{
    users <- (
      if(isWithdrawal != 1 && searchType == "email"){
        users.filter(_.email like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === false)
      }else if(isWithdrawal != 1 && searchType == "name"){
        users.filter(_.name like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === false)
      }else if(isWithdrawal != 1 && searchType == "school"){
        users.filter(_.email like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === false)
      }else if(isWithdrawal == 1 && searchType == "email"){
        users.filter(_.email like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === true)
      }else if(isWithdrawal == 1 && searchType == "name"){
        users.filter(_.name like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === true)
      }else if(isWithdrawal == 1 && searchType == "school"){
        users.filter(_.email like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === true)
      }else{
        users.filter(_.typeId =!= 1)
      }
      )
  } yield (users)).length
  
//  def findMemberTotal(searchType: String, keyword: String,member_type: List[Int],isWithdrawal: Int): Future[TotalElement] = {
//
//    if(member_type.size > 0 ){
//      db.run(findMemberByMemberTypeTotalQuery(searchType,keyword,member_type,isWithdrawal).result).map(rows =>
//        TotalElement(total = rows)
//      )
//    }else{
//      db.run(findMemberTotalQuery(searchType,keyword,isWithdrawal).result).map(rows =>
//        TotalElement(total = rows)
//      )
//    }
//  }

 def findMemberByMemberTypePaging(searchType: String, keyword: String,start: Int, pageSize: Int,member_type: List[Int],isWithdrawal: Int) = (for{
    users <- (
         if(isWithdrawal != 1 && searchType == "email"){
              users.filter(_.email like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === false)
          }else if(isWithdrawal != 1 && searchType == "name"){
              users.filter(_.name like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === false)
          }else if(isWithdrawal != 1 && searchType == "school"){
              users.filter(_.email like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === false)
          }else if(isWithdrawal == 1 && searchType == "email"){
              users.filter(_.email like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === true)
          }else if(isWithdrawal == 1 && searchType == "name"){
              users.filter(_.name like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === true)
          }else if(isWithdrawal == 1 && searchType == "school"){
              users.filter(_.email like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === true)
          }else{
              users.filter(_.typeId =!= 1)
          }
    )

      if users.typeId inSet member_type

 } yield (users)).drop(start).take(pageSize)


  def findMemberPaging(searchType: String, keyword: String,start: Int, pageSize: Int,isWithdrawal: Int) = (for{
    users <- (
      if(isWithdrawal != 1 && searchType == "email"){
        users.filter(_.email like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === false)
      }else if(isWithdrawal != 1 && searchType == "name"){
        users.filter(_.name like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === false)
      }else if(isWithdrawal != 1 && searchType == "school"){
        users.filter(_.email like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === false)
      }else if(isWithdrawal == 1 && searchType == "email"){
        users.filter(_.email like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === true)
      }else if(isWithdrawal == 1 && searchType == "name"){
        users.filter(_.name like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === true)
      }else if(isWithdrawal == 1 && searchType == "school"){
        users.filter(_.email like "%"+keyword+"%").filter(_.typeId =!= 1).filter(_.isWithdrawal === true)
      }else{
        users.filter(_.typeId =!= 1)
      }
      )
  } yield (users)).drop(start).take(pageSize)


//  def findForMemberPadding(searchType: String, keyword: String,start: Int, pageSize: Int,member_type: List[Int],isWithdrawal: Int): Future[Seq[MemberForList]] ={
//
//    if(member_type.size > 0 ){
//      db.run(findMemberByMemberTypePaging(searchType,keyword,start, pageSize,member_type,isWithdrawal).result).map(rows => rows.map(row =>
//        MemberForList(id = row.id, name = row.name, nickname = Option(""), email = row.email, phone = row.phone, university = Option(""), year = Option(1), gender = row.gender, isAuthenticated = Option(true),createddate = row.createdAt,isWithdrawal = row.isWithdrawal)
//      ))
//    }else {
//      db.run(findMemberPaging(searchType, keyword, start, pageSize,isWithdrawal).result).map(rows => rows.map(row =>
//        MemberForList(id = row.id, name = row.name, nickname = Option(""), email = row.email, phone = row.phone, university = Option(""), year = Option(1), gender = row.gender, isAuthenticated = Option(true),createddate = row.createdAt,isWithdrawal = row.isWithdrawal)
//      ))
//    }
//  }

  def updateApprovalQuery(id: Int) = for {m <- mentors if m.id === id} yield m.isAuthenticated

  def updateApproval(id: Int) = db.run(updateApprovalQuery(id).update(Option(true)))

  def findByNickname(nickname: String): Future[Option[MentorsRow]] = db.run(mentors.filter(_.nickname === nickname).result.headOption)
}