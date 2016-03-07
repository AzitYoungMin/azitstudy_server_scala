package services

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PostingImagesService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val postingImages = TableQuery[Tables.PostingImages]

  def all(): Future[Seq[PostingImagesRow]] = db.run(postingImages.result)

  def insert(postingImagesRow: PostingImagesRow): Future[Unit] = db.run(postingImages += postingImagesRow).map { _ => () }

  def delete(id: Int): Future[Int] = db.run(postingImages.filter(_.id === id).delete)

  def findById(id: Int): Future[Option[PostingImagesRow]] = db.run(postingImages.filter(_.id === id).result.headOption)

  def findByPostingId(id: Int): Future[Seq[PostingImagesRow]] = db.run(postingImages.filter(_.postingId === id).result)
}