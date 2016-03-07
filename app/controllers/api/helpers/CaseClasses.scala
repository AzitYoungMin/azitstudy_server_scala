package controllers.api.helpers

/**
 * Created by Administrator on 2015-10-13.
 */
case class PostingsRowForList(posting_id: Option[Int] = None, title: Option[String] = None, contents: Option[String] = None, year: Option[Int] = None, writer: Option[String] = None, user_id: Option[Int] = None, num_of_images: Option[Int] = None, images: Option[List[PostingImagesForList]] = None, created_at: Option[String] = None,  num_of_reply: Option[Int] = None, is_answered: Option[Boolean] = None, textbook: Option[String] = None, article: Option[String] = None, profile_image: Option[String] = None)
case class PostingContents(posting_id: Option[Int] = None, title: Option[String] = None, writer: Option[String] = None, user_id: Option[Int] = None, created_at: Option[String] = None, article: Option[String] = None, num_of_images: Option[Int] = None, images: Option[List[PostingImagesForList]] = None,  num_of_reply: Option[Int] = None, replies: Option[List[PostingReplies]] = None, answers: Option[List[AnswerContents]] = None, is_chosen: Option[Boolean] = None)
case class PostingImagesForList(id: Int, image_url: Option[String] = None)
case class AnswerContents(answer_id: Option[Int] = None, answer_article: Option[String] = None, answer_image: Option[String] = None, user_id: Option[Int] = None, writer: Option[String] = None, profile_image: Option[String] = None, created_at: Option[String] = None, replies: Option[List[PostingReplies]] = None, is_chosen: Option[Boolean] = None, score: Option[Int] = None, num_of_replies: Int)
case class PostingReplies(reply_id: Int, reply_article: Option[String] = None, reply_images: Option[String] = None, writer: Option[String] = None, profile_image: Option[String] = None, user_id: Int, role_of_writer: Option[String] = None, created_at: Option[String] = None, num_of_like: Option[Int] = None, num_of_reply: Option[Int] = None, replies: Option[List[PostingReplies]] = None, click_like: Option[Boolean] = None)
case class StudentsForSearch(user_id: Int, name: String, school: String, email: String)
case class DDayRowForList(id: Int, title: Option[String] = None, date: Option[String] = None, isActive: Option[Boolean] = None)
case class ActivitiesRowForList(id: Int, title: Option[String] = None, sub_title: Option[String] = None, activity_type: Int, goal: Option[String] = None, activity_type_id: Int, start_time: Option[String] = None, end_time: Option[String] = None, start_page: Option[Int] = None, end_page: Option[Int] = None)
case class ExamCriteria(id: Int, title: String)
case class ExamList(id: Int, title: String, exam_subject_score: List[ExamCriteria], exam_subject_percentile: List[ExamCriteria], exam_subject_standard: List[ExamCriteria])
case class UniversityList(university: String, department: String, optional: Int)
case class TextbookList(id: Int, title: String, isCustom: Boolean = false)
case class RatingList(month: Int, standard: Int, origin: Int, rating: Int)
case class PercentileList(month: Int, percentile: Int)
case class AnalysisGradeData(title: String, ratings: List[RatingList], percentiles: List[PercentileList])
case class CustomActivityTypeList(id:Int, title: String, icon_id: Int )
case class TitleTime(title: String, time: Long)
case class TitleTimes(title: String, times: List[TitleTime])
case class AnalysisStudySubjectList(total_study_time: String, my_rank: String, part_analysis: List[TitleTimes], contents_analysis: List[TitleTimes], type_analysis: List[TitleTimes])
case class StudyTimeSubjectSum(subjectId: Int, contentsBasic: Long, contentsSolution: Long, contentsEbs: Long, contentsReal: Long, typeIndependently: Long, typeLecture: Long, typeSchool: Long, typePrivateEdu: Long, typePrivateTeacher: Long, typeCheck: Long, typeExam: Long, title: String)
case class SaveHistoryList(title: String, date: String, balance: Int, addedPoint: Int)
case class RefundHistoryList(account: String, date: String, point: Int, balance: Int)
case class ExamForTeacher(exam_id: Int, title: String)
case class StudentExam(exam_id: Int, has_score: Boolean)
case class StudentsForList(student_id: Int, name: String, profile_image: String, goal_achieve: Option[String] = None, student_exam: Option[List[StudentExam]] = None)

case class MessagesRowForList(message_id: Int, message: Option[String] = None, created_at: Option[String] = None, name: Option[String] = None, title: Option[String] = None)

case class StudentsForPush(id: Int, goalTime: Option[Long] = None, totalStudy: Option[Long] = None)

case class NoticesRowForList(title: String, article: String, createdAt: String)