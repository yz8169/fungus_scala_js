package myJs

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import Utils._
import org.querki.jquery._
import org.scalajs.dom._

import scala.scalajs.js
import scalatags.Text.all._

import scala.scalajs.js.JSON


/**
  * Created by yz on 2019/4/9
  */
@JSExportTopLevel("ImageIndex")
object ImageIndex {

  @JSExport("init")
  def init = {
    refreshLeft
    $(window).scroll { (y: Element) => scrollEvent(y) }

  }

  def scrollEvent(y: Element) = {
    val targetHeight = $("#myScrollspy").offset().top
    val scrollTop = $(y).scrollTop()
    if (scrollTop>targetHeight) {
      $(".affix").css("top", "20px")
    } else {
      $(".affix").css("top", s"220px")
    }

  }

  def refreshLeft = {
    val url = g.jsRoutes.controllers.DataController.getAllDirNames().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).contentType("application/json").
      `type`("get").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Array[String]]
      val html = rs.map { x =>
        val url = g.jsRoutes.controllers.DataController.imageDetailBefore().url.toString
        li(
          a(cls := "", href := s"#", onclick := "ImageIndex.click(this)", x))
      }.mkString
      $("#myNav").html(html)
      $("#myNav li:first a").trigger("click")
    }
    $.ajax(ajaxSettings)

  }

  @JSExport("click")
  def click(y: Element) = {
    $("li").foreach { y =>
      $(y).removeClass("active")
    }
    $(y).parent().addClass("active")
    val dir = $(y).text()
    refreshImage(dir)
  }

  def refreshImage(dir: String) = {
    val data = js.Dictionary(
      "fileName" -> dir
    )
    val url = g.jsRoutes.controllers.DataController.getAllFiles().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(url).`type`("post").data(JSON.stringify(data)).
      contentType("application/json").success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Array[String]]
      val html = rs.map { key =>
        val imageUrl = g.jsRoutes.controllers.AppController.getImage().url.toString
        div(cls := "col-sm-4", marginBottom := 10,
          a(href:=s"${imageUrl}?fileName=${key}&dir=${dir}",target:="_blank",
            img(src := s"${imageUrl}?fileName=${key}&dir=${dir}&height=250", height := 250, width := "100%")
          )
        )
      }.mkString
      $("#dirName").text(dir)
      $("#appendix").html(html)
    }
    $.ajax(ajaxSettings)

  }


}
