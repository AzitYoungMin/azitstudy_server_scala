package controllers.api.helpers

/**
 * Created by Admin6 on 1/15/2016.
 */
class DataForAnalysisStudySubject {
  var totalStudy: Long = 0
  var myRank: Long = 0
  var contentsBasic: Long = 0
  var contentsSolution: Long = 0
  var contentsEbs: Long = 0
  var contentsReal: Long = 0
  var typeIndependently: Long = 0
  var typeLecture: Long = 0
  var typeSchool: Long = 0
  var typePrivateEdu: Long = 0
  var typePrivateTeacher: Long = 0
  var typeCheck: Long = 0
  var typeExam: Long = 0

  def addTotalStudy(time: Long)={
    totalStudy = totalStudy + time
  }
  def addContentsBasic(time: Long)={
    contentsBasic = contentsBasic + time
  }
  def addContentsSolution(time: Long)={
    contentsSolution = contentsSolution + time
  }
  def addContentsEbs(time: Long)={
    contentsEbs = contentsEbs + time
  }
  def addContentsReal(time: Long)={
    contentsReal = contentsReal + time
  }
  def addTypeIndependently(time: Long)={
    typeIndependently = typeIndependently + time
  }
  def addTypeLecture(time: Long)={
    typeLecture = typeLecture + time
  }
  def addTypeSchool(time: Long)={
    typeSchool = typeSchool + time
  }
  def addTypePrivateEdu(time: Long)={
    typePrivateEdu = typePrivateEdu + time
  }
  def addTypePrivateTeacher(time: Long)={
    typePrivateTeacher = typePrivateTeacher + time
  }
  def addTypeCheck(time: Long)={
    typeCheck = typeCheck + time
  }
  def addTypeExam(time: Long)={
    typeExam = typeExam + time
  }
}
