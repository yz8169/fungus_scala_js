package myJs

import myJs.DrugAllergy.idA
import myJs.Utils._
import myJs.myPkg.Implicits._
import myJs.myPkg._
import org.querki.jquery._
import scalatags.Text.all._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

/**
  * Created by yz on 2019/4/9
  */
@JSExportTopLevel("MyList")
object MyList {
  var rs: js.Dictionary[js.Any] = _

  @JSExport("init")
  def init = {
    getOrignalData
    refreshTable(rs)
    refreshName

  }

  def getOrignalData = {
    val url = g.jsRoutes.controllers.DataController.getFileData().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?fileName=已录质谱信息菌株列表.txt").contentType("application/json").
      `type`("get").async(false).success { (data, status, e) =>
      val tmpRs = data.asInstanceOf[js.Dictionary[js.Any]]
      rs = tmpRs
    }
    $.ajax(ajaxSettings)
  }

  @JSExport("mySearch")
  def mySearch = {
    val index = layer.alert(Tool.element, Tool.layerOptions)
    val name = $(s":input[name='name']").`val`()
    refreshTable(rs, name, { () =>
      layer.close(index)
    })

  }

  def refreshName = {
    val array=rs("array").asInstanceOf[js.Array[js.Dictionary[String]]]
    val names = array.map(_ ("菌株名称")).distinct
    val options = Select2Options.placeholder("--请选择--").data(names).
      allowClear(true)
    $(s":input[name='name']").empty().select2(options).`val`("").trigger("change")

  }

  def refreshTable(rs: js.Dictionary[js.Any], name: js.Any = "", f: () => js.Any = () => ()) = {
    val columnNames = rs("columnNames").asInstanceOf[js.Array[String]]
    val columns = columnNames.map { columnName =>
      val fmt = myFmt(columnName)
      ColumnOptions.title(columnName).field(columnName).sortable(true).formatter(fmt)
    }
    val array=rs("array").asInstanceOf[js.Array[js.Dictionary[String]]]
    val newArray = if (Utils.isBlank(name)) array else {
      array.filter(x => x("菌株名称") == name.toString)
    }
    val options = TableOptions.columns(columns).data(newArray)
    $("#table").bootstrapTable("destroy").bootstrapTable(options)
    f()

  }

  def myFmt(columnName: String): js.Function = (v: String) => columnName match {
    case "菌株编号" => {
      DrugAllergy.idA(v)
    }
    case _ => v
  }


}
