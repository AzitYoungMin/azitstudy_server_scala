package services

import caseClasses._
import controllers.api.StaticValues
import controllers.api.helpers.PostingReplies
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile
import tables.Tables

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class PostingsForAdminService extends HasDatabaseConfig[JdbcProfile] {

  import slick.driver.MySQLDriver.api._

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private val postings = TableQuery[Tables.Postings]

  private val users = TableQuery[Tables.Users]

  private val replies = TableQuery[Tables.Replies]

  private val postingReport = TableQuery[Tables.PostingReport]

  private val replyReport = TableQuery[Tables.ReplyReport]

  val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  val format2 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")

  def getPostingContentsQuery(id: Int) = for {
    (p, u) <- postings.filter(_.id === id) join users on (_.userId === _.id)
  } yield (p, u)

  def getPostingContents(id: Int): Future[PostingContentsForAdmin] = {
    val action = getPostingContentsQuery(id).result
    db.run(action.headOption).map { result =>
      val postingWithUser = result.get
      PostingContentsForAdmin(posting_id = Option(postingWithUser._1.id), title = postingWithUser._1.title, writer = postingWithUser._2.name, created_at = Option(format.format(postingWithUser._1.createdAt.get)), article = postingWithUser._1.article)
    }
  }

  def getAnswerContentsQuery(id: Int) = for {
    p <- postings.filter(row => (row.parentId === id && row.id =!= id && row.isDeleted === false))
    u <- users.filter(_.id === p.userId)
  } yield (p, u)

  def postingImagesService = new PostingImagesService

  def repliesService = new RepliesService

  def getAnswerContents(id: Int): Future[Seq[AnswerContentsForAdmin]] = db.run(getAnswerContentsQuery(id).result).map(rows => rows.map { row =>
    val image = Await.result(postingImagesService.findByPostingId(row._1.id), Duration.Inf)
    var imageUrl: Option[String] = Option(null)
    if (!image.isEmpty) imageUrl = image.seq.head.imageUrl
    AnswerContentsForAdmin(answer_id = Option(row._1.id), answer_article = row._1.article, answer_image = imageUrl, writer = row._2.name, created_at = Option(format2.format(row._1.createdAt.get)), is_chosen = row._1.isChosen)
  })

  def deletePosting(id: Int) = db.run(postings.filter(_.id === id).map(_.isDeleted).update(Option(true)))

  def getPostingsForListQuery(postingType: Int, nickname: String, contents: String, title: String, contentsTitle: String, subjects: List[Int]) = for {
    p <- {
      var postingQuery = postings.filter(row => (row.typeId === postingType && row.isDeleted === false && row.id === row.parentId)).sortBy(_.id.desc)
      if (!contents.equals("")) postingQuery = postingQuery.filter(_.article like contents + "%")
      if (!title.equals("")) postingQuery = postingQuery.filter(_.title like title + "%")
      if (!contentsTitle.equals("")) postingQuery = postingQuery.filter(row => ((row.title like contentsTitle + "%") || (row.article like contentsTitle + "%")))
      if (!subjects.isEmpty) postingQuery = postingQuery.filter(_.subSubject inSet subjects)
      postingQuery
    }
    u <- {
      var userQuery = users.filter(_.id === p.userId)
      if (!nickname.equals("")) userQuery = userQuery.filter(_.name like nickname + "%")
      userQuery
    }
  } yield (p.id, u.name, p.article, p.createdAt, p.subSubject)

  def subjectsService = new SubjectsService

  def getPostingsForList(postingType: Int, nickname: String, contents: String, title: String, contentsTitle: String, subjects: List[Int], startIndex: Int, pageSize: Int): Future[Seq[PostingsForList]] = db.run(getPostingsForListQuery(postingType, nickname, contents, title, contentsTitle, subjects).drop(startIndex).take(pageSize).result).map(rows => rows.map { row =>
    var subject = ""
    if (row._5.isDefined) {
      subject = Await.result(subjectsService.getTitleBySubjectId(row._5.get), Duration.Inf).get.getOrElse("")
    }
    PostingsForList(id = row._1, name = row._2.getOrElse(""), contents = row._3.getOrElse(""), createdAt = row._4.get, subject = subject)
  })

  def countPostingsForList(postingType: Int, nickname: String, contents: String, title: String, contentsTitle: String, subjects: List[Int]): Future[Int] = db.run(getPostingsForListQuery(postingType, nickname, contents, title, contentsTitle, subjects).length.result)

  def getRepliesByPostingIdQuery(id: Int) = for {
    r <- replies.filter(row => (row.postingId === id && row.id === row.parentId && row.isDeleted === false))
    u <- users.filter(_.id === r.userId)
  } yield (r, u)

  def getRepliesByPostingId(id: Int): Future[Seq[PostingRepliesForAdmin]] = db.run(getRepliesByPostingIdQuery(id).result).map(rows => rows.map { row =>
    PostingRepliesForAdmin(reply_id = row._1.id, reply_article = row._1.reply, reply_images = row._1.imageUrl, writer = row._2.name, role_of_writer = StaticValues.USER_TYPE.lift(row._2.typeId), created_at = Option(format2.format(row._1.createdAt.get)), num_of_like = row._1.numOfLike)
  })

  def getPostingForReportQuery(postingType: Int) = for{
    r <- postingReport
    p <- postings.filter(row =>(row.typeId === postingType && row.isDeleted === false && row.id === row.parentId && row.id === r.postingId))
    u <- users.filter(_.id === p.userId)
  } yield (r.postingId, r.userId, p.article, u.name)

  def getPostingForReport(postingType:Int, startIndex: Int, pageSize: Int) : Future[Seq[PostingsForReport]] = db.run(getPostingForReportQuery(postingType).drop(startIndex).take(pageSize).result).map(rows => rows.map { row =>
    PostingsForReport(postingId = row._1, userId = row._2, contents = row._3.getOrElse(""), name = row._4.getOrElse(""))
  })

  def countPostingForReport(postingType:Int) : Future[Int] = db.run(getPostingForReportQuery(postingType).length.result)

  def getAnswerForReportQuery(postingType: Int) = for{
    r <- postingReport
    p <- postings.filter(row =>(row.typeId === postingType && row.isDeleted === false && row.id =!= row.parentId && row.id === r.postingId))
    u <- users.filter(_.id === p.userId)
  } yield (r.postingId, r.userId, p.article, u.name)

  def getAnswerForReport(postingType:Int, startIndex: Int, pageSize: Int) : Future[Seq[PostingsForReport]] = db.run(getAnswerForReportQuery(postingType).drop(startIndex).take(pageSize).result).map(rows => rows.map { row =>
    PostingsForReport(postingId = row._1, userId = row._2, contents = row._3.getOrElse(""), name = row._4.getOrElse(""))
  })

  def countAnswerForReport(postingType:Int) : Future[Int] = db.run(getAnswerForReportQuery(postingType).length.result)

  def getRepliesForReportQuery(postingType: Int) = for{
    report <- replyReport
    reply <- replies.filter(row =>(row.isDeleted === false && row.id === row.parentId && row.id === report.replyId))
    p <- postings.filter(row => (row.typeId === postingType && row.id === reply.postingId))
    u <- users.filter(_.id === reply.userId)
  } yield (report.replyId, report.userId, reply.reply, u.name)

  def getRepliesForReport(postingType: Int, startIndex: Int, pageSize: Int) : Future[Seq[RepliesForReport]] = db.run(getRepliesForReportQuery(postingType).drop(startIndex).take(pageSize).result).map(rows => rows.map { row =>
    RepliesForReport(replyId = row._1, userId = row._2, contents = row._3.getOrElse(""), name = row._4.getOrElse(""))
  })

  def countRepliesForReport(postingType: Int) : Future[Int] = db.run(getRepliesForReportQuery(postingType).length.result)

}

