package services

import caseClasses.MentorForPushList
import controllers.api.StaticValues
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MentorsService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val mentors = TableQuery[Tables.Mentors]

  private val users = TableQuery[Tables.Users]

  private val educationalInst = TableQuery[Tables.EducationalInst]

  private val userEdu = TableQuery[Tables.UserEdu]

  private val mentorSubjects = TableQuery[Tables.MentorSubjects]

  def all(): Future[Seq[MentorsRow]] = db.run(mentors.result)

  def insert(mentor: MentorsRow): Future[Unit] = db.run(mentors += mentor).map { _ => () }

  def delete(id: Int): Future[Int] = db.run(mentors.filter(_.id === id).delete)

  def update(mentor: MentorsRow) = db.run(mentors.filter(_.id === mentor.id).update(mentor))

  def findById(id: Int): Future[Option[MentorsRow]] = db.run(mentors.filter(_.id === id).result.headOption)

//  def findForApprovalPaging(searchType: String, keyword: String,start: Int, pageSize: Int) = (for{
//    mentors <- mentors.filter(_.isAuthenticated === false)
//    users <- users.filter(_.id === mentors.id)
//    userEdu <- userEdu.filter(row => (row.userId === users.id && row.isDefault === true))
//    educationalInst <- educationalInst.filter(_.id === userEdu.eduInstId)
//  } yield (mentors, users, educationalInst))
//
//
// def findMemberTotalQuery(searchType: String, keyword: String) = (for{
//    mentors <- mentors.filter(_.isAuthenticated === false)
//    users <- (
//          if(searchType == "email"){
//           users.filter(_.id === mentors.id).filter(_.email like "%"+keyword+"%")
//          }else if(searchType == "name"){
//           users.filter(_.id === mentors.id).filter(_.name like "%"+keyword+"%")
//          }else if(searchType == "membership"){
//           users.filter(_.id === mentors.id).filter(_.email like "%"+keyword+"%")
//          }else if(searchType == "approval"){
//           users.filter(_.id === mentors.id).filter(_.email like "%"+keyword+"%")
//          }else{
//            users.filter(_.id === mentors.id)
//          }
//    )
//
//    userEdu <- userEdu.filter(row => (row.userId === users.id && row.isDefault === true))
//    educationalInst <- educationalInst.filter(_.id === userEdu.eduInstId)
//  } yield (mentors, users, educationalInst)).length
  
//  def findMemberTotal(searchType: String, keyword: String): Future[TotalElement] = db.run(findMemberTotalQuery(searchType,keyword).result).map(rows =>
//    TotalElement(total = rows)
//  )
  
// def findMemberPaging(searchType: String, keyword: String,start: Int, pageSize: Int) = (for{
//    mentors <- mentors.filter(_.isAuthenticated === false)
//    users <- (
//          if(searchType == "email"){
//           users.filter(_.id === mentors.id).filter(_.email like "%"+keyword+"%")
//          }else if(searchType == "name"){
//           users.filter(_.id === mentors.id).filter(_.name like "%"+keyword+"%")
//          }else if(searchType == "membership"){
//           users.filter(_.id === mentors.id).filter(_.email like "%"+keyword+"%")
//          }else if(searchType == "approval"){
//           users.filter(_.id === mentors.id).filter(_.email like "%"+keyword+"%")
//          }else{
//            users.filter(_.id === mentors.id)
//          }
//    )
//    userEdu <- userEdu.filter(row => (row.userId === users.id && row.isDefault === true))
//    educationalInst <- educationalInst.filter(_.id === userEdu.eduInstId)
//  } yield (mentors, users, educationalInst)).drop(start).take(pageSize)
//
//  def findForMemberApprovalPadding(searchType: String, keyword: String,start: Int, pageSize: Int): Future[Seq[MentorsForList]] = db.run(findMemberPaging(searchType,keyword,start, pageSize).result).map(rows => rows.map(row =>
//    MentorsForList(id = row._1.id, name = row._2.name, nickname = row._1.nickname, email = row._2.email, phone = row._2.phone, university = row._3.name, year = row._1.year, gender = row._2.gender, isAuthenticated = row._1.isAuthenticated)
//  ))
//
//  def findForApprovalPadding(searchType: String, keyword: String,start: Int, pageSize: Int): Future[Seq[MentorsForList]] = db.run(findForApprovalPaging(searchType,keyword,start, pageSize).result).map(rows => rows.map(row =>
//  MentorsForList(id = row._1.id, name = row._2.name, nickname = row._1.nickname, email = row._2.email, phone = row._2.phone, university = row._3.name, year = row._1.year, gender = row._2.gender, isAuthenticated = row._1.isAuthenticated)
//		  ))
  
