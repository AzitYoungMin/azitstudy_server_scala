package controllers.api.helpers

/**
  * Created by Admin6 on 1/15/2016.
  */
class DataForAnalysisStudySubjectMathGa {
  var subject1: Long = 0
  var subject2: Long = 0
  var subject3: Long = 0
  var subject4: Long = 0
  var subject5: Long = 0
  var subject6: Long = 0
  var title1 = "수학1"
  var title2 = "수학2"
  var title3 = "미통기"
  var title4 = "기벡"
  var title5 = "적통"
  var title6 = "종합"

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
  def addSubject6(time: Long) = {
    subject6 = subject6 + time
  }
  def getTitle1():String = title1
  def getTitle2():String = title2
  def getTitle3():String = title3
  def getTitle4():String = title4
  def getTitle5():String = title5
  def getTitle6():String = title6
 }
