package controllers.api

import controllers.api.helpers.{CommonHelper, MentorHelper}
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.{UserEduService, EducationInstService, UsersService, MentorsService}
import tables.Tables.MentorsRow

import scala.collection.immutable.HashMap
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class MentorApiController extends Controller {
  val secretKey = StaticValues.API_SECRET

  def mentors = new MentorsService

  def usersService = new UsersService

  def userEduService = new UserEduService

  def educationInstService = new EducationInstService

  //회원가입 요청
  def singUp(key: Option[String]) = Action { request =>
    Logger.info("[API Mentor SingUp] request")
    if (key.equals(secretKey)) {
      try {
        //val bodyJson = request.body.asJson.get
        println(request.body.toString)
        var bodyJson: JsValue = null
        request.body.asFormUrlEncoded.map { jsonmap =>
          val jsonList = jsonmap.get("jsonbody").get.toList
          bodyJson = Json.parse(jsonList.head)
          println(bodyJson)
        }
        if (bodyJson == null) {
          request.body.asMultipartFormData.map { file =>
            bodyJson = Json.parse(file.dataParts.get("jsonbody").get.head)
          }
        }
        //println(bodyJson.toString())
        if (CommonHelper.emailIsDuplicated((bodyJson \ "email").as[String])) {
          Logger.info("[API Mentor SingUp] duplicated email")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "duplicated email")))
        } else {

          var newMentor: MentorsRow = new MentorsRow(0, None, None, None, None, None, None, None, Option(0), Option(0), Option(0), Option(false), Option((bodyJson \ "nickname").as[String]), Option((bodyJson \ "year").as[Int]))
          val userId = CommonHelper.createUser(bodyJson, StaticValues.USER_TYPE_MENTOR)
          if (userId != 0) {
            val userEdu = Await.result(userEduService.findByUserId(userId), Duration.Inf).get
            val university = Await.result(educationInstService.findById(userEdu.eduInstId), Duration.Inf).get
            val universityName = university.name
            var image: String = null
            request.body.asMultipartFormData.map { file =>
              file.file("card_image").map(image =>
                CommonHelper.imageUpload(image, userId, StaticValues.PROFILE_IMAGE_UPLOAD_PATH)
              ).map(imageUrl => image = imageUrl)
            }
            newMentor = newMentor.copy(id = userId, university = universityName, cardImage = Option(image))
            val insertedMentor = MentorHelper.insert(newMentor)
            if (insertedMentor.isDefined) {
              val user = Await.result(usersService.findById(userId), Duration.Inf)
              var result: Map[String, String] = new HashMap[String, String]
              result += ("result" -> "success")
              result += ("message" -> "signUp success")
              result += ("user_id" -> user.get.id.toString)
              result += ("secret" -> user.get.secret.get)
              Logger.info("[API Mentor SingUp] success")
              Ok(Json.toJson(result))
//              Ok(Json.toJson(Map("result" -> "success", "message" -> "signUp success")))
            } else {
              CommonHelper.deleteUser(userId)
              throw new Exception
            }
          } else {
            throw new Exception
          }
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Mentor SingUp] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Mentor SingUp] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //프로필 정보 요청
  def profile(key: Option[String]) = Action { request =>
    Logger.info("[API Mentor profile] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val userRow = user.get
          val mentorRow = MentorHelper.findById(userRow.id).get
          var result: Map[String, String] = new HashMap[String, String]
          result += ("result" -> "success")
          result += ("university" -> mentorRow.university.getOrElse(""))
          result += ("major" -> mentorRow.major.getOrElse(""))
          result += ("introduce" -> mentorRow.introduce.getOrElse(""))
          result += ("nickname" -> mentorRow.nickname.getOrElse(""))
          result += ("gender" -> userRow.gender.getOrElse(""))
          result += ("name" -> userRow.name.getOrElse(""))
          result += ("email" -> userRow.email.getOrElse(""))
          result += ("profile_image" -> userRow.profileImage.getOrElse(""))
          result += ("phone" -> userRow.phone.getOrElse(""))
          result += ("year" -> mentorRow.year.getOrElse(0).toString)
          result += ("subjects" -> MentorHelper.getSubjectsForProfile(mentorRow.id))

          result += ("push" -> userRow.push.get.toString)
          Logger.info("[API Mentor profile] success")
          Ok(Json.toJson(result))
        } else {
          Logger.info("[API Mentor profile] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          Logger.info("[API Mentor profile] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Mentor profile] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //프로필 수정 요청
  def profileEdit(key: Option[String]) = Action { request =>
    Logger.info("[API Mentor profile edit] request")
    if (key.equals(secretKey)) {
      try {
        println(request.body.toString)
        var bodyJson: JsValue = null
        request.body.asFormUrlEncoded.map { jsonmap =>
          val jsonList = jsonmap.get("jsonbody").get.toList
          bodyJson = Json.parse(jsonList.head)
          println(bodyJson)
        }
        if(bodyJson == null){
          request.body.asMultipartFormData.map { file =>
            bodyJson = Json.parse(file.dataParts.get("jsonbody").get.head)
          }
        }
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          var image: String = (bodyJson \ "profile_image").as[String]
          request.body.asMultipartFormData.map { file =>
            file.file("profile_image").map(image =>
              CommonHelper.imageUpload(image, (bodyJson \ "user_id").as[String].toInt, StaticValues.PROFILE_IMAGE_UPLOAD_PATH)
            ).map(imageUrl => image = imageUrl)
          }
          MentorHelper.updateProfile(bodyJson, image)
          Logger.info("[API Mentor profile edit] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "profile edit success")))
        } else {
          Logger.info("[API Mentor profile edit] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Mentor profile edit] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Mentor profile edit] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //멘토 정보 요청
  def info(key: Option[String]) = Action { request =>
    Logger.info("[API Mentor info] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val userRow = user.get
          val mentorRow = MentorHelper.findById(userRow.id).get
          var result: Map[String, String] = new HashMap[String, String]
          result += ("result" -> "success")
          result += ("name" -> userRow.name.getOrElse(""))
          result += ("profile_image" -> userRow.profileImage.getOrElse(""))
          result += ("university" -> mentorRow.university.getOrElse(""))
          result += ("introduce" -> mentorRow.introduce.getOrElse(""))
          result += ("point" -> mentorRow.point.getOrElse("").toString)
          result += ("num_of_answer" -> mentorRow.numOfAnswer.getOrElse("").toString)
          result += ("grade" -> mentorRow.grade.getOrElse("").toString)
          Logger.info("[API Mentor info] success")
          Ok(Json.toJson(result))
        } else {
          Logger.info("[API Mentor info] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          Logger.info("[API Mentor info] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Mentor info] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //승인여부 확인 요청
  def authCheck(key: Option[String]) = Action { request =>
    Logger.info("[API Mentor auth check] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          Logger.info("[API Mentor auth check] success")
          Ok(Json.toJson(Map("result" -> "success", "is_authenticated" -> MentorHelper.isAuthenticated(user.get.id).toString)))
        } else {
          Logger.info("[API Mentor profile edit] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          Logger.info("[API Mentor auth check] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Mentor auth check] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //추가된 학생 정보 요청, 초기기획에 포함된 내용
  def getStudents(key: Option[String]) = Action { request =>
    Logger.info("[API Mentor get students] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        CommonHelper.secretIsValid(bodyJson) match {
          case Some(user) => {
            val students = MentorHelper.getStudents(bodyJson)
            implicit val formats = Serialization.formats(NoTypeHints)
            var result: Map[String, String] = new HashMap[String, String]
            result += ("result" -> "success")
            result += ("student_list" -> write(students))
            result += ("num_of_student" -> students.length.toString)
            Logger.info("[API Mentor get students] success")
            Ok(Json.toJson(result))
          }
          case _ => {
            Logger.info("[API Mentor get students] user id and secret key is not matched")
            Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
          }
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Mentor get students] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Teacher get students] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //답변 이미지 업로드 요청
  def answerImageUpload(key: Option[String], id: Option[Int], secret: Option[String]) = Action(parse.multipartFormData) { request =>
    Logger.info("[API Mentor answer image upload] request")
    if (key.equals(secretKey) && id.isDefined && secret.isDefined) {
      try {
        val user = CommonHelper.secretIsValid(id.get, secret.get)
        if (user.isDefined) {
          var result: Map[String, String] = new HashMap[String, String]
          request.body.file("image").map(file =>
            CommonHelper.imageUpload(file, id.get, StaticValues.POSTING_IMAGE_UPLOAD_PATH)
          ).map(imageUrl => result += ("image_url" -> imageUrl))
          result += ("result" -> "success")
          result += ("message" -> "answer image upload success")
          Logger.info("[API Mentor answer image upload] success")
          Ok(Json.toJson(result))
        } else {
          Logger.info("[API Mentor answer image upload] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Mentor answer image upload] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Mentor answer image upload] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //답변 저장
  def answerSave(key: Option[String]) = Action { request =>
    Logger.info("[API Mentor answer save] request")
    if (key.equals(secretKey)) {
      try {
        println(request.body.toString)
        var bodyJson: JsValue = null
        request.body.asFormUrlEncoded.map { jsonmap =>
          val jsonList = jsonmap.get("jsonbody").get.toList
          bodyJson = Json.parse(jsonList.head)
          println(bodyJson)
        }
        if(bodyJson == null){
          request.body.asMultipartFormData.map { file =>
            bodyJson = Json.parse(file.dataParts.get("jsonbody").get.head)
          }
        }
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          var image: String = ""
          request.body.asMultipartFormData.map { file =>
            file.file("profile_image").map(image =>
              CommonHelper.imageUpload(image, (bodyJson \ "user_id").as[String].toInt, StaticValues.POSTING_IMAGE_UPLOAD_PATH)
            ).map(imageUrl => image = imageUrl)
          }
          MentorHelper.answerSave(bodyJson, image)
          Logger.info("[API Mentor answer save] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "answer save success")))
        } else {
          Logger.info("[API Mentor answer save] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Mentor answer save] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Mentor answer save] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //멘토 답변 내역 조회 요청
  def postingList(key: Option[String]) = Action { request =>
    Logger.info("[API Mentor posting list] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        implicit val formats = Serialization.formats(NoTypeHints)
        val postings = MentorHelper.getPostings(bodyJson)

        if (postings.size == 0) {
          Logger.info("[API Mentor posting list] page is not exist")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "page is not exist")))
        } else {
          var result: Map[String, String] = new HashMap[String, String]
          result += ("result" -> "success")
          result += ("posting_list" -> write(postings))
          result += ("num_of_posting" -> postings.size.toString)
          Logger.info("[API Mentor posting list] success")
          Ok(Json.toJson(result))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Mentor posting list] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Mentor posting list] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //포인트 적립 내역 조회 요청
  def saveHistory(key: Option[String]) = Action { request =>
    Logger.info("[API Mentor get save history] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val message = MentorHelper.getSaveHistory(bodyJson)
          Logger.info("[API Mentor get save history] success")
          Ok(Json.toJson(message))
        } else {
          Logger.info("[API Mentor get save history] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Mentor get save history] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Mentor get save history] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //포인트 환급 내역 조회 요청
  def refundHistory(key: Option[String]) = Action{ request =>
    Logger.info("[API Mentor get refund history] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val message = MentorHelper.getRefundHistory(bodyJson)
          Logger.info("[API Mentor get refund history] success")
          Ok(Json.toJson(message))
        } else {
          Logger.info("[API Mentor get refund history] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Mentor get refund history] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Mentor get refund history] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //포인트 환급 요청
  def registerRefund(key: Option[String]) = Action { request =>
    Logger.info("[API Mentor refund] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val message = MentorHelper.registerRefund(bodyJson)
          Ok(Json.toJson(message))
        } else {
          Logger.info("[API Mentor refund] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Mentor refund] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Mentor get refund hostory] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }
}