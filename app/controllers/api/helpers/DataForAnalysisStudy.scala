package controllers.api.helpers

/**
 * Created by Admin6 on 1/15/2016.
 */
class DataForAnalysisStudy {
  var highRankTotalStudy: Long = 0
  var totalStudy: Long = 0
  var thisWeekKorea: Long = 0
  var thisWeekMath: Long = 0
  var thisWeekEnglish: Long = 0
  var thisWeekSocialScience: Long = 0
  var thisWeekNonsubject: Long = 0
  var lastWeekKorea: Long = 0
  var lastWeekMath: Long = 0
  var lastWeekEnglish: Long = 0
  var lastWeekSocialScience: Long = 0
  var lastWeekNonsubject: Long = 0
  var subject: Long = 0
  var alone: Long = 0
  var sunday: Long = 0
  var monday: Long = 0
  var tuesday: Long = 0
  var wednesday: Long = 0
  var thursday: Long = 0
  var friday: Long = 0
  var saturday: Long = 0
  var highRankSunday: Long = 0
  var highRankMonday: Long = 0
  var highRankTuesday: Long = 0
  var highRankWednesday: Long = 0
  var highRankThursday: Long = 0
  var highRankFriday: Long = 0
  var highRankSaturday: Long = 0
  var sleep: Long = 0
  var eat: Long = 0
  var rest: Long = 0
  var etc: Long = 0
  var waste: Long = 0
  var highRankGoalTime: Long = 0

  def addTotalStudy(time: Long)={
    totalStudy = totalStudy + time
  }
  def addThisWeekKorea(time: Long)={
    thisWeekKorea = thisWeekKorea + time
  }
  def addThisWeekMath(time: Long)={
    thisWeekMath = thisWeekMath + time
  }
  def addThisWeekEnglish(time: Long)={
    thisWeekEnglish = thisWeekEnglish + time
  }
  def addThisWeekSocialScience(time: Long)={
    thisWeekSocialScience = thisWeekSocialScience + time
  }
  def addThisWeekNonsubject(time: Long)={
    thisWeekNonsubject = thisWeekNonsubject + time
  }
  def addLastWeekKorea(time: Long)={
    lastWeekKorea = lastWeekKorea + time
  }
  def addLastWeekMath(time: Long)={
    lastWeekMath = lastWeekMath + time
  }
  def addLastWeekEnglish(time: Long)={
    lastWeekEnglish = lastWeekEnglish + time
  }
  def addLastWeekSocialScience(time: Long)={
    lastWeekSocialScience = lastWeekSocialScience + time
  }
  def addLastWeekNonsubject(time: Long)={
    lastWeekNonsubject = lastWeekNonsubject + time
  }
  def addSubject(time: Long)={
    subject = subject + time
  }
  def addAlone(time: Long)={
    alone = alone + time
  }
  def addSunday(time: Long)={
    sunday = sunday + time
  }
  def addMonday(time: Long)={
    monday = monday + time
  }
  def addTuesday(time: Long)={
    tuesday = tuesday + time
  }
  def addWednesday(time: Long)={
    wednesday = wednesday + time
  }
  def addThursday(time: Long)={
    thursday = thursday + time
  }
  def addFriday(time: Long)={
    friday = friday + time
  }
  def addSaturday(time: Long)={
    saturday = saturday + time
  }
  def addHighRankTotalStudy(time: Long)={
    highRankTotalStudy = highRankTotalStudy + time
  }
  def addSleep(time: Long)={
    sleep = sleep + time
  }
  def addEat(time: Long)={
    eat = eat + time
  }
  def addRest(time: Long)={
    rest = rest + time
  }
  def addEtc(time: Long)={
    etc = etc + time
  }
  def addWaste(time: Long)={
    waste = waste + time
  }
  def addHighRankGoalTime(time: Long)={
    highRankGoalTime = highRankGoalTime + time
  }

  def getWasteTime(numOfStudnet: Int): Long = {
    waste = ( numOfStudnet * 24 * 3600 ) - (subject + thisWeekNonsubject + sleep + eat + rest + etc )
    math.ceil(waste / numOfStudnet).toLong
  }

  def getHighRankGoalTime(numOfDate: Int): Long = {
    var temp = numOfDate
    if(temp == 0) temp = 1
    math.ceil(highRankGoalTime / temp).toLong
  }
}
