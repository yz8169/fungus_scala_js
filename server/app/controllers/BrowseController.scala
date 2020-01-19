package controllers

import java.io.File
import java.nio.file.Files

import javax.inject.Inject
import dao._
import models.Tables._
import play.api.data.Forms._
import play.api.data._
import play.api.libs.json.Json
import play.api.mvc._
import tool.FormTool

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by yz on 2017/7/7.
  */
case class ClassifyData(phylum: String, outline: String, order: String, family: String, genus: String, species: String)

class BrowseController @Inject()(classifyDao: ClassifyDao, basicInfoDao: BasicInfoDao, formTool:FormTool,
                                 appendixDao: AppendixDao) extends Controller {

  def toIndex = Action {
    Ok(views.html.cn.browse.index())
  }

  def getPhylumInfo = Action.async {
    classifyDao.selectAll.map { x =>
      val phylums = x.map(_.phylum).distinct
      val json = Json.obj("num" -> phylums.size, "info" -> phylums)
      Ok(json)
    }
  }


  val classifyForm = Form(
    mapping(
      "phylum" -> text,
      "outline" -> text,
      "order" -> text,
      "family" -> text,
      "genus" -> text,
      "species" -> text
    )(ClassifyData.apply)(ClassifyData.unapply)
  )

  def getOutlineInfo = Action.async { implicit request =>
    val data = classifyForm.bindFromRequest.get
    classifyDao.selectByPhylum(data.phylum).map { x =>
      val infos = x.map(_.outline).distinct
      val json = Json.obj("num" -> infos.size, "info" -> infos)
      Ok(json)
    }
  }

  def getOrderInfo = Action.async { implicit request =>
    val data = classifyForm.bindFromRequest.get
    classifyDao.selectGetOrderInfo(data).map { x =>
      val infos = x.map(_.order).distinct
      val json = Json.obj("num" -> infos.size, "info" -> infos)
      Ok(json)
    }
  }

  def getFamilyInfo = Action.async { implicit request =>
    val data = classifyForm.bindFromRequest.get
    classifyDao.selectGetFamilyInfo(data).map { x =>
      val infos = x.map(_.family).distinct
      val json = Json.obj("num" -> infos.size, "info" -> infos)
      Ok(json)
    }
  }

  def getGenusInfo = Action.async { implicit request =>
    val data = classifyForm.bindFromRequest.get
    classifyDao.selectGetGenusInfo(data).map { x =>
      val infos = x.map(_.genus).distinct
      val json = Json.obj("num" -> infos.size, "info" -> infos)
      Ok(json)
    }
  }

  def getSpeciesInfo = Action.async { implicit request =>
    val data = classifyForm.bindFromRequest.get
    classifyDao.selectGetSpeciesInfo(data).map { x =>
      val infos = x.map(_.species).distinct
      val json = Json.obj("num" -> infos.size, "info" -> infos)
      Ok(json)
    }
  }

  def getIdInfo = Action.async { implicit request =>
    val data = classifyForm.bindFromRequest.get
    classifyDao.selectGetIdInfo(data).map { x =>
      val infos = x.map(_.id).distinct
      val json = Json.obj("num" -> infos.size, "info" -> infos)
      Ok(json)
    }
  }

  def getRefreshImageFuture(id: String) = {
    Future{id}.map { x =>
      val files = new File(Utils.imagePath, id)
      val images = if (!files.exists()) "NA" else {
        files.listFiles() match {
          case Array() => "NA"
          case y => y.map(_.getName).mkString(";")
        }
      }
      AppendixRow(id, images)
    }.flatMap { x =>
      appendixDao.insertOrUpdate(x)
    }
  }

  def getDetailInfo = Action.async { implicit request =>
    val data=formTool.idForm.bindFromRequest().get
    val id=data.id
    basicInfoDao.selectById(id).map(_.get).zip(classifyDao.selectById(id).map(_.get)).zip(
      getRefreshImageFuture(id).flatMap(_ => appendixDao.selectById(id).map(_.get))).
      map { x =>
        Ok(views.html.cn.browse.detailInfo((x._1._1, x._1._2, x._2)))
      }
  }




}
