package myJs

import myJs.myPkg.{BootstrapTableJQueryImplicits, Layer, LayerOptions}
import scalatags.Text.all._
import com.karasiq.bootstrap.Bootstrap.default._

import scala.scalajs.js
import org.scalajs.jquery.JQuery


/**
  * Created by yz on 2019/2/26
  */
object Utils {

  implicit class MyJson(val json: js.Dictionary[js.Any]) {

    def myGet(key: String) = json.getOrElse(key, "NA").toString

  }

  implicit class MyJsArray(val array: js.Array[js.Dictionary[js.Any]]) {
    val emptyDic: js.Dictionary[js.Any] = js.Dictionary()

    def myHead = array.headOption.getOrElse(emptyDic)

  }

  implicit class BootstrapTableJQuery(val jq: JQuery) extends AnyVal {

  }

  val g = js.Dynamic.global
  val layer = g.layer.asInstanceOf[Layer]

  val dataToggle=attr("data-toggle")
  val dataContent=attr("data-content")
  val dataContainer=attr("data-container")
  val dataPlacement=attr("data-placement")
  val dataHtml=attr("data-html")
  val dataAnimation=attr("data-animation")
  val dataTrigger=attr("data-trigger")

  def isBlank(v:js.Any)=v==null || v.toString.trim==""


}
