package controllers.api

import controllers.api.helpers._
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.{SelectedTextbookService, UsersService, StudentsService}
import tables.Tables.{SelectedTextbookRow, DDayRow}

import scala.collection.immutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class StudentApiController extends Controller {
  val secretKey = StaticValues.API_SECRET

  def students = new StudentsService

  //회원가입 요청
  def singUp(key: Option[String]) = Action { request =>
    Logger.info("[API Student SingUp] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        //println(bodyJson.toString())
        if (CommonHelper.emailIsDuplicated((bodyJson \ "email").as[String])) {
          Logger.info("[API Student SingUp] duplicated email")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "duplicated email")))
        } else {
          val userId = StudentHelper.signUp(bodyJson)
          def users = new UsersService
          val user = Await.result(users.findById(userId), Duration.Inf)
          var result: Map[String, String] = new HashMap[String, String]
          result += ("result" -> "success")
          result += ("message" -> "signUp success")
          result += ("user_id" -> user.get.id.toString)
          result += ("secret" -> user.get.secret.get)
          Logger.info("[API Student SingUp] success")
          Ok(Json.toJson(result))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student SingUp] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student SingUp] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //프로필 정보 요청
  def profile(key: Option[String]) = Action { request =>
    Logger.info("[API Student profile] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val userRow = user.get
          val studentRow = StudentHelper.findById(userRow.id).get
          var result: Map[String, String] = new HashMap[String, String]
          result += ("result" -> "success")
          result += ("name" -> userRow.name.getOrElse(""))
          result += ("email" -> userRow.email.getOrElse(""))
          result += ("gender" -> userRow.gender.getOrElse(""))
          result += ("profile_image" -> userRow.profileImage.getOrElse(""))
          result += ("introduce" -> studentRow.introduce.getOrElse(""))
          result += ("target_university" -> studentRow.targetUniversity.getOrElse(""))
          result += ("target_department" -> studentRow.targetDepartment.getOrElse(""))
          result += ("phone" -> userRow.phone.getOrElse(""))
          result += ("year" -> studentRow.year.getOrElse("").toString)
          result += ("nickname" -> studentRow.nickname.getOrElse(""))
          result += ("has_new_answer" -> StudentHelper.hasNewAnswer(userRow.id).toString)
          result += ("has_new_message" -> CommonHelper.hasNewMessage(userRow.id).toString)
          result += ("push" -> userRow.push.get.toString)
          Logger.info("[API Student profile] success")
          Ok(Json.toJson(result))
        } else {
          Logger.info("[API Student profile] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          Logger.info("[API Student profile] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student profile] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //프로필 수정 요청
  def profileEdit(key: Option[String]) = Action { request =>
    Logger.info("[API Student profile edit] request")
    if (key.equals(secretKey)) {
      try {
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
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          var image: String = (bodyJson \ "profile_image").as[String]
          request.body.asMultipartFormData.map { file =>
            file.file("profile_image").map(image =>
              CommonHelper.imageUpload(image, (bodyJson \ "user_id").as[String].toInt, StaticValues.PROFILE_IMAGE_UPLOAD_PATH)
            ).map(imageUrl => image = imageUrl)
          }
          StudentHelper.updateProfile(bodyJson, image)
          Logger.info("[API Student profile edit] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "profile edit success")))
        } else {
          Logger.info("[API Student profile edit] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student profile edit] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student profile edit] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //멘토만나기 저장 요청
  def mentoringSave(key: Option[String]) = Action { request =>
    Logger.info("[API Student mentoring save] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          StudentHelper.mentoringSave(bodyJson)
          Logger.info("[API Student mentoring save] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "mentoring save success")))
        } else {
          Logger.info("[API Student mentoring save] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student mentoring save] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student mentoring save] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //멘토만나기 수정 요청
  def mentoringUpdate(key: Option[String]) = Action { request =>
    Logger.info("[API Student mentoring update] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          StudentHelper.mentoringUpdate(bodyJson)
          Logger.info("[API Student mentoring update] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "mentoring update success")))
        } else {
          Logger.info("[API Student mentoring update] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student mentoring update] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student mentoring update] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //멘토만나기 세부정보 조회 요청
  def mentoringGet(key: Option[String]) = Action { request =>
    Logger.info("[API Student mentoring get] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val message = StudentHelper.mentoringGet(bodyJson)
          Logger.info("[API Student mentoring get] success")
          Ok(Json.toJson(message))
        } else {
          Logger.info("[API Student mentoring get] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student mentoring get] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student mentoring get] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //풀어주세요 저장 요청
  def clinicSave(key: Option[String]) = Action { request =>
    Logger.info("[API Student clinic save] request")
    if (key.equals(secretKey)) {
      try {
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
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          var imageList = new ListBuffer[String]()
          request.body.asMultipartFormData.map { file =>
            file.files.map(image =>
              CommonHelper.imageUpload(image, (bodyJson \ "user_id").as[String].toInt, StaticValues.POSTING_IMAGE_UPLOAD_PATH)
            ).map(imageUrl => imageList += imageUrl)
          }
          StudentHelper.clinicSave(bodyJson, imageList.toSeq)
          Logger.info("[API Student clinic save] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "profile edit success")))
        } else {
          Logger.info("[API Student clinic save] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student clinic save] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student clinic save] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //풀어주세요 세부정보 조회 요청
  def clinicGet(key: Option[String]) = Action { request =>
    Logger.info("[API Student clinic get] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val message = StudentHelper.clinicGet(bodyJson)
          Logger.info("[API Student clinic get] success")
          Ok(Json.toJson(message))
        } else {
          Logger.info("[API Student clinic get] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student clinic get] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student clinic get] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //풀어주세요 업데이트 요청
  def clinicUpdate(key: Option[String]) = Action { request =>
    Logger.info("[API Student clinic update] request")
    if (key.equals(secretKey)) {
      try {
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
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          var imageList = new ListBuffer[String]()
          request.body.asMultipartFormData.map { file =>
            file.files.map(image =>
              CommonHelper.imageUpload(image, (bodyJson \ "user_id").as[String].toInt, StaticValues.POSTING_IMAGE_UPLOAD_PATH)
            ).map(imageUrl => imageList += imageUrl)
          }
          StudentHelper.clinicUpdate(bodyJson, imageList.toSeq)
          Logger.info("[API Student clinic update] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "clinic update success")))
        } else {
          Logger.info("[API Student clinic update] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student clinic update] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student clinic update] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //질문내역 리스트 조회 요청
  def postingList(key: Option[String]) = Action { request =>
    Logger.info("[API Student posting list] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          implicit val formats = Serialization.formats(NoTypeHints)
          val postings = StudentHelper.getPostings(bodyJson)
          if (postings.size == 0) {
            Logger.info("[API Student posting list] posting is not exist")
            Ok(Json.toJson(Map("result" -> "fail", "message" -> "posting is not exist")))
          } else {
            var result: Map[String, String] = new HashMap[String, String]
            result += ("result" -> "success")
            result += ("posting_list" -> write(postings))
            result += ("num_of_posting" -> postings.size.toString)
            Logger.info("[API Student posting list] success")
            Ok(Json.toJson(result))
          }
        } else {
          Logger.info("[API Student posting list] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student posting list] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student posting list] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //답변 채택 요청
  def answerChoice(key: Option[String]) = Action { request =>
    Logger.info("[API Student answer choice] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          StudentHelper.answerChoice(bodyJson)
          Logger.info("[API Student answer choice] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "answer choice success")))
        } else {
          Logger.info("[API Student answer choice] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          Logger.info("[API Student answer choice] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student answer choice] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //답변 점수 저장 요청
  def answerEvaluation(key: Option[String]) = Action { request =>
    Logger.info("[API Student answer evaluation] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          StudentHelper.answerEvaluation(bodyJson)
          Logger.info("[API Student answer evaluation] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "answer evaluation success")))
        } else {
          Logger.info("[API Student answer evaluation] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          Logger.info("[API Student answer evaluation] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student answer evaluation] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //d-day 저장 요청
  def dDaySave(key: Option[String]) = Action { request =>
    Logger.info("[API Student d-day save] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          StudentHelper.dDaySave(bodyJson)
          Logger.info("[API Student d-day save] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "d-day save success")))
        } else {
          Logger.info("[API Student d-day save] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          Logger.info("[API Student d-day save] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student d-day save] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //d-day 수정 요청
  def dDayEdit(key: Option[String]) = Action { request =>
    Logger.info("[API Student d-day edit] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          StudentHelper.dDayEdit(bodyJson)
          Logger.info("[API Student d-day edit] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "d-day edit success")))
        } else {
          Logger.info("[API Student d-day edit] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          Logger.info("[API Student d-day edit] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student d-day edit] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //d-day 삭제 요청
  def dDayDelete(key: Option[String]) = Action { request =>
    Logger.info("[API Student d-day delete] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          StudentHelper.dDayDelete(bodyJson)
          Logger.info("[API Student d-day delete] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "d-day delete success")))
        } else {
          Logger.info("[API Student d-day delete] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          Logger.info("[API Student d-day delete] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student d-day delete] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //d-day 설정 요청
  def dDayChoice(key: Option[String]) = Action { request =>
    Logger.info("[API Student d-day choice] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          StudentHelper.dDayChoice(bodyJson)
          Logger.info("[API Student d-day choice] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "d-day choice success")))
        } else {
          Logger.info("[API Student d-day choice] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          Logger.info("[API Student d-day choice] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student d-day choice] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //d-day 리스트 조회 요청
  def dDayList(key: Option[String]) = Action { request =>
    Logger.info("[API Student d-day list] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          implicit val formats = Serialization.formats(NoTypeHints)
          val dDayList: Seq[DDayRowForList] = StudentHelper.dDayList(bodyJson)
          if (dDayList.size == 0) {
            Logger.info("[API Student d-day list] d-day is not exist")
            Ok(Json.toJson(Map("result" -> "fail", "message" -> "d-day is not exist")))
          } else {
            var result: Map[String, String] = new HashMap[String, String]
            result += ("result" -> "success")
            result += ("dday_list" -> write(dDayList))
            result += ("num_of_dday" -> dDayList.size.toString)
            Logger.info("[API Student d-day list] success")
            Ok(Json.toJson(result))
          }
        } else {
          Logger.info("[API Student d-day list] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student d-day list] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student d-day list] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //선택된 d-day 정보 요청
  def dDay(key: Option[String]) = Action { request =>
    Logger.info("[API Student d-day] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          implicit val formats = Serialization.formats(NoTypeHints)
          val dDay: Option[DDayRow] = StudentHelper.dDay(bodyJson)
          if (dDay.isDefined) {
            var result: Map[String, String] = new HashMap[String, String]
            result += ("result" -> "success")
            result += ("title" -> dDay.get.title.get)
            result += ("date" -> dDay.get.date.get)
            Logger.info("[API Student d-day] success")
            Ok(Json.toJson(result))
          } else {
            Logger.info("[API Student d-day] d-day is not exist")
            Ok(Json.toJson(Map("result" -> "fail", "message" -> "d-day is not exist")))
          }
        } else {
          Logger.info("[API Student d-day] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student d-day] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student d-day] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //활동 저장 요청
  def activitySave(key: Option[String]) = Action { request =>
    Logger.info("[API Student activity save] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          (bodyJson \ "activity_type").as[Int] match {
            case 1 => StudentHelper.studySave(bodyJson)
            case 2 => StudentHelper.activitySave(bodyJson)
            case 3 => StudentHelper.customActivitySave(bodyJson)
            case default => throw new Exception
          }
          Logger.info("[API Student activity save] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "activity save success")))
        } else {
          Logger.info("[API Student activity save] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student activity save] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student activity save] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //활동 시간 업데이트 요청
  def activityTimeUpdate(key: Option[String]) = Action { request =>
    Logger.info("[API Student activity update] request")
    if (key.equals(secretKey)) {
      try {
        println(request.body.toString)
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          (bodyJson \ "activity_type").as[Int] match {
            case 1 => StudentHelper.studyTimeUpdate(bodyJson)
            case 2 => StudentHelper.activityTimeUpdate(bodyJson)
            case 3 => StudentHelper.customActivityTimeUpdate(bodyJson)
            case default => throw new Exception
          }
          Logger.info("[API Student activity update] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "activity update success")))
        } else {
          Logger.info("[API Student activity update] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student activity update] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student activity update] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //활동 리스트 조회 요청
  def activityList(key: Option[String]) = Action { request =>
    Logger.info("[API Student activity list] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          implicit val formats = Serialization.formats(NoTypeHints)
          val studyList = StudentHelper.activityList(bodyJson)
          if (studyList.size == 0) {
            Logger.info("[API Student activity list] activity is not exist")
            Ok(Json.toJson(Map("result" -> "fail", "message" -> "study is not exist")))
          } else {
            var result: Map[String, String] = new HashMap[String, String]
            result += ("result" -> "success")
            result += ("activity_list" -> write(studyList))
            result += ("num_of_study" -> studyList.size.toString)
            Logger.info("[API Student activity list] success")
            Ok(Json.toJson(result))
          }
        } else {
          Logger.info("[API Student activity list] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student activity list] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student activity list] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //활동 삭제 요청
  def activityDelete(key: Option[String]) = Action { request =>
    Logger.info("[API Student activity delete] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          (bodyJson \ "activity_type").as[Int] match {
            case 1 => StudentHelper.studyDelete(bodyJson)
            case 2 => StudentHelper.activityDelete(bodyJson)
            case 3 => StudentHelper.customActivityDelete(bodyJson)
            case default => throw new Exception
          }
          Logger.info("[API Student activity delete] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "activity delete success")))
        } else {
          Logger.info("[API Student activity delete] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student activity delete] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student activity delete] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //활동 시간 저장 요청
  def activityTimeSave(key: Option[String]) = Action { request =>
    Logger.info("[API Student activity time save] request")
    if (key.equals(secretKey)) {
      try {
        println(request.body.toString)
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          (bodyJson \ "activity_type").as[Int] match {
            case 1 => StudentHelper.studyTimeSave(bodyJson)
            case 2 => StudentHelper.activityTimeSave(bodyJson)
            case 3 => StudentHelper.activityTimeSave(bodyJson)
            case default => throw new Exception
          }
          Logger.info("[API Student activity time save] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "activity time save success")))
        } else {
          Logger.info("[API Student activity time save] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student activity time save] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student activity time save] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //내신 입력 요청
  def recordSchool(key: Option[String]) = Action { request =>
    Logger.info("[API Student record school] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          StudentHelper.recordSchool(bodyJson)
          Logger.info("[API Student record school] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "save school record success")))
        } else {
          Logger.info("[API Student record school] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student record school] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student record school] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //목표 학교 설정 요청
  def universitySave(key: Option[String]) = Action { request =>
    Logger.info("[API Student target university save] request")
    if (key.equals(secretKey)) {
      try {
        println(request.body.toString)
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          StudentHelper.universitySave(bodyJson)
          Logger.info("[API Student target university save] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "target university save success")))
        } else {
          Logger.info("[API Student target university save] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student target university save] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student target university save] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //입력 가능 모의고사 리스트 요청
  def examRecordList(key: Option[String]) = Action { request =>
    Logger.info("[API Student exam record list] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          implicit val formats = Serialization.formats(NoTypeHints)
          val examList = StudentHelper.examRecordList(bodyJson)
          if (examList.size == 0) {
            Logger.info("[API Student exam record list] active exam is not exist")
            Ok(Json.toJson(Map("result" -> "fail", "message" -> "active exam is not exist")))
          } else {
            var result: Map[String, String] = new HashMap[String, String]
            result += ("result" -> "success")
            result += ("exam_list" -> write(examList))
            result += ("num_of_exam" -> examList.size.toString)
            Logger.info("[API Student exam record list] success")
            Ok(Json.toJson(result))
          }
        } else {
          Logger.info("[API Student exam record list] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student exam record list] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student exam record list] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //모의고사 성적 입력 요청
  def recordExam(key: Option[String]) = Action { request =>
    Logger.info("[API Student record Exam] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          StudentHelper.recordExam(bodyJson)
          Logger.info("[API Student record Exam] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "save Exam record success")))
        } else {
          Logger.info("[API Student record Exam] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student record Exam] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student record Exam] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //추천 대학 리스트 요청
  def recommendUniversityList(key: Option[String]) = Action { request =>
    Logger.info("[API Student recommend university list] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          implicit val formats = Serialization.formats(NoTypeHints)
          val recommendUniversityList = StudentHelper.recommendUniversityList(bodyJson)
          if (recommendUniversityList.size == 0) {
            Logger.info("[API Student recommend university list] recommend university is not exist")
            Ok(Json.toJson(Map("result" -> "fail", "message" -> "recommend university is not exist")))
          } else {
            var result: Map[String, String] = new HashMap[String, String]
            result += ("result" -> "success")
            result += ("university_list" -> write(recommendUniversityList))
            result += ("num_of_university" -> recommendUniversityList.size.toString)
            Logger.info("[API Student recommend university list] success")
            Ok(Json.toJson(result))
          }
        } else {
          Logger.info("[API Student recommend university list] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student recommend university list] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student recommend university list] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //사용자 정의 활동 타입 저장 요청
  def customActivityTypeAdd(key: Option[String]) = Action { request =>
    Logger.info("[API Student custom activity type add] request")
    if (key.equals(secretKey)) {
      try {
        println(request.body.toString)
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          StudentHelper.customActivityTypeAdd(bodyJson)
          Logger.info("[API Student custom activity type add] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "custom activity type add success")))
        } else {
          Logger.info("[API Student custom activity type add] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student custom activity type add] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student custom activity type add] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //사용자 정의 활동 타입 삭제 요청
  def customActivityTypeDelete(key: Option[String]) = Action { request =>
    Logger.info("[API Student custom activity type delete] request")
    if (key.equals(secretKey)) {
      try {
        println(request.body.toString)
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          StudentHelper.customActivityTypeDelete(bodyJson)
          Logger.info("[API Student custom activity type delete] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "custom activity type delete success")))
        } else {
          Logger.info("[API Student custom activity type delete] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student custom activity type delete] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student custom activity type delete] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //사용자 정의 활동 타입 리스트 요청
  def customActivityTypeList(key: Option[String]) = Action { request =>
    Logger.info("[API Student custom activity list] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          implicit val formats = Serialization.formats(NoTypeHints)
          val customActivityList = StudentHelper.customActivityTypeList(bodyJson)
          if (customActivityList.size == 0) {
            Logger.info("[API Student custom activity list] custom activity is not exist")
            Ok(Json.toJson(Map("result" -> "fail", "message" -> "custom activity is not exist")))
          } else {
            var result: Map[String, String] = new HashMap[String, String]
            result += ("result" -> "success")
            result += ("custom_type_list" -> write(customActivityList))
            result += ("num_of_type" -> customActivityList.size.toString)
            Logger.info("[API Student custom activity list] success")
            Ok(Json.toJson(result))
          }
        } else {
          Logger.info("[API Student custom activity list] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student custom activity list] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student custom activity list] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //선택 교재 추가 요청
  def selectedTextbookAdd(key: Option[String]) = Action { request =>
    Logger.info("[API Student selected textbook add] request")
    if (key.equals(secretKey)) {
      try {
        println(request.body.toString)
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          StudentHelper.selectedTextbookAdd(bodyJson)
          Logger.info("[API Student selected textbook add] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "selected textbook add success")))
        } else {
          Logger.info("[API Student selected textbook add] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student selected textbook add] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student selected textbook add] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //선택 교재 삭제 요청
  def selectedTextbookDelete(key: Option[String]) = Action { request =>
    Logger.info("[API Student selected textbook delete] request")
    if (key.equals(secretKey)) {
      try {
        println(request.body.toString)
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          StudentHelper.selectedTextbookDelete(bodyJson)
          Logger.info("[API Student selected textbook delete] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "selected textbook delete success")))
        } else {
          Logger.info("[API Student selected textbook delete] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student selected textbook delete] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student selected textbook delete] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //선택 교재 리스트 요청
  def selectedTextbookList(key: Option[String]) = Action { request =>
    Logger.info("[API Student selected textbook list] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          implicit val formats = Serialization.formats(NoTypeHints)
          val selectedTextbookList = StudentHelper.selectedTextbookList(bodyJson)
          if (selectedTextbookList.size == 0) {
            Logger.info("[API Student selected textbook list] selected textbook is not exist")
            Ok(Json.toJson(Map("result" -> "fail", "message" -> "selected textbook is not exist")))
          } else {
            var result: Map[String, String] = new HashMap[String, String]
            result += ("result" -> "success")
            result += ("textbook_list" -> write(selectedTextbookList))
            result += ("num_of_textbook" -> selectedTextbookList.size.toString)
            Logger.info("[API Student selected textbook list] success")
            Ok(Json.toJson(result))
          }
        } else {
          Logger.info("[API Student selected textbook list] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student selected textbook list] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student selected textbook list] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //교재 리스트 요청
  def textbookList(key: Option[String]) = Action { request =>
    Logger.info("[API Student textbook list] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          implicit val formats = Serialization.formats(NoTypeHints)
          val textbookList = StudentHelper.textbookList(bodyJson)
          if (textbookList.size == 0) {
            Logger.info("[API Student textbook list] textbook is not exist")
            Ok(Json.toJson(Map("result" -> "fail", "message" -> "selected textbook is not exist")))
          } else {
            var result: Map[String, String] = new HashMap[String, String]
            result += ("result" -> "success")
            result += ("textbook_list" -> write(textbookList))
            result += ("num_of_textbook" -> textbookList.size.toString)
            Logger.info("[API Student textbook list] success")
            Ok(Json.toJson(result))
          }
        } else {
          Logger.info("[API Student textbook list] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student textbook list] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student textbook list] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //목표시간 저장 요청
  def studyGoalSave(key: Option[String]) = Action { request =>
    Logger.info("[API Student study goal save] request")
    if (key.equals(secretKey)) {
      try {
        println(request.body.toString)
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          StudentHelper.studyGoalSave(bodyJson)
          Logger.info("[API Student study goal save] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "study goal save success")))
        } else {
          Logger.info("[API Student study goal save] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student study goal save] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student study goal save] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //목표시간 조회 요청
  def getGoalTime(key: Option[String]) = Action { request =>
    Logger.info("[API Student get goal time] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val messages = StudentHelper.getStudyGoal(bodyJson)
          Logger.info("[API Student get goal time] success")
          Ok(Json.toJson(messages))
        } else {
          Logger.info("[API Student get goal time] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student get goal time] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student get goal time] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //풀어주세요 교재 리스트 요청
  def textbookListForPosting(key: Option[String]) = Action { request =>
    Logger.info("[API Student textbook list] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          implicit val formats = Serialization.formats(NoTypeHints)
          val textbookList = StudentHelper.textbookListForPosting(bodyJson)
          if (textbookList.size == 0) {
            Logger.info("[API Student textbook list] textbook is not exist")
            Ok(Json.toJson(Map("result" -> "fail", "message" -> "selected textbook is not exist")))
          } else {
            var result: Map[String, String] = new HashMap[String, String]
            result += ("result" -> "success")
            result += ("textbook_list" -> write(textbookList))
            result += ("num_of_textbook" -> textbookList.size.toString)
            Logger.info("[API Student textbook list] success")
            Ok(Json.toJson(result))
          }
        } else {
          Logger.info("[API Student textbook list] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student textbook list] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student textbook list] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }
  def selectedTextbookService = new SelectedTextbookService

  //사용자 정의 교재 추가 요청
  def customTextbookAdd(key: Option[String]) = Action { request =>
    Logger.info("[API Student custom textbook add] request")
    if (key.equals(secretKey)) {
      try {
        println(request.body.toString)
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val customTextbookId = StudentHelper.customTextbookAdd(bodyJson)
          if((bodyJson \ "is_study").as[Boolean]){
            Await.result(selectedTextbookService.insert(SelectedTextbookRow(id = 0, studentId = user.get.id, studyType = (bodyJson \ "subject_id").as[Int], textbookId = customTextbookId, isCustom = Option(true))), Duration.Inf)
          }
          Logger.info("[API Student custom textbook add] success")
          Ok(Json.toJson(Map("result" -> "success", "custom_textbook_id" -> customTextbookId.toString)))
        } else {
          Logger.info("[API Student custom textbook add] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Student custom textbook add] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student custom textbook add] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //채택 변경 요청
  def answerChoiceChange(key: Option[String]) = Action { request =>
    Logger.info("[API Student answer choice change] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          StudentHelper.answerChoiceChange(bodyJson)
          Logger.info("[API Student answer choice change] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "answer choice change success")))
        } else {
          Logger.info("[API Student answer choice change] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          Logger.info("[API Student answer choice change] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Student answer choice change] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }
}