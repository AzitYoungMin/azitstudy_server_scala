package caseClasses

import tables.Tables.{ExamCriteriaRow, NoticesRow}

case class StudentForPushList(id: Int, name: String, school: String, year: String, totalGrade: Int, createdAt: java.sql.Timestamp)
case class MentorForPushList(id: Int, name: String, university: String, year: String, major: String, gender: String)

case class UserForList(id:Int, userType: String, name: String, email: String, school: String, phone: String, createdAt:java.sql.Timestamp, isWithdrawal: Boolean, withdrawApproval: Boolean)
case class UserForApproval(id:Int, userType: String, name: String, email: String, school: String, phone: String, image: String, createdAt:java.sql.Timestamp, approval: Int)
case class UnreceivedTSMatching(teacherId: Int, tName: String, tEmail: String, tEduInst: String, studentId: Int, sName: String, sEmail: String, sEduInst: String, createdAt: java.sql.Timestamp, isAuthenticated: Int)

case class PostingsForList(id: Int, name: String, contents: String, createdAt: java.sql.Timestamp, subject: String)
case class PostingContentsForAdmin(posting_id: Option[Int] = None, title: Option[String] = None, writer: Option[String] = None, created_at: Option[String] = None, article: Option[String] = None, images: Option[List[PostingImagesForAdmin]] = None, replies: Option[List[PostingRepliesForAdmin]] = None, answers: Option[List[AnswerContentsForAdmin]] = None)
case class PostingImagesForAdmin(id: Int, image_url: Option[String] = None)
case class AnswerContentsForAdmin(answer_id: Option[Int] = None, answer_article: Option[String] = None, answer_image: Option[String] = None, writer: Option[String] = None, created_at: Option[String] = None, replies: Option[List[PostingRepliesForAdmin]] = None, is_chosen: Option[Boolean] = None)
case class PostingRepliesForAdmin(reply_id: Int, reply_article: Option[String] = None, reply_images: Option[String] = None, writer: Option[String] = None, role_of_writer: Option[String] = None, created_at: Option[String] = None, num_of_like: Option[Int] = None)
case class PostingsForReport(postingId: Int, userId: Int, contents: String, name: String)
case class RepliesForReport(replyId: Int, userId: Int, contents: String, name: String)

case class ExamListForAdmin(id: Int, title: String, year: Int, month: Int, isActive: Boolean)

case class OtherSubjects(title: String, score: Int)
case class KME(korean: Int, math: Int, english: Int)
case class StudentsForGrade(id: Int, name: String, exam: String, subject: Option[KME] = None, social: Option[List[OtherSubjects]] = None, science: Option[List[OtherSubjects]] = None, foreign: Option[OtherSubjects] = None, hasScore: Option[String] = None)
//case class ExamCriteriaForAdmin(id: Int, tier1Min: Option[Int] = None, tier1Max: Option[Int] = None, tier2Min: Option[Int] = None, tier2Max: Option[Int] = None, tier3Min: Option[Int] = None, tier3Max: Option[Int] = None, tier4Min: Option[Int] = None, tier4Max: Option[Int] = None, tier5Min: Option[Int] = None, tier5Max: Option[Int] = None, tier6Min: Option[Int] = None, tier6Max: Option[Int] = None, tier7Min: Option[Int] = None, tier7Max: Option[Int] = None, tier8Min: Option[Int] = None, tier8Max: Option[Int] = None, tier9Min: Option[Int] = None, tier9Max: Option[Int] = None, examId: Int, subjectId: Int, examRecordTypeId: Int)
case class ExamCriteriaDetail(title: String, score: ExamCriteriaRow, standard: ExamCriteriaRow, percentile: ExamCriteriaRow)

case class PointManageList(typeId: Int, name: String, nickName: Option[String], email: String, school: String, phone: String, point: Int, studentName: String, studentNickName: Option[String], studentEmail:String, postingId: Int)

case class RefundHistoryForList(name: String, email: String, school: String, phone: String, point: Int, amount: Int, account: String, isApproval: Boolean, id: Int)
case class RefundCompleteHistory(name: String, email: String, school: String, phone: String, previous: Int, refund: Int, balance: Int)
case class ChoiceChangeList(id: Int, owner: String, postingId: Int, choicedUser: String, newUser: String, isChanged: Boolean, isCanceled: Boolean, typeId: Int)
case class NoticesList(list: Seq[NoticesRow])
case class NoticeDetail(notice: NoticesRow)