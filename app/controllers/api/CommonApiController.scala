package controllers.api

import akka.actor.{ActorRef, Props, ActorSystem}
import common.{SingletonSchedule, SchedulingActor}
import controllers.api.helpers.{StudentHelper, CommonHelper}
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.UsersService

import scala.collection.immutable.HashMap
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class CommonApiController extends Controller {

  val secretKey = StaticValues.API_SECRET

  //로그인 요청
  def login(key: Option[String]) = Action { request =>
    Logger.info("[API Common Login] request :" + key)
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        //println(bodyJson.toString())
        Logger.info("[API Common Login] start authenticate")
        def users = new UsersService
        users.authenticate((bodyJson \ "email").as[String], (bodyJson \ "password").as[String]) match {
          case Some(user) => {
            Logger.info("[API Common Login] success")
            Ok(Json.toJson(Map("result" -> "success", "message" -> "login success", "user_id" -> user.id.toString, "type_id" -> user.typeId.toString, "secret" -> user.secret.getOrElse("secret"))))
          }
          case None => {
            Logger.info("[API Common Login] fail")
            Ok(Json.toJson(Map("result" -> "fail", "message" -> "check account info")))
          }
        }
      } catch {
        case e: Exception =>
          Logger.info("[API Common Login] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common Login] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //게시물 리스트 조회 요청
  def postingList(key: Option[String]) = Action { request =>
    Logger.info("[API Common posting list] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        println(bodyJson.toString())
        implicit val formats = Serialization.formats(NoTypeHints)
        val postings = CommonHelper.getPostings(bodyJson)
        val postingLastPage = CommonHelper.getPostingLastPageNumber(bodyJson)
        if (postings.size == 0) {
          Logger.info("[API Common posting list] page is not exist")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "page is not exist")))
        } else {
          var result: Map[String, String] = new HashMap[String, String]
          result += ("result" -> "success")
          result += ("posting_list" -> write(postings))
          result += ("last_page_number" -> postingLastPage.toString)
          Logger.info("[API Common posting list] success :")
          Ok(Json.toJson(result))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common posting list] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common posting list] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //게시물 상세 내용 조회 요청
  def postingContents(key: Option[String]) = Action { request =>
    Logger.info("[API Common posting content] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        implicit val formats = Serialization.formats(NoTypeHints)
        var result: Map[String, String] = new HashMap[String, String]
        val posting = CommonHelper.getPosting(bodyJson)
        result += ("result" -> "success")
        result += ("contents" -> write(posting))
        result += ("num_of_answers" -> posting.answers.get.size.toString)
        Logger.info("[API Common posting content] success:" + posting.toString)
        Ok(Json.toJson(result))
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common posting content] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common posting content] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //댓글 좋아요 요청
  def replyLike(key: Option[String]) = Action { request =>
    Logger.info("[API Common posting like] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          CommonHelper.addReplyLike(bodyJson)
          Logger.info("[API Common posting like] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "reply like success")))
        } else {
          Logger.info("[API Common posting like] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          Logger.info("[API Common posting like] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common posting like] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //댓글 신고 요청
  def replyReport(key: Option[String]) = Action { request =>
    Logger.info("[API Common reply Report] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val message = CommonHelper.replyReport(bodyJson)
          Logger.info(s"[API Common reply Report] $message")
          Ok(Json.toJson(Map("result" -> "success", "message" -> message)))
        } else {
          Logger.info("[API Common reply Report] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common reply Report] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common reply Report] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //댓글 삭제 요청
  def replyDelete(key: Option[String]) = Action { request =>
    Logger.info("[API Common posting delete] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          CommonHelper.deleteReply(bodyJson)
          Logger.info("[API Common posting delete] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "reply delete success")))
        } else {
          Logger.info("[API Common posting delete] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common posting delete] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common posting delete] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //댓글 저장 요청
  def replySave(key: Option[String]) = Action { request =>
    Logger.info("[API Common reply save] request")
    if (key.equals(secretKey)) {
      try {
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
          var image: String = ""
          request.body.asMultipartFormData.map { file =>
            file.file("profile_image").map(image =>
              CommonHelper.imageUpload(image, (bodyJson \ "user_id").as[String].toInt, StaticValues.REPLY_IMAGE_UPLOAD_PATH)
            ).map(imageUrl => image = imageUrl)
          }
          CommonHelper.replySave(bodyJson, image)
          Logger.info("[API Student reply save] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "profile edit success")))
        } else {
          Logger.info("[API Student reply save] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common reply save] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common reply save] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //메세지 전송 요청
  def messageSend(key: Option[String]) = Action { request =>
    Logger.info("[API Common message send] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          println(bodyJson)
          CommonHelper.messageSend(bodyJson)
          Logger.info("[API Common message send] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "message send success")))
        } else {
          Logger.info("[API Common message send] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common message send] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common message send] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //메세지 리스트 요청
  def messageList(key: Option[String]) = Action { request =>
    Logger.info("[API Common message list] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val messages = CommonHelper.messageList(bodyJson)
          if (messages.size == 0) {
            Logger.info("[API Common message list] there are not messages")
            Ok(Json.toJson(Map("result" -> "success", "message" -> "there are not messages")))
          } else {
            implicit val formats = Serialization.formats(NoTypeHints)
            var result: Map[String, String] = new HashMap[String, String]
            result += ("result" -> "success")
            result += ("messages" -> write(messages))
            result += ("number_of_messages" -> messages.size.toString)
            Logger.info("[API Common message list] success")
            Ok(Json.toJson(result))
          }

        } else {
          Logger.info("[API Common message list] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common message list] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common message list] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //메세지 세부정보 조회 요청
  def getMessage(key: Option[String]) = Action { request =>
    Logger.info("[API Common message get] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val message = CommonHelper.getMessage(bodyJson)
          Logger.info("[API Common message get] success")
          Ok(Json.toJson(message))

        } else {
          Logger.info("[API Common message get] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common message get] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common message get] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //메세지 삭제 요청
  def messageDelete(key: Option[String]) = Action { request =>
    Logger.info("[API Common message delete] request")
    if (key.equals(secretKey)) {
      try {
        println(request.body.toString)
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          println(bodyJson)
          CommonHelper.messageDelete(bodyJson)
          Logger.info("[API Common message delete] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "message delete success")))
        } else {
          Logger.info("[API Common message send] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common message delete] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common message delete] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //이메일 중복 확인 요청
  def emailCheck(key: Option[String]) = Action { request =>
    Logger.info("[API Common mail check] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        Logger.info("[API Common mail check] success")
        Ok(Json.toJson(Map("result" -> "success", "is_duplicated" -> CommonHelper.emailIsDuplicated((bodyJson \ "email").as[String]).toString)))
      } catch {
        case e: Exception =>
          Logger.info("[API Common mail check] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common mail check] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //닉네임 중복 확인 요청
  def nicknameCheck(key: Option[String]) = Action { request =>
    Logger.info("[API Common nickname check] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        Logger.info("[API Common nickname check] success")
        Ok(Json.toJson(Map("result" -> "success", "is_duplicated" -> CommonHelper.nicknameCheck(bodyJson).toString)))
      } catch {
        case e: Exception =>
          Logger.info("[API Common nickname check] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common nickname check] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //성적 분석 데이터 요청
  def analysisGrade(key: Option[String]) = Action { request =>
    Logger.info("[API Common analysis grade] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val message = CommonHelper.getAnalysisData(bodyJson)
          Logger.info("[API Common analysis grade] success")
          Ok(Json.toJson(message))
        } else {
          Logger.info("[API Common analysis grade] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common analysis grade] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common analysis grade] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //비밀번호 변경 요청
  def passwordChange(key: Option[String]) = Action { request =>
    Logger.info("[API Common password change] request")
    if (key.equals(secretKey)) {
      try {
        println(request.body.toString)
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          if (CommonHelper.passwordCheck(bodyJson)) {
            CommonHelper.passwordChange(bodyJson)
            Logger.info("[API Common password change] success")
            Ok(Json.toJson(Map("result" -> "success", "message" -> "password change success")))
          } else {
            Logger.info("[API Common password change] password is incorrect")
            Ok(Json.toJson(Map("result" -> "fail", "message" -> "password is incorrect")))
          }
        } else {
          Logger.info("[API Common password change] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common password change] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common password change] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //핸드폰 번호 변경 요청
  def phoneChange(key: Option[String]) = Action { request =>
    Logger.info("[API Common phone change] request")
    if (key.equals(secretKey)) {
      try {
        println(request.body.toString)
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          CommonHelper.phoneChange(bodyJson)
          Logger.info("[API Common phone change] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "phone change success")))
        } else {
          Logger.info("[API Common phone change] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common phone change] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common phone change] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //학습분석 데이터 요청
  def analysisStudyAll(key: Option[String]) = Action { request =>
    Logger.info("[API Common analysis study all] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val messages = CommonHelper.analysisStudyAll(bodyJson)
          Logger.info("[API Common analysis study all] success")
          Ok(Json.toJson(messages))

        } else {
          Logger.info("[API Common analysis study all] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common analysis study all] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common analysis study all] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //하루일기 데이터 요청
  def analysisStudyDaily(key: Option[String]) = Action { request =>
    Logger.info("[API Common analysis study daily] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val messages = CommonHelper.analysisStudyDaily(bodyJson)
          Logger.info("[API Common analysis study daily] success")
          Ok(Json.toJson(messages))

        } else {
          Logger.info("[API Common analysis study daily] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common analysis study daily] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common analysis study daily] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //과목별 학습분석 데이터 요청
  def analysisStudySubject(key: Option[String]) = Action { request =>
    Logger.info("[API Common analysis study subject] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val messages = CommonHelper.analysisStudySubject(bodyJson)
          Logger.info("[API Common analysis study subject] success")
          Ok(Json.toJson(messages))

        } else {
          Logger.info("[API Common analysis study subject] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common analysis study subject] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common analysis study subject] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //게시물 삭제 요청
  def deletePosting(key: Option[String]) = Action { request =>
    Logger.info("[API Common posting delete] request")
    if (key.equals(secretKey)) {
      try {
        println(request.body.toString)
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          CommonHelper.deletePosting(bodyJson)
          Logger.info("[API Common posting delete] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "posting delete success")))
        } else {
          Logger.info("[API Common posting delete] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common posting delete] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common posting delete] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //게시물 신고 요청
  def postingReport(key: Option[String]) = Action { request =>
    Logger.info("[API Common posting Report] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          val message = CommonHelper.postingReport(bodyJson)
          Logger.info(s"[API Common posting Report] $message")
          Ok(Json.toJson(Map("result" -> "success", "message" -> message)))
        } else {
          Logger.info("[API Common posting Report] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common posting Report] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common posting Report] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //push 수신 설정 요청
  def updatePushSetting(key: Option[String]) = Action { request =>
    Logger.info("[API Common push change] request")
    if (key.equals(secretKey)) {
      try {
        println(request.body.toString)
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          CommonHelper.updatePushSetting(bodyJson)
          Logger.info("[API Common push change] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "push change success")))
        } else {
          Logger.info("[API Common push change] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common push change] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common push change] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //탈퇴 요청
  def withdrawal(key: Option[String]) = Action { request =>
    Logger.info("[API Common withdrawal] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          if (CommonHelper.passwordCheck(bodyJson)) {
            CommonHelper.withdrawal(bodyJson)
            Logger.info("[API Common withdrawal] success")
            Ok(Json.toJson(Map("result" -> "success", "message" -> "withdrawal success")))
          } else {
            Logger.info("[API Common withdrawal] password is incorrect")
            Ok(Json.toJson(Map("result" -> "fail", "message" -> "password is incorrect")))
          }
        } else {
          Logger.info("[API Common withdrawal] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common withdrawal] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common withdrawal] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //device token 설정 요청
  def setToken(key: Option[String]) = Action { request =>
    Logger.info("[API Common set token] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val user = CommonHelper.secretIsValid(bodyJson)
        if (user.isDefined) {
          CommonHelper.setToken(bodyJson)
          Logger.info("[API Common set token] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "set token success")))
        } else {
          Logger.info("[API Common set token] user id and secret key is not matched")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "user id and secret key is not matched")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common set token] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common set token] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  def usersService = new UsersService

  //비밀번호 찾기 요청
  def findPassword(key: Option[String]) = Action { request =>
    Logger.info("[API Common find password] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        val email = (bodyJson \ "email").as[String]
        val user = Await.result(usersService.findByEmail(email), Duration.Inf)
        if (user.isDefined) {
          CommonHelper.sendTempPassword(user.get.id, email)
          Logger.info("[API Common find password] success")
          Ok(Json.toJson(Map("result" -> "success", "message" -> "임시비밀번호를 이메일로 발송하였습니다.")))
        } else {
          Logger.info("[API Common find password] user not found")
          Ok(Json.toJson(Map("result" -> "fail", "message" -> "해당 사용자가 존재하지 않습니다.")))
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common find password] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common find password] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }

  //공지사항 데이터 요청
  def noticeList(key: Option[String]) = Action { request =>
    Logger.info("[API Common notice list] request")
    if (key.equals(secretKey)) {
      try {
        val bodyJson = request.body.asJson.get
        println(bodyJson.toString())
        implicit val formats = Serialization.formats(NoTypeHints)
        val notices = CommonHelper.getNotices()
        var result: Map[String, String] = new HashMap[String, String]
        result += ("result" -> "success")
        result += ("notices" -> write(notices))
        Logger.info("[API Common notice list] success :")
        Ok(Json.toJson(result))
      } catch {
        case e: Exception =>
          e.printStackTrace()
          Logger.info("[API Common notice list] invalid body")
          BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "invalid body")))
      }
    } else {
      Logger.info("[API Common notice list] access denied")
      BadRequest(Json.toJson(Map("result" -> "fail", "message" -> "access denied")))
    }
  }
}