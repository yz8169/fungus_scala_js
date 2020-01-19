package controllers

import javax.inject.Inject

import dao._
import play.api.libs.json.Json
import play.api.mvc._
import models.Tables._
import play.api.cache._
import play.api.data.Form
import play.api.data.Forms._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by yz on 2017/7/11.
  */
case class SearchClassifyData(phylum: Option[Seq[String]], outline: Option[Seq[String]], order: Option[Seq[String]],
                              family: Option[Seq[String]], genus: Option[Seq[String]], species: Option[Seq[String]])

case class PhenotypeData(habitat: Option[Seq[String]], bHLevel: Option[Seq[String]], mCharacteristic: Option[String])

class SearchController @Inject()(allInfoDao: AllInfoDao, classifyDao: ClassifyDao, basicInfoDao: BasicInfoDao, cache: CacheApi) extends Controller {

  def toIndex = Action {
    Ok(views.html.cn.search.index())
  }

  def toClassifyIndex = Action {
    Ok(views.html.cn.search.classifyIndex())
  }

  def toPhenotypeIndex = Action {
    Ok(views.html.cn.search.phenotypeIndex())
  }

  val classifyForm = Form(
    mapping(
      "phylum" -> optional(seq(text)),
      "outline" -> optional(seq(text)),
      "order" -> optional(seq(text)),
      "family" -> optional(seq(text)),
      "genus" -> optional(seq(text)),
      "species" -> optional(seq(text))
    )(SearchClassifyData.apply)(SearchClassifyData.unapply)
  )

  def checkClassify = Action.async { implicit request =>
    val data = classifyForm.bindFromRequest.get
    var json = Json.obj()
    if (data.phylum == None && data.outline == None && data.order == None && data.family == None &&
      data.genus == None && data.species == None) {
      json ++= Json.obj("valid" -> "false")
      Future.successful(Ok(json))
    } else {
      classifyDao.selectGetAllIdByData(data).flatMap { x =>
        allInfoDao.selectByIds(x)
      }.map { x =>
        val array = x.map { y =>
          getArrayByBasicInfo(y)
        }
        json ++= Json.obj("valid" -> "true")
        json ++= Json.obj("data" -> array)
        Ok(json)
      }
    }
  }

  val phenotypeForm = Form(
    mapping(
      "habitat" -> optional(seq(text)),
      "bHLevel" -> optional(seq(text)),
      "mCharacteristic" -> optional(text)
    )(PhenotypeData.apply)(PhenotypeData.unapply)
  )

  def searchByPhenotypeData = Action.async { implicit request =>
    val data = phenotypeForm.bindFromRequest.get
    var json = Json.obj()
    if (data.habitat == None && data.bHLevel == None && data.mCharacteristic == None) {
      json ++= Json.obj("valid" -> "false")
      Future.successful(Ok(json))
    } else {
      basicInfoDao.selectGetAllIdByPhenotypeData(data).flatMap { x =>
        allInfoDao.selectByIds(x)
      }.map { x =>
        val array = x.map { y =>
          getArrayByBasicInfo(y)
        }
        json ++= Json.obj("valid" -> "true")
        json ++= Json.obj("data" -> array)
        Ok(json)
      }
    }

  }

  def searchAllInfos = Action.async { implicit request =>
    allInfoDao.selectAll.map { x =>
      val array = x.map { y =>
        getArrayByBasicInfo(y)
      }
      Ok(Json.toJson(array))
    }
  }

  def getAllPhylums = Action.async {
    classifyDao.selectGetAllPhylumns.map { x =>
      Ok(Json.toJson(x))
    }
  }

  def getAllOutlines = Action.async {
    classifyDao.selectGetAllOutlines.map { x =>
      Ok(Json.toJson(x))
    }
  }

  def getAllOrders = Action.async {
    classifyDao.selectGetAllOrders.map { x =>
      Ok(Json.toJson(x))
    }
  }

  def getAllFamilys = Action.async {
    classifyDao.selectGetAllFamilys.map { x =>
      Ok(Json.toJson(x))
    }
  }

  def getAllGenus = Action.async {
    classifyDao.selectGetAllGenus.map { x =>
      Ok(Json.toJson(x))
    }
  }

  def getAllSpecies = Action.async {
    classifyDao.selectGetAllSpecies.map { x =>
      Ok(Json.toJson(x))
    }
  }

  def getAllHabitat = Action.async {
    basicInfoDao.selectGetAllHabitat.map { x =>
      Ok(Json.toJson(x))
    }
  }

  def getAllBHLevel = Action.async {
    basicInfoDao.selectGetAllBHLevel.map { x =>
      Ok(Json.toJson(x))
    }
  }

  def getArrayByBasicInfo(y: (BasicinfoRow, ClassifyRow)) = {
    val basicInfo = y._1
    val classify = y._2
    val detailStr = "<a title='添加到购物车' onclick=\"addCart('" + basicInfo.id + "')\" style='cursor: pointer;'><span><em class='fa fa-plus' style='color:green;'></em></span></a>" +
      "&nbsp;&nbsp;<a title='详细信息' href='" +s"${routes.BrowseController.getDetailInfo}?id=${basicInfo.id}"+"' target='_blank' style='cursor: pointer;'>" + basicInfo.id + "</a>"

    Json.obj("id" -> detailStr, "originalid" -> basicInfo.originalid, "chinesename" -> basicInfo.chinesename,
      "source" -> basicInfo.source, "storetime" -> basicInfo.storetime, "opcountry" -> basicInfo.opcountry,
      "habitat" -> basicInfo.habitat, "ctemperature" -> basicInfo.ctemperature, "cnumber" -> basicInfo.cnumber,
      "mcharacteristic" -> basicInfo.mcharacteristic, "pkind" -> basicInfo.pkind, "pmethod" -> basicInfo.pmethod,
      "bhlevel" -> basicInfo.bhlevel, "pstate" -> basicInfo.pstate, "phylum" -> classify.phylum,
      "outline" -> classify.outline, "order" -> classify.order, "family" -> classify.family,
      "genus" -> classify.genus, "species" -> classify.species
    )
  }


}
