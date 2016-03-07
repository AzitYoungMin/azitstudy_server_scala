package services

import caseClasses.ExamListForAdmin
import controllers.api.helpers.{StudentExam, ExamForTeacher}
import play.api.{Logger, Play}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class ExamService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val exams = TableQuery[Tables.Exam]

  private val examRecords = TableQuery[Tables.ExamRecords]

  def all(): Future[Seq[ExamRow]] = db.run(exams.result)

//  def insert(examRow: ExamRow): Future[Unit] = db.run(exams += examRow).map(_ => ())

  def insert(examRow: ExamRow): Future[Int] = {
    val action =(exams returning exams.map(_.id)) += examRow
    db.run(action).map(id => id)
  }

  def delete(id: Int): Future[Int] = db.run(exams.filter(_.id === id).delete)

  def update(examRow: ExamRow) = db.run(exams.filter(_.id === examRow.id).update(examRow))

  def findByAllActiveExamList(): Future[Seq[ExamRow]] = db.run(exams.filter(row =>(row.isActive === true && row.isDelete === false)).result)

  def getActiveExamList(startIndex: Int, pageSize: Int): Future[Seq[ExamListForAdmin]] = db.run(exams.filter(row =>(row.isActive === true && row.isDelete === false)).sortBy(row =>(row.year.desc, row.month.desc)).drop(startIndex).take(pageSize).result).map(rows => rows.map{ row =>
    ExamListForAdmin(id = row.id, title = row.title.get, year = row.year.get, month = row.month.get, isActive = row.isActive.get)
  })

  def countActiveExamList(): Future[Int] = db.run(exams.filter(row =>(row.isActive === true && row.isDelete === false)).length.result)

  def getExamList(startIndex: Int, pageSize: Int): Future[Seq[ExamListForAdmin]] = db.run(exams.filter(_.isDelete === false).sortBy(row =>(row.year.desc, row.month.desc)).drop(startIndex).take(pageSize).result).map(rows => rows.map{ row =>
    ExamListForAdmin(id = row.id, title = row.title.get, year = row.year.get, month = row.month.get, isActive = row.isActive.get)
  })

  def countExamList(): Future[Int] = db.run(exams.filter(_.isDelete === false).length.result)

  def getAllActiveExamList(): Future[Seq[ExamForTeacher]] = db.run(exams.filter(row =>(row.isActive === true && row.isDelete === false)).sortBy(row =>(row.year.desc, row.month.desc)).result).map(rows => rows.map { row =>
    val title = row.year.get + "년 " + row.month.get + "월 " + row.title.get
    ExamForTeacher(exam_id = row.id, title = title)
  })

  //  def getAllActiveExamIdQuery(studentId: Int) = for{
  //    e <- exams.filter(_.isActive === true)
  //    u <- examRecords.filter(row => (row.studentId === studentId && row.examId === e.id)).exists
  //  }yield  (e.id, u)
  //
  def getAllActiveExamAndHasScore(studentId: Int): Future[Seq[StudentExam]] = db.run(exams.filter(row =>(row.isActive === true && row.isDelete === false)).result).map(rows => rows.map { rowExam =>
    val hasScore = Await.result(db.run(examRecords.filter(row => (row.studentId === studentId && row.examId === rowExam.id)).exists.result), Duration.Inf)
    StudentExam(exam_id = rowExam.id, has_score = hasScore)
  })

  def updateIsActive(id: Int, isActive: Boolean) = db.run(exams.filter(_.id === id).map(_.isActive).update(Option(isActive)))

  def updateIsDelete(id: Int, isDelete: Boolean) = db.run(exams.filter(_.id === id).map(_.isDelete).update(Option(isDelete)))

  def getLatestExam(): Future[Option[ExamRow]] = db.run(exams.filter(row =>(row.isDelete === false)).sortBy(row =>(row.year.desc, row.month.desc)).result.headOption)

  def searchByYearAndMonthAndTitleQuery(year: Int, month: Int, title: String) = for{
    exam <- { var query = exams.filter(row =>(row.isDelete === false))
      if(year != 0) query = query.filter(_.year === year)
      if(month != 0) query = query.filter(_.month === month)
      if(title != "") query = query.filter(_.title === title)
      query
    }
  } yield exam

  def searchByYearAndMonthAndTitle(year: Int, month: Int, title: String): Future[Option[ExamRow]] = db.run(searchByYearAndMonthAndTitleQuery(year, month, title).result.headOption)

  def getYearList(): Future[Seq[Option[Int]]] = db.run(exams.filter(row =>(row.isDelete === false)).groupBy(_.year).map(_._1).sortBy(_.desc).result)

  def getMonthList(year: Int): Future[Seq[Option[Int]]] = db.run(exams.filter(row => (row.isDelete === false && row.year === year)).groupBy(_.month).map(_._1).sortBy(_.desc).result)

  def getTitleList(year: Int, month: Int): Future[Seq[Option[String]]] = db.run(exams.filter(row => (row.isDelete === false && row.year === year && row.month === month)).map(_.title).result)
}