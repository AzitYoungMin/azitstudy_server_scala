package common

import java.util.Calendar

import controllers.admin.Properties
import play.api.Logger
import play.api.libs.json.Json
import services.{MessagesService, UsersService, PushMessageService}
import tables.Tables.MessagesRow

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scalaj.http.{Http, HttpResponse}

object PushHelper {

  def pushMessageService = new PushMessageService

  //push message 전송
  def sendPush(jsonString: String): HttpResponse[String] ={
    val result = Http("https://gcm-http.googleapis.com/gcm/send").postData(jsonString)
      .header("Content-Type", "application/json")
      .header("Charset", "UTF-8")
      .header("Authorization", "key=AIzaSyCs7aVBQkAgpkePtCRCyhFWh5t3Zg1r1lc")
      .asString
    result
  }

  //push message 전송을 위한 json 생성
  def getJsonString(tokens: Seq[String], title: String, message: String, code: String): String ={
    val json = Json.toJson(Map("registration_ids" -> Json.toJson(tokens), "data" -> Json.toJson(Map("title" -> title, "message" -> message, "code"-> code))))
    json.toString
  }

  //자동 push message 전송을 위해 시간 체크
  def checkTimeAndDay(messageId: Int): Boolean = {
    var result = false
    val today = Calendar.getInstance()
    val hour = today.get(Calendar.HOUR_OF_DAY)
    val minute = today.get(Calendar.MINUTE)
    var day = today.get(Calendar.DAY_OF_WEEK) - 1
    if(day == 0 ){
      day = 7
    }

    val pushMessage = Await.result(pushMessageService.findById(messageId), Duration.Inf).get
//    println("hour: " + hour + ", minute: " + minute + ", day: " + day)

    if(hour == pushMessage.hour.get && minute == pushMessage.minute.get) {
      val splitDay = pushMessage.day.get.split(",")
      splitDay.zipWithIndex.map { row =>
        if (row._1.equals("1") && (row._2 + 1) == day) {
          result = true
        }
      }
    }
    result
  }

  def usersService = new UsersService

  def messagesService = new MessagesService

  //입력된 정보들을 이용해 json 생성, push 전송, message 전송
  def sendPushMessage(receiverIds: List[Int], messageId: Int, code: String) = {
    val optionTokens = Await.result(usersService.getTokenByIds(receiverIds), Duration.Inf)
    val tokens = optionTokens.flatten
    val message = Await.result(pushMessageService.findById(messageId), Duration.Inf).get.message.get
    val pushJson = getJsonString(tokens, "", message, code)
    val pushResult = sendPush(pushJson)
    if(pushResult.isError){
      Logger.info("[Send push message] fail]:" + code)
    }else{
      messageSend(receiverIds, Properties.ADMIN_ID, "Azit", message)
      Logger.info("[Send push message] success]:" + code)
    }
  }

  //push 전송시 동일한 메세지를 azit app의 message 기능을 이용해 전송
  def messageSend(receiverIds: List[Int], senderId: Int, title: String, message: String) = {
    val date = new java.util.Date()
    val createdAt = new java.sql.Timestamp(date.getTime)
    receiverIds.foreach(receiverId =>
      Await.result(messagesService.insert(MessagesRow(id = 0, message = Option(message), senderId = senderId, receiverId = receiverId, createdAt = Option(createdAt), isNew = Option(true), title = Option(title))), Duration.Inf)
    )
  }
}
