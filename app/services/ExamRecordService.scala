package services

import controllers.api.StaticValues
import controllers.api.helpers.{PercentileList, RatingList}
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ExamRecordService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val examRecords = TableQuery[Tables.ExamRecords]

  private val examCriteria = TableQuery[Tables.ExamCriteria]

  private val exam = TableQuery[Tables.Exam]

  def all(): Future[Seq[ExamRecordsRow]] = db.run(examRecords.result)

  def insert(examRecordsRow: ExamRecordsRow): Future[Unit] =  db.run(examRecords += examRecordsRow).map(_ => ())

  //def delete(studentId: Int, examCriteriaId: Int): Future[Int] = db.run(examRecords.filter(row => (row.studentId === studentId && row.examCriteriaId === examCriteriaId)).delete)

  def update(examRecordsRow: ExamRecordsRow) = db.run(examRecords.filter(row => (row.studentId === examRecordsRow.studentId && row.examId === examRecordsRow.examId && row.recordTypeId === examRecordsRow.recordTypeId && row.subjectId === examRecordsRow.subjectId)).update(examRecordsRow))

  def getLatestExamId(studentId: Int): Future[Option[Option[Int]]] = db.run(examRecords.filter(_.studentId === studentId).map(_.examId).sortBy(_.desc).result.headOption)

  def getTotalScore(studentId: Int, examId: Int): Future[Option[Int]] = db.run(examRecords.filter(row => (row.studentId === studentId && row.examId === examId && row.recordTypeId === StaticValues.EXAM_RECORD_TYPE_SCORE)).map(_.score).sum.result)

  def getTotalGrade(studentId: Int, examId: Int): Future[Option[Int]] = db.run(examRecords.filter(row => (row.studentId === studentId && row.examId === examId && (row.subjectId === 10000 || row.subjectId === 30000 || row.subjectId === 50000) && row.recordTypeId === StaticValues.EXAM_RECORD_TYPE_GRADE)).map(_.score).sum.result)

  def getAnalysisGradeRatingQuery(studentId: Int, subjectId: Int) = for{
    examRecordStandard <- examRecords.filter(row => (row.studentId === studentId && row.subjectId === subjectId && row.recordTypeId === StaticValues.EXAM_RECORD_TYPE_STANDARD))
    examRecordOrigin <- examRecords.filter(row => (row.studentId === studentId && row.subjectId === subjectId && row.recordTypeId === StaticValues.EXAM_RECORD_TYPE_SCORE))
    examRecordRating <- examRecords.filter(row => (row.studentId === studentId && row.subjectId === subjectId && row.recordTypeId === StaticValues.EXAM_RECORD_TYPE_GRADE))
    exam <- exam.filter(row => (row.id === examRecordStandard.examId && row.id === examRecordOrigin.examId && row.id === examRecordRating.examId))
  } yield (exam.month, examRecordStandard.score, examRecordOrigin.score, examRecordRating.score)

  def getAnalysisGradeRating(studentId: Int, subjectId: Int): Future[Seq[RatingList]] = db.run(getAnalysisGradeRatingQuery(studentId, subjectId).result).map(rows => rows.map(row =>
    RatingList(month = row._1.get, standard = row._2.get, origin = row._3.get, rating = row._4.get)
  ))

  def getAnalysisGradePercentileQuery(studentId: Int, subjectId: Int)= for{
    examRecord <- examRecords.filter(row => (row.studentId === studentId && row.subjectId === subjectId && row.recordTypeId === StaticValues.EXAM_RECORD_TYPE_PERCENTILE))
    exam <- exam.filter(_.id === examRecord.examId)
  } yield (exam.month, examRecord.score)

  def getAnalysisGradePercentile(studentId: Int, subjectId: Int): Future[Seq[PercentileList]] = db.run(getAnalysisGradePercentileQuery(studentId, subjectId).result).map(rows => rows.map(row =>
    PercentileList(month = row._1.get, percentile = row._2.get)
  ))

  def findByStudentIdAndExamId(studentId: Int, examId: Int, recordType: Int, subjectId: Int): Future[Option[ExamRecordsRow]] = db.run(examRecords.filter(row => (row.studentId === studentId && row.examId === examId && row.recordTypeId === recordType && row.subjectId === subjectId)).result.headOption)


}