package controllers.admin

import java.sql.Timestamp
import java.util.Calendar

import caseClasses.{NoticeDetail, NoticesList}
import controllers.admin.Forms._
import controllers.authentication.Secured
import play.api.Logger
import play.api.mvc._
import services.NoticesService
import tables.Tables.NoticesRow

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class AdminNoticeController extends Controller with Secured{
  var pageSize = Properties.PAGE_SIZE

  def noticesService = new NoticesService

  //공지사항 리스트
  def list(page: Int) = withAuth { user => implicit request =>
    val startIndex = (page - 1) * pageSize

    val list = Await.result(noticesService.getNotices(startIndex, pageSize), Duration.Inf)
    val totalSize = Await.result(noticesService.countNotices(), Duration.Inf)

    Ok(views.html.admin.notice.list(page, pageSize, totalSize, NoticesList(list = list)))
  }

  //공지사항 글쓰기 페이지
  def create() = withAuth { user => implicit request =>

    Ok(views.html.admin.notice.create())
  }

  //공지사항 수정 페이지
  def edit(id: Int) = withAuth { user => implicit request =>
    val existingNotice = Await.result(noticesService.findById(id), Duration.Inf).get

    Ok(views.html.admin.notice.edit(NoticeDetail(notice = existingNotice)))
  }

  //공지사항 글쓰기
  def createNotice = Action { implicit request =>
    noticeForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("create notice")
        println(formWithErrors)
        BadRequest("fail")
      },
      notice => {
        Await.result(noticesService.insert(NoticesRow(id = 0, title = notice.title, article = notice.article, createdAt = new Timestamp(Calendar.getInstance().getTime.getTime))), Duration.Inf)
        Redirect(routes.AdminNoticeController.list())
      }
    )
  }

  //공지사항 수정
  def updateNotice = Action { implicit request =>
    noticeForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.info("create notice")
        println(formWithErrors)
        BadRequest("fail")
      },
      notice => {
        val existingNotice = Await.result(noticesService.findById(notice.id), Duration.Inf).get
        Await.result(noticesService.update(existingNotice.copy(title = notice.title, article = notice.article)), Duration.Inf)
        Redirect(routes.AdminNoticeController.list())
      }
    )
  }
}