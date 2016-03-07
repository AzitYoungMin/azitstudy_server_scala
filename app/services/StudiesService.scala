package services

import java.sql.Time

import controllers.api.helpers.ActivitiesRowForList
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class StudiesService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val studies = TableQuery[Tables.Studies]

  private val textbooks = TableQuery[Tables.Textbooks]

  private val subjects = TableQuery[Tables.Subjects]

  private val analysisCategory = TableQuery[Tables.AnalysisCategory]

  private val studyRecords = TableQuery[Tables.StudyRecords]

  private val studyType = TableQuery[Tables.StudyType]

  def all(): Future[Seq[StudiesRow]] = db.run(studies.result)

  def insert(studiesRow: StudiesRow): Future[Unit] =  db.run(studies += studiesRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(studies.filter(_.id === id).delete)

  def update(studiesRow: StudiesRow) = db.run(studies.filter(_.id === studiesRow.id).update(studiesRow))

  def findById(id: Int): Future[Option[StudiesRow]] = db.run(studies.filter(_.id === id).result.headOption)

  val format = new java.text.SimpleDateFormat("yyyy.MM.dd")

  def findByStudentIdAndCreatedAtQuery(id: Int, createdAt: java.sql.Date) = for{
    study <- studies.filter(row =>(row.studentId === id && row.isDeleted === false && (row.createdAt === createdAt || row.isRepeated === true)))
    textbook <- textbooks.filter(_.id === study.textbookId)
    studyType <- studyType.filter(_.id === study.studyTypeId)
  } yield (study, textbook, studyType)

  def findByStudentIdAndCreatedAt(id: Int, createdAt: java.sql.Date): Future[Seq[ActivitiesRowForList]] = db.run(findByStudentIdAndCreatedAtQuery(id, createdAt).result).map(rows => rows.map(row =>
    ActivitiesRowForList(id = row._1.id, title = row._3.title, sub_title = row._2.title, activity_type = 1, goal=row._1.goal, activity_type_id = row._1.activityTypeId)
  ))

  def updateIsDeletedTrueQuery(id: Int) = for {s <- studies if s.id === id} yield s.isDeleted

  def updateIsDeletedTrue(id: Int) = db.run(updateIsDeletedTrueQuery(id).update(Option(true)))

  def findSubjectIdByStudyIdQuery(id: Int) = for {
    study <- studies.filter(_.id === id)
    textbook <- textbooks.filter(_.id === study.textbookId)
    subject <- subjects.filter(_.id === textbook.subjectId)
  } yield subject

  def findSubjectByStudyId(id: Int): Future[Option[SubjectsRow]] = db.run(findSubjectIdByStudyIdQuery(id).result.headOption)

  def findAnalysisTypeByStudyIdQuery(id: Int) = for {
    study <- studies.filter(_.id === id)
    textbook <- textbooks.filter(_.id === study.textbookId)
    analysis <- analysisCategory.filter(_.id === textbook.analysisCategoryId)
  } yield analysis.id

  def findAnalysisTypeByStudyId(id: Int): Future[Option[Int]] = db.run(findAnalysisTypeByStudyIdQuery(id).result.headOption)

  def getByStudentIdAndCreatedAtQuery(id: Int, createdAt: java.sql.Date) = for{
    study <- studies.filter(row =>(row.studentId === id && row.isDeleted === false && (row.createdAt === createdAt || row.isRepeated === true)))
    studyType <- studyType.filter(_.id === study.studyTypeId)
  } yield (study, studyType)

  def textbooksService = new TextbooksService
  def customTextbooksService = new CustomTextbooksService

  def getByStudentIdAndCreatedAt(id: Int, createdAt: java.sql.Date): Future[Seq[ActivitiesRowForList]] = db.run(getByStudentIdAndCreatedAtQuery(id, createdAt).result).map(rows => rows.map { row =>
    var title = ""
    if(row._1.textbookId.get == 0 && row._1.customTextbookId.get != 0){
      title = Await.result(customTextbooksService.findById(row._1.customTextbookId.get), Duration.Inf).get.title.get
    }else if(row._1.customTextbookId.get == 0 && row._1.textbookId.get != 0 ){
      title = Await.result(textbooksService.findById(row._1.textbookId.get), Duration.Inf).get.title.get
    }
    ActivitiesRowForList(id = row._1.id, title = row._2.title, sub_title = Option(title), activity_type = 1, goal = row._1.goal, activity_type_id = row._1.activityTypeId)
  })
}