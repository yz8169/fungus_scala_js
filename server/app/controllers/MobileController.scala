package controllers

import java.io.File
import javax.inject.Inject

import play.api.mvc._
import dao._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import models.Tables._

class MobileController @Inject()(classifyDao: ClassifyDao,basicInfoDao: BasicInfoDao,appendixDao: AppendixDao) extends Controller {

  def getPhylum = Action.async {
    val data=ClassifyData("","","","","","")
    classifyDao.selectAll.map { x =>
      val phylums = x.map(_.phylum).distinct
      Ok(views.html.cn.mobile.index(phylums,data,"门名"))
    }
  }

  def getOutline(phylum:String) = Action.async {
    val data=ClassifyData(phylum,"","","","","")
    classifyDao.selectByPhylum(phylum).map { x =>
      val rows = x.map(_.outline).distinct
      Ok(views.html.cn.mobile.index(rows,data,"纲名"))
    }
  }

  def getOrder(phylum:String,outline:String) = Action.async {
    val data=ClassifyData(phylum,outline,"","","","")
    classifyDao.selectGetOrderInfo(data).map { x =>
      val rows = x.map(_.order).distinct
      Ok(views.html.cn.mobile.index(rows,data,"目名"))
    }
  }

  def getFamily(phylum:String,outline:String,order:String) = Action.async {
    val data=ClassifyData(phylum,outline,order,"","","")
    classifyDao.selectGetFamilyInfo(data).map { x =>
      val rows = x.map(_.family).distinct
      Ok(views.html.cn.mobile.index(rows,data,"科名"))
    }
  }

  def getGenus(phylum:String,outline:String,order:String,family:String) = Action.async {
    val data=ClassifyData(phylum,outline,order,family,"","")
    classifyDao.selectGetGenusInfo(data).map { x =>
      val rows = x.map(_.genus).distinct
      Ok(views.html.cn.mobile.index(rows,data,"属名"))
    }
  }

  def getSpecies(phylum:String,outline:String,order:String,family:String,genus:String) = Action.async {
    val data=ClassifyData(phylum,outline,order,family,genus,"")
    classifyDao.selectGetSpeciesInfo(data).map { x =>
      val rows = x.map(_.species).distinct
      Ok(views.html.cn.mobile.index(rows,data,"种名"))
    }
  }

  def getId(phylum:String,outline:String,order:String,family:String,genus:String,species:String) = Action.async {
    val data=ClassifyData(phylum,outline,order,family,genus,species)
    classifyDao.selectGetIdInfo(data).map { x =>
      val rows = x.map(_.id).distinct
      Ok(views.html.cn.mobile.index(rows,data,"菌种编号"))
    }
  }

  def getDetailInfo(phylum:String,outline:String,order:String,family:String,genus:String,species:String,id: String) = Action.async { implicit request =>
    basicInfoDao.selectById(id).map(_.get).zip(classifyDao.selectById(id).map(_.get)).zip(
      getRefreshImageFuture(id).flatMap(_ => appendixDao.selectById(id).map(_.get))).
      map { x =>
        Ok(views.html.cn.mobile.detailInfo((x._1._1, x._1._2, x._2)))
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



}
