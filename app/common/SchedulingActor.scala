package common

import akka.actor._
import controllers.admin.Properties
import controllers.api.helpers.StudentsForPush
import play.Logger
import services.StudentsService

import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

//자동 push 발송을 위한 actor
object SchedulingActor extends Actor{
  import context.dispatcher
  private var scheduledTask: Cancellable = null

  //단일 scheduleTask 생성
  def getScheduledTask(): Cancellable = {
    if(scheduledTask == null){
      scheduledTask = context.system.scheduler.schedule(500 millis, 60 second, self, "tick")
    }
    scheduledTask
  }
  //actor preStart
  override def preStart = {

  }

  //actor receive
  def receive = {
    case "tick" => {
      Logger.info("message")
      sendLessPushMessage()
      sendMorePushMessage()
    }
    case "stop" => {
      scheduledTask = getScheduledTask()
      scheduledTask.cancel()
      scheduledTask = null
    }
    case "start" => {
      scheduledTask = getScheduledTask()
    }
    case unknown => postStop
  }

  //actor postStop
  override def postStop = {
    Logger.info("post stop")

  }

  def studentsService = new StudentsService

  //공부시간<목표시간 메세지
  def sendLessPushMessage() = {
    if(PushHelper.checkTimeAndDay(Properties.PUSH_MESSAGE_ID_STUDENT_LESS)){
      val studentIds = getStudentIds(false)
      println(studentIds)
      PushHelper.sendPushMessage(studentIds, Properties.PUSH_MESSAGE_ID_STUDENT_LESS, Properties.PUSH_CODE_STUDENT)
    }
  }

  //공부시간>목표시간 메세지
  def sendMorePushMessage() = {
    if(PushHelper.checkTimeAndDay(Properties.PUSH_MESSAGE_ID_STUDENT_MORE)){
      val studentIds = getStudentIds(true)
      println(studentIds)
      PushHelper.sendPushMessage(studentIds, Properties.PUSH_MESSAGE_ID_STUDENT_MORE, Properties.PUSH_CODE_STUDENT)
    }
  }

  //상황에 맞는 학생 리스트 조회
  def getStudentIds(isMore: Boolean): List[Int] = {
    val today = new java.sql.Date(new java.util.Date().getTime)
    val monday = Utils.getMonday()
    val studentIds = new ListBuffer[Int]
    val rows:Seq[StudentsForPush] = Await.result(studentsService.getStudentIdsForPush(today, monday), Duration.Inf)
    rows.foreach{ row =>
      if(isMore){
        if((row.goalTime.get / 7) < row.totalStudy.get){
          studentIds += row.id
        }
      }else{
        if((row.goalTime.get / 7) > row.totalStudy.get){
          studentIds += row.id
        }
      }
    }
    studentIds.toList
  }
}

