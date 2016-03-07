package common

import akka.actor.{Cancellable, Props, ActorSystem, ActorRef}

/**
 * Created by dykim on 2/18/16.
 */
//고유한 actor schedule 생성
object SingletonSchedule {
  var schedule: ActorRef = null

  def getSchedule(): ActorRef = {
    if(schedule == null){
      val system = ActorSystem("azit-push")
      schedule = system.actorOf(Props(SchedulingActor), "schedule")
    }
    schedule
  }
}
