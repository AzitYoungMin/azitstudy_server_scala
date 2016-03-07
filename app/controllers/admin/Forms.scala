package controllers.admin

import java.sql.Blob

import caseClasses.ExamCriteriaDetail
import play.api.data.Forms._
import play.api.data._
import tables.Tables.ExamRow

/**
 * Created by Admin6 on 11/10/2015.
 */
object Forms {
  case class ExamForForm(id: Int,
                         title: Option[String] = None,
                         date: Option[java.sql.Date] = None,
                         totalScore: Option[Int] = None,
                         examineeCount: Option[Int] = None,
                         detailPoint: Option[String] = None,
                         eduInstId: Int,
                         subjectId: Int,
                         isActive: Option[Boolean] = None)
  val registerExamForm = Form(
    mapping(
      "id" -> number,
      "title" -> optional(text),
      "date" -> optional(sqlDate),
      "totalScore" -> optional(number),
      "examineeCount" -> optional(number),
      "detailPoint" -> optional(text),
      "eduInstId" -> number,
      "subjectId" -> number,
      "isActive" -> optional(boolean)
    )(ExamForForm.apply)(ExamForForm.unapply)
  )
  case class ExamCriteriaForForm(id: Int, tier1Min: Option[Int] = None, tier1Max: Option[Int] = None, tier2Min: Option[Int] = None, tier2Max: Option[Int] = None, tier3Min: Option[Int] = None, tier3Max: Option[Int] = None, tier4Min: Option[Int] = None, tier4Max: Option[Int] = None, tier5Min: Option[Int] = None, tier5Max: Option[Int] = None, tier6Min: Option[Int] = None, tier6Max: Option[Int] = None, tier7Min: Option[Int] = None, tier7Max: Option[Int] = None, tier8Min: Option[Int] = None, tier8Max: Option[Int] = None, tier9Min: Option[Int] = None, tier9Max: Option[Int] = None)
  case class ExamCriteriaForm(score: ExamCriteriaForForm, standard: ExamCriteriaForForm, percentile: ExamCriteriaForForm)

  val examCriteriaForm: Form[ExamCriteriaForm] = Form(
    mapping(
      "score" -> mapping (
        "id" -> number,
        "tier1Min" -> optional(number),
        "tier1Max" -> optional(number),
        "tier2Min" -> optional(number),
        "tier2Max" -> optional(number),
        "tier3Min" -> optional(number),
        "tier3Max" -> optional(number),
        "tier4Min" -> optional(number),
        "tier4Max" -> optional(number),
        "tier5Min" -> optional(number),
        "tier5Max" -> optional(number),
        "tier6Min" -> optional(number),
        "tier6Max" -> optional(number),
        "tier7Min" -> optional(number),
        "tier7Max" -> optional(number),
        "tier8Min" -> optional(number),
        "tier8Max" -> optional(number),
        "tier9Min" -> optional(number),
        "tier9Max" -> optional(number)
      )(ExamCriteriaForForm.apply)(ExamCriteriaForForm.unapply),
      "standard" -> mapping (
        "id" -> number,
        "tier1Min" -> optional(number),
        "tier1Max" -> optional(number),
        "tier2Min" -> optional(number),
        "tier2Max" -> optional(number),
        "tier3Min" -> optional(number),
        "tier3Max" -> optional(number),
        "tier4Min" -> optional(number),
        "tier4Max" -> optional(number),
        "tier5Min" -> optional(number),
        "tier5Max" -> optional(number),
        "tier6Min" -> optional(number),
        "tier6Max" -> optional(number),
        "tier7Min" -> optional(number),
        "tier7Max" -> optional(number),
        "tier8Min" -> optional(number),
        "tier8Max" -> optional(number),
        "tier9Min" -> optional(number),
        "tier9Max" -> optional(number)
      )(ExamCriteriaForForm.apply)(ExamCriteriaForForm.unapply),
      "percentile" -> mapping (
        "id" -> number,
        "tier1Min" -> optional(number),
        "tier1Max" -> optional(number),
        "tier2Min" -> optional(number),
        "tier2Max" -> optional(number),
        "tier3Min" -> optional(number),
        "tier3Max" -> optional(number),
        "tier4Min" -> optional(number),
        "tier4Max" -> optional(number),
        "tier5Min" -> optional(number),
        "tier5Max" -> optional(number),
        "tier6Min" -> optional(number),
        "tier6Max" -> optional(number),
        "tier7Min" -> optional(number),
        "tier7Max" -> optional(number),
        "tier8Min" -> optional(number),
        "tier8Max" -> optional(number),
        "tier9Min" -> optional(number),
        "tier9Max" -> optional(number)
      )(ExamCriteriaForForm.apply)(ExamCriteriaForForm.unapply)
    )(ExamCriteriaForm.apply)(ExamCriteriaForm.unapply)
  )

  case class NoticeForForm(id: Int,
                         title: String,
                         article: String)

  val noticeForm = Form(
    mapping(
      "id" -> number,
      "title" -> text,
      "article" -> text
    )(NoticeForForm.apply)(NoticeForForm.unapply)
  )

}
