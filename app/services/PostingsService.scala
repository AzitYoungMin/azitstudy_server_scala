package services

import caseClasses.{PostingsForList, UserForList}
import controllers.api.StaticValues
import controllers.api.helpers.{AnswerContents, PostingsRowForList, PostingContents}
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables
import tables.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class PostingsService extends HasDatabaseConfig[JdbcProfile]{

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val postings = TableQuery[Tables.Postings]

  private val users = TableQuery[Tables.Users]

  private val student = TableQuery[Tables.Students]

  private val postingImages = TableQuery[Tables.PostingImages]

  val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  val format2 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")

  def all(): Future[Seq[PostingsRow]] = db.run(postings.result)

  def insert(posting: PostingsRow): Future[Int] = db.run((postings returning postings.map(_.id)) += posting).map {id => id}

  def delete(id: Int): Future[Int] = db.run(postings.filter(_.id === id).delete)

  def update(posting: PostingsRow) = db.run(postings.filter(_.id === posting.id).update(posting))

  def findById(id: Int): Future[Option[PostingsRow]] = db.run(postings.filter(_.id === id).result.headOption)

  def findByTypeId(id: Int, page: Int, size: Int): Future[Seq[PostingsRow]] = db.run(postings.filter(row => (row.typeId === id && row.id === row.parentId && row.isDeleted === false)).sortBy(_.id.desc).drop((page - 1) * size).take(size).result)

  def countByTypeId(id: Int): Future[Int] = db.run(postings.filter(row => (row.typeId === id && row.id === row.parentId && row.isDeleted === false)).length.result)

  def findByTypeIdAndSubjectId(id: Int, page: Int, size: Int, subjectId: Int): Future[Seq[PostingsRow]] = db.run(postings.filter(row => (row.typeId === id && row.id === row.parentId && row.subjectId === subjectId && row.isDeleted === false)).sortBy(_.id.desc).drop((page - 1) * size).take(size).result)

  def countByTypeIdAndSubjectId(id: Int, subjectId: Int): Future[Int] = db.run(postings.filter(row => (row.typeId === id && row.id === row.parentId && row.subjectId === subjectId && row.isDeleted === false)).length.result)

  def getPostingContentsQuery(id: Int) = for {
    (p, u) <- postings.filter(_.id === id) join users on (_.userId === _.id)
  } yield (p, u)

  def getPostingContents(id: Int) : Future[PostingContents] = {
    val action = getPostingContentsQuery(id).result
    db.run(action.headOption).map{ result =>
      val postingWithUser = result.get
      PostingContents(posting_id = Option(postingWithUser._1.id), title = postingWithUser._1.title, user_id = Option(postingWithUser._2.id), writer = postingWithUser._2.name, created_at = Option(format.format(postingWithUser._1.createdAt.get)), article = postingWithUser._1.article, num_of_reply = postingWithUser._1.numOfReply, is_chosen = postingWithUser._1.isChosen)
    }
  }

  def findByTypeIdAndUser(userId: Int, typeId: Int): Future[Seq[PostingsRow]] = db.run(postings.filter(row =>(row.userId === userId && row.typeId === typeId && row.isDeleted === false)).sortBy(_.id.desc).result)

  def getPostingForMentorQuery(id: Int, typeId: Int) = for {
    p1 <- postings.filter(row => (row.typeId === typeId && row.userId === id && row.isDeleted === false))
    p2 <- postings.filter(row => (row.id === p1.parentId && row.isDeleted === false)).groupBy(x=>x).map(_._1).sortBy(_.id.desc)
    u <- users.filter(_.id === p2.userId)
  } yield (p2, u)

  def findForMentor(userId: Int, typeId: Int): Future[Seq[(PostingsRow, UsersRow)]] = db.run(getPostingForMentorQuery(userId, typeId).result)

  def getAnswerContentsQuery(id: Int) = for {
    p <- postings.filter(row => (row.parentId === id && row.id =!= id && row.isDeleted === false))
    u <- users.filter(_.id === p.userId)
  } yield (p, u)

  def postingImagesService = new PostingImagesService

  def repliesService = new RepliesService

  def getAnswerContents(id: Int) : Future[Seq[AnswerContents]] = db.run(getAnswerContentsQuery(id).result).map(rows => rows.map{row =>
      val image = Await.result(postingImagesService.findByPostingId(row._1.id), Duration.Inf)
      var imageUrl: Option[String] = Option(null)
      if(!image.isEmpty)  imageUrl = image.seq.head.imageUrl
      val numOfReply = Await.result(repliesService.countByPostingId(row._1.id), Duration.Inf)
      AnswerContents(answer_id = Option(row._1.id), answer_article = row._1.article, answer_image = imageUrl, user_id = Option(row._2.id), writer = row._2.name, created_at = Option(format2.format(row._1.createdAt.get)), is_chosen = row._1.isChosen, score = row._1.score, profile_image = row._2.profileImage, num_of_replies = numOfReply)
  })

  def updateHasNewAnswerFalseQuery(id: Int) = for {p <- postings if p.id === id} yield p.hasNewAnswer

  def updateHasNewAnswerFalse(id: Int) = db.run(updateHasNewAnswerFalseQuery(id).update(Option(false)))

  def checkHasNewAnswerByUserId(id: Int): Future[Boolean] = db.run(postings.filter(row => (row.userId === id && row.hasNewAnswer === true)).exists.result)

  def deletePosting(id: Int) = db.run(postings.filter(_.id === id).map(_.isDeleted).update(Option(true)))

  def getChoicedAnswerId(id: Int): Future[Option[Int]] = db.run(postings.filter(row => (row.parentId === id && row.isChosen === true && row.id =!= row.parentId)).map(_.id).result.headOption)
}

