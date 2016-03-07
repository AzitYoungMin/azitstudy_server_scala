package services

import controllers.api.helpers.UniversityList
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RecommendUniversityService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val recommendUniversity = TableQuery[Tables.RecommendUniversity]

  def all(): Future[Seq[RecommendUniversityRow]] = db.run(recommendUniversity.result)

  def insert(recommendUniversityRow: RecommendUniversityRow): Future[Unit] =  db.run(recommendUniversity += recommendUniversityRow).map(_ => ())

  def delete(id: Int): Future[Int] = db.run(recommendUniversity.filter(_.id === id).delete)

  def update(recommendUniversityRow: RecommendUniversityRow) = db.run(recommendUniversity.filter(_.id === recommendUniversityRow.id).update(recommendUniversityRow))

  def getRecommendUniversityQuery(min: Int, max: Int, department: List[Int]) = for{
    recommendUniversity <- recommendUniversity.filter(row => (row.score >= min && row.score <= max))
    if recommendUniversity.departmentType inSet department
  }yield recommendUniversity

  def getRecommendUniversity(score: Int, department:  List[Int]): Future[Seq[UniversityList]] = db.run(getRecommendUniversityQuery((score*0.9).toInt, (score*1.1).toInt, department).result).map(rows => rows.map(row =>
    UniversityList(university = row.university.get, department = row.department.get, optional = row.numOfOptional.get)
  ))
}