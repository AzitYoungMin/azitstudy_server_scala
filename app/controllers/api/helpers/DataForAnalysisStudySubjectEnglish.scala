package controllers.api.helpers

/**
  * Created by Admin6 on 1/15/2016.
  */
class DataForAnalysisStudySubjectEnglish {
  var subject1: Long = 0
  var subject2: Long = 0
  var subject3: Long = 0
  var subject4: Long = 0
  var subject5: Long = 0
  var title1 = "독해"
  var title2 = "청해"
  var title3 = "문법"
  var title4 = "어휘"
  var title5 = "종합"

  def addSubject1(time: Long) = {
    subject1 = subject1 + time
  }
  def addSubject2(time: Long) = {
    subject2 = subject2 + time
  }
  def addSubject3(time: Long) = {
    subject3 = subject3 + time
  }
  def addSubject4(time: Long) = {
    subject4 = subject4 + time
  }
  def addSubject5(time: Long) = {
    subject5 = subject5 + time
  }
  def getTitle1():String = title1
  def getTitle2():String = title2
  def getTitle3():String = title3
  def getTitle4():String = title4
  def getTitle5():String = title5
 }
