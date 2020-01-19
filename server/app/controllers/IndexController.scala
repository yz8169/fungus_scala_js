package controllers

import java.io.File

import javax.inject.Inject
import org.apache.commons.io.FileUtils
import play.api.mvc._
import tool.FormTool

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by yz on 2017/6/27.
  */
class IndexController @Inject()(cc:ControllerComponents,formTool:FormTool) extends AbstractController(cc) {

  def toIndex = Action {
    Ok(views.html.cn.index())
  }

  def toDeveloping = Action {
    Ok(views.html.cn.developing())
  }

  def toDownload=Action{implicit request=>
    Ok(views.html.cn.download())
  }

  def downloadData = Action { implicit request =>
    val data = formTool.fileNameForm.bindFromRequest().get
    val file = new File(Utils.downloadDir, data.fileName)
    println(file.getName)
    Ok.sendFile(file).as("application/x-download").
      withHeaders(
        CACHE_CONTROL -> "max-age=3600",
        CONTENT_DISPOSITION -> Utils.getContentDisposition(s"${file.getName}")
      )
  }

  def openFile=Action { implicit request =>
    val data = formTool.fileNameForm.bindFromRequest().get
    val file = new File(Utils.downloadDir, data.fileName)
    val bytes = FileUtils.readFileToByteArray(file)
    Ok(bytes).as("application/pdf")

  }

}
