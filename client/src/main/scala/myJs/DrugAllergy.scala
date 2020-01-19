package myJs

import com.karasiq.bootstrap.Bootstrap.default._
import myJs.Utils._
import myJs.myPkg._
import org.querki.jquery._
import scalatags.Text.all._

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import myJs.myPkg.Implicits._
import scala.scalajs.js.JSConverters._

/**
  * Created by yz on 2019/4/9
  */
@JSExportTopLevel("DrugAllergy")
object DrugAllergy {

  var array: js.Array[js.Dictionary[String]] = _

  @JSExport("init")
  def init = {
    getOrignalData
    refreshTable(array)
    refreshName

  }

  def refreshName = {
    val names = array.map(_ ("菌株名称")).distinct
    val options = Select2Options.placeholder("--请选择--").data(names).
      allowClear(true)
    $(s":input[name='name']").empty().select2(options).`val`("").trigger("change")

  }

  @JSExport("mySearch")
  def mySearch = {
    val index = layer.alert(Tool.element, Tool.layerOptions)
    val name = $(s":input[name='name']").`val`()
    refreshTable(array, name, { () =>
      layer.close(index)
    })

  }

  def getOrignalData = {
    val url = g.jsRoutes.controllers.DataController.getFileData().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?fileName=80个标准株药敏信息.txt").contentType("application/json").
      `type`("get").async(false).success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      val tmpArray = rs("array").asInstanceOf[js.Array[js.Dictionary[String]]]
      array = tmpArray
    }
    $.ajax(ajaxSettings)
  }

  def refreshTable(array: js.Array[js.Dictionary[String]], name: js.Any = "", f: () => js.Any = () => ()) = {
    val nameColumn = ColumnOptions.title("菌株名称").sortable(true).rowspan(3).valign("middle").field("菌株名称").
      titleTooltip("菌株名称").halign("center")
    val cColumn = ColumnOptions.title("CLSI")
    val row1 = js.Array(nameColumn, cColumn) ++ js.Array("阿尼芬净MEC", "米卡芬净MEC", "卡泊芬净MEC", "5-氟孢嘧啶", "泊沙康唑",
      "伏立康唑", "伊曲康唑", "两性霉素B", "氟康唑").map { columnName =>
      ColumnOptions.field(columnName).title(columnName).sortable(true).rowspan(3).valign("middle").
        titleTooltip(columnName).halign("center")
    }
    val columns = js.Array(row1) += js.Array("MIC(ug/ml)").map { columnName =>
      ColumnOptions.title(columnName)
    } += js.Array("菌编号").map { columnName =>
      val fmt = (v: String) => {
       idA(v)
      }
      ColumnOptions.title(columnName).field(columnName).sortable(true).formatter(fmt)
    }
    val newArray = if (Utils.isBlank(name)) array else {
      array.filter(x => x("菌株名称") == name.toString)
    }
    val options = TableOptions.columnsM(columns.toJSArray).data(newArray)
    $("#table").bootstrapTable("destroy").bootstrapTable(options)
    f()

  }

  def idA(v:String)={
    val url = g.jsRoutes.controllers.BrowseController.getDetailInfo().url.toString
    a(
      target := "_blank",
      href := s"${url}?id=${v}"
    )(v).render
  }


}
