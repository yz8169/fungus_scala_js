package controllers

import java.io.File

import javax.inject.Inject
import org.apache.commons.lang3.StringUtils
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import tool.FormTool

/**
  * Created by yz on 2019/4/9
  */
class DataController @Inject()(cc:ControllerComponents,formTool:FormTool) extends AbstractController(cc){

  def toImageIndex=Action{implicit request=>
    Ok(views.html.cn.imageIndex())
  }

  def getAllDirNames=Action{implicit request=>
    val names=Utils.imageDir.listFiles().map(_.getName)
    println(names.head)
    Ok(Json.toJson(names))
  }

  def imageDetailBefore=Action{implicit request=>
    val data=formTool.fileNameForm.bindFromRequest().get
    Ok(views.html.cn.imageDetail(data.fileName))
  }

  def drugAllergyBefore =Action{implicit request=>
    Ok(views.html.cn.drugAllergy())
  }

  def getAllFiles = Action { implicit request =>
    val data = formTool.fileNameForm.bindFromRequest().get
    val dir=new File(Utils.imageDir,data.fileName)
    val fileNames=dir.listFiles().filter{file=>
      !file.getName.endsWith(".db")}.map(_.getName)
    Ok(Json.toJson(fileNames))

  }

  def getFileData = Action { implicit request =>
    val data=formTool.fileNameForm.bindFromRequest().get
    val file = new File(Utils.dataDir, data.fileName)
    val (columnNames, array) = Utils.getInfoByFile(file)
    val json = Json.obj("columnNames" -> columnNames, "array" -> array)
    Ok(json)
  }

  def listBefore=Action{implicit request=>
    Ok(views.html.cn.list())
  }




}