//  def findForApprovalQuery() = for{
//    mentors <- mentors.filter(_.isAuthenticated === false)
//    users <- users.filter(_.id === mentors.id)
//    userEdu <- userEdu.filter(row => (row.userId === users.id && row.isDefault === true))
//    educationalInst <- educationalInst.filter(_.id === userEdu.eduInstId)
//  } yield (mentors, users, educationalInst)
//
//  def findForApproval(): Future[Seq[MentorsForList]] = db.run(findForApprovalQuery().result).map(rows => rows.map (row =>
//    MentorsForList(id = row._1.id, name = row._2.name, nickname = row._1.nickname, email = row._2.email, phone = row._2.phone, university = row._3.name, year = row._1.year, gender = row._2.gender, isAuthenticated = row._1.isAuthenticated)
//  ))

  def updateApprovalQuery(id: Int) = for {m <- mentors if m.id === id} yield m.isAuthenticated

  def updateApproval(id: Int) = db.run(updateApprovalQuery(id).update(Option(true)))

  def findByNickname(nickname: String): Future[Option[MentorsRow]] = db.run(mentors.filter(_.nickname === nickname).result.headOption)

  def findPointById(id:Int): Future[Option[Option[Int]]] = db.run(mentors.filter(_.id === id).map(_.point).result.headOption)

  def getMentorsForPushQuery(university: String, major: String, year: Int, gender: String) = for {
    m <- {
      var mentorQuery = mentors.sortBy(_.id.desc)
      if (university != "") mentorQuery = mentorQuery.filter(_.university like university + "%")
      if (major != "") mentorQuery = mentorQuery.filter(_.major like major + "%")
      if (year != 0) mentorQuery = mentorQuery.filter(_.year === year)
      mentorQuery
    }
    u <- {
      var userQuery = users.filter(row => (row.id === m.id && !row.token.isEmpty && row.push === true && row.withdrawalApproval === false))
      if(gender != "") userQuery = userQuery.filter(_.gender === gender)
      userQuery
    }

  } yield (u.id, u.name, m.university, m.year, m.major, u.gender)

  def getMentorsForPush(university: String, major: String, year: Int, gender: String, startIndex: Int, pageSize: Int): Future[Seq[MentorForPushList]] = db.run(getMentorsForPushQuery(university, major, year, gender).result).map(rows => rows.map{row =>
    val year = row._4.get
    var yearString = ""
    if(year == 5){
      yearString ="휴학생"
    }else if(year == 6){
      yearString ="졸업"
    }else{
      yearString = year + "학년"
    }
    var gender = "남"
    if(row._6.get.equals("F")){
      gender = "여"
    }
    MentorForPushList(id = row._1, name = row._2.getOrElse(""), university = row._3.get, year = yearString,  major = row._5.getOrElse(""), gender = gender)
  })

  def countMentorsForPush(university: String, major: String, year: Int, gender: String): Future[Int] = db.run(getMentorsForPushQuery(university, major, year, gender).length.result)

  def getCardImage(id:Int): Future[Option[Option[String]]] = db.run(mentors.filter(_.id === id).map(_.cardImage).result.headOption)

  def getMentorsBySubjectsForPushQuery(university: String, major: String, year: Int, gender: String, subjects: List[Int]) = for {
    m <- {
      var mentorQuery = mentors.sortBy(_.id.desc)
      if (university != "") mentorQuery = mentorQuery.filter(_.university like university + "%")
      if (major != "") mentorQuery = mentorQuery.filter(_.major like major + "%")
      if (year != 0) mentorQuery = mentorQuery.filter(_.year === year)
      mentorQuery
    }
    u <- {
      var userQuery = users.filter(row => (row.id === m.id && !row.token.isEmpty && row.push === true && row.withdrawalApproval === false))
      if(gender != "") userQuery = userQuery.filter(_.gender === gender)
      userQuery
    }
    if mentorSubjects.filter(_.mentorId === m.id).filter(_.subjectId inSet subjects).exists
  } yield (u.id, u.name, m.university, m.year, m.major, u.gender)

  def getMentorsBySubjectsForPush(university: String, major: String, year: Int, gender: String, subjects: List[Int], startIndex: Int, pageSize: Int): Future[Seq[MentorForPushList]] = db.run(getMentorsBySubjectsForPushQuery(university, major, year, gender, subjects).result).map(rows => rows.map{row =>
    val year = row._4.get
    var yearString = ""
    if(year == 5){
      yearString ="휴학생"
    }else if(year == 6){
      yearString ="졸업"
    }else{
      yearString = year + "학년"
    }
    var gender = "남"
    if(row._6.get.equals("F")){
      gender = "여"
    }
    MentorForPushList(id = row._1, name = row._2.getOrElse(""), university = row._3.get, year = yearString,  major = row._5.getOrElse(""), gender = gender)
  })

  def countMentorsBySubjectsForPush(university: String, major: String, year: Int, gender: String, subjects: List[Int]): Future[Int] = db.run(getMentorsBySubjectsForPushQuery(university, major, year, gender, subjects).length.result)

  def getMentorsIdsBySubjectIdQuery(subjectId: Int) = for {
    u <- users.filter(row => (row.typeId === StaticValues.USER_TYPE_MENTOR && !row.token.isEmpty && row.push === true && row.withdrawalApproval === false))
    if mentorSubjects.filter(_.mentorId === u.id).filter(_.subjectId === subjectId).exists
  } yield (u.id)

  def getMentorsIdsBySubjectId(subjectId: Int): Future[Seq[Int]] = db.run(getMentorsIdsBySubjectIdQuery(subjectId).result)
}