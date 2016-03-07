package services

import controllers.api.helpers.TextbookList
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class SelectedTextbookService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val selectedTextbook = TableQuery[Tables.SelectedTextbook]

  private val textbook = TableQuery[Tables.Textbooks]

  def all(): Future[Seq[SelectedTextbookRow]] = db.run(selectedTextbook.result)

  def insert(selectedTextbookRow: SelectedTextbookRow): Future[Unit] =  db.run(selectedTextbook += selectedTextbookRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(selectedTextbook.filter(_.id === id).delete)

  def update(selectedTextbookRow: SelectedTextbookRow) = db.run(selectedTextbook.filter(_.id === selectedTextbookRow.id).update(selectedTextbookRow))

  def findByStudentIdAndStudyTypeQuery(studentId: Int, studyType: Int) = for {
    selectedText <- selectedTextbook.filter(row => (row.studentId === studentId && row.studyType === studyType)).sortBy(_.id.desc)
    textbook <- textbook.filter(_.id === selectedText.textbookId)
  } yield (textbook)

  def findByStudentIdAndStudyType(studentId: Int, studyType: Int): Future[Seq[TextbookList]] = db.run(findByStudentIdAndStudyTypeQuery(studentId, studyType).result).map(rows => rows.map(row =>
    TextbookList(id = row.id, title = row.title.get)
  ))

  def textbooksService = new TextbooksService
  def customTextbooksService = new CustomTextbooksService

  def getByStudentIdAndStudyType(studentId: Int, studyType: Int): Future[Seq[TextbookList]] = db.run(selectedTextbook.filter(row => (row.studentId === studentId && row.studyType === studyType)).sortBy(_.id.desc).result).map(rows => rows.map { row =>
    var title = ""
    if(row.isCustom.get){
      title = Await.result(customTextbooksService.findById(row.textbookId), Duration.Inf).get.title.get
    }else{
      title = Await.result(textbooksService.findById(row.textbookId), Duration.Inf).get.title.get
    }
    TextbookList(id = row.textbookId, title = title, isCustom = row.isCustom.get)
  })

  def deleteByStudentAndStudyTypeAndTextbook(studentId: Int, studyType: Int, textbookId: Int, isCustom: Boolean): Future[Int] = db.run(selectedTextbook.filter(row => (row.studentId === studentId && row.studyType === studyType && row.textbookId === textbookId && row.isCustom === isCustom)).delete)


}