package myJs

import myJs.Utils._
import org.querki.jquery._
import scalatags.Text.all._

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import com.karasiq.bootstrap.Bootstrap.default._


/**
  * Created by yz on 2019/4/9
  */
@JSExportTopLevel("ImageDetail")
object ImageDetail {

  @JSExport("init")
  def init = {
    refreshImage

  }

  @JSExport("refreshImage")
  def refreshImage = {
    val dir = "Bastina 喙枝孢"
    val data = js.Dictionary(
      "fileName" -> "Bastina 喙枝孢"
    )
    val url = g.jsRoutes.controllers.DataController.getAllFiles().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("post").data(JSON.stringify(data)).
      contentType("application/json").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Array[String]]
      val html = rs.map { key =>
        val imageUrl = g.jsRoutes.controllers.AppController.getImage().url.toString
        div(cls := "col-sm-4",marginBottom:=10,
            img(src := s"${imageUrl}?fileName=${key}&dir=${dir}", height := 250,width:="100%"),
        )
      }.mkString

      $("#dirName").text(dir)
      $("#appendix").html(html)

    }
    $.ajax(ajaxSettings)

  }

  @JSExport("show")
  def show(dir: String, fileName: String) = {
    val url = g.jsRoutes.controllers.AppController.getImage().url.toString
    val imageUrl = s"${url}?fileName=${fileName}&dir=${dir}"
    $("#image").attr("src", imageUrl)
    $("#title").html(fileName)
    jQuery("#myModal").modal(js.Dictionary(
      "keyboard" -> true
    ))

  }


}
