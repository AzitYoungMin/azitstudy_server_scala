package controllers.api

import controllers.api.helpers.{MentorHelper, StudentHelper, CommonHelper, TeacherHelper}
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import services.{UsersService, TeachersService}
import tables.Tables.{OptionalSubjectsRow, TeachersRow}

import scala.collection.immutable.HashMap
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class TeacherApiController extends Controller {
  val secretKey = StaticValues.API_SECRET
  def teachers = new TeachersService

  //회원가입 요청
  def singUp(key: Option[String])  = Action { request =>
    Logger.info("[API Teacher SingUp] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        if(CommonHelper.emailIsDuplicated((bodyJson \ "email").as[String])){
          Logger.info("[API Teacher SingUp] duplicated email")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "duplicated email")))
        }else{
          var newTeacher: TeachersRow = new TeachersRow(0, Option((bodyJson \ "classification").as[String]), Option((bodyJson \ "birthday").as[String]), Option((bodyJson \ "last_school").as[String]), Option((bodyJson \ "is_graduated").as[String].toBoolean))
          val userId = CommonHelper.createUser(bodyJson, StaticValues.USER_TYPE_TEACHER)
          if(userId != 0){
            newTeacher = newTeacher.copy(id = userId)
            val insertedTeacher = TeacherHelper.insert(newTeacher)
            if(insertedTeacher.isDefined){
              Logger.info("[API Teacher SingUp] success")
              def users = new UsersService
              val user = Await.result(users.findById(userId), Duration.Inf)
              var result: Map[String, String] = new HashMap[String, String]
              result += ("result" -> "success")
              result += ("message" -> "signUp success")
              result += ("user_id" -> user.get.id.toString)
              result += ("secret" -> user.get.secret.get)
              Ok(Json.toJson(result))
            }else{
              CommonHelper.deleteUser(userId)
              throw new Exception
            }
          }else{
            throw new Exception
          }
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Teacher SingUp] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Teacher SingUp] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //학생 검색 요청
  def searchStudents(key: Option[String]) = Action { request =>
    Logger.info("[API Teacher search students] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        CommonHelper.secretIsValid(bodyJson) match{
          case Some(user) => {
            val studentList = TeacherHelper.searchStudents(bodyJson)
            if(studentList.size == 0){
              Logger.info("[API Teacher search students] there are not search results")
              Ok(Json.toJson(Map("result" -> "success", "message" -> "there are not search results")))
            }else{
              implicit val formats = Serialization.formats(NoTypeHints)
              var result: Map[String, String] = new HashMap[String, String]
              result += ("result" -> "success")
              result += ("student_list" -> write(studentList))
              result += ("num_of_student" -> studentList.size.toString)
              Logger.info("[API Teacher search students] success")
              Ok(Json.toJson(result))
            }
          }
          case _ => {
            Logger.info("[API Teacher search students] user id and secret key is not matched")
            Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
          }
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Teacher search students] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Teacher search students] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //학생 추가 요청
  def addStudent(key: Option[String]) = Action { request =>
    Logger.info("[API Teacher add students] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        CommonHelper.secretIsValid(bodyJson) match{
          case Some(user) => {
            TeacherHelper.addStudent(bodyJson)
            Logger.info("[API Teacher add students] success")
            Ok(Json.toJson(Map("result" -> "success", "message" -> "add student is success")))
          }
          case _ => {
            Logger.info("[API Teacher add students] user id and secret key is not matched")
            Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
          }
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Teacher add students] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Teacher add students] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //추가된 학생 리스트 요청
  def getStudents(key: Option[String]) = Action { request =>
    Logger.info("[API Teacher get students] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        CommonHelper.secretIsValid(bodyJson) match{
          case Some(user) => {
            val message = TeacherHelper.getStudents(bodyJson)
            Logger.info("[API Teacher get students] success")
            Ok(Json.toJson(message))
          }
          case _ => {
            Logger.info("[API Teacher get students] user id and secret key is not matched")
            Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
          }
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Teacher get students] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Teacher get students] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //프로필 정보 요청
  def profile(key: Option[String]) = Action { request =>
    Logger.info("[API Teacher profile] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val userRow = user.get
          val teacherRow = TeacherHelper.findById(userRow.id).get
          var result: Map[String, String] = new HashMap[String, String]
          result += ("result" -> "success")
          result += ("name" -> userRow.name.getOrElse(""))
          result += ("email" -> userRow.email.getOrElse(""))
          result += ("gender" -> userRow.gender.getOrElse(""))
          result += ("last_school" -> teacherRow.lastSchool.getOrElse(""))
          result += ("is_graduated" -> teacherRow.isGraduated.getOrElse("").toString)
          result += ("phone" -> userRow.phone.getOrElse(""))
          result += ("has_new_answer" -> StudentHelper.hasNewAnswer(userRow.id).toString)
          result += ("has_new_message" -> CommonHelper.hasNewMessage(userRow.id).toString)
          result += ("push" -> userRow.push.get.toString)
          result += ("edu_inst" -> TeacherHelper.getEduInst(userRow.id))
          Logger.info("[API Teacher profile] success")
          Ok(Json.toJson(result))
        } else {
          Logger.info("[API Teacher profile] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          Logger.info("[API Teacher profile] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Teacher profile] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //최종 학력 업데이트 요청
  def updateLastSchool(key: Option[String]) = Action { request =>
    Logger.info("[API Teacher update last school] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          TeacherHelper.updateLastSchool(bodyJson)
          var result: Map[String, String] = new HashMap[String, String]
          result += ("result" -> "success")
          result += ("message" -> "update last school success")
          Logger.info("[API Teacher update last school] success")
          Ok(Json.toJson(result))
        } else {
          Logger.info("[API Teacher update last school] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          Logger.info("[API Teacher update last school] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Teacher update last school] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //소속 학교/학원 업데이트 요청
  def updateEduInst(key: Option[String]) = Action { request =>
    Logger.info("[API Teacher update edu inst] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          TeacherHelper.updateEduInst(bodyJson)
          var result: Map[String, String] = new HashMap[String, String]
          result += ("result" -> "success")
          result += ("message" -> "update edu inst success")
          Logger.info("[API Teacher update edu inst] success")
          Ok(Json.toJson(result))
        } else {
          Logger.info("[API Teacher update edu inst] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Teacher update edu inst] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Teacher update edu inst] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //승인 여부 확인 요청
  def authCheck(key: Option[String]) = Action { request =>
    Logger.info("[API Teacher auth check] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          Logger.info("[API Teacher auth check] success")
          Ok(Json.toJson(Map("result" -> "success", "is_authenticated" -> TeacherHelper.isAuthenticated(user.get.id).toString)))
        } else {
          Logger.info("[API Teacher profile edit] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          Logger.info("[API Teacher auth check] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Teacher auth check] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }
}