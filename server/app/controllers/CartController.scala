package controllers

import java.nio.file.Files
import java.util.Date
import javax.inject.Inject

import play.api.cache._
import play.api.libs.json.Json
import play.api.mvc._
import dao._
import models.Tables.{BasicinfoRow, SubmissionRow}
import org.apache.commons.io.FileUtils
import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.collection.JavaConverters._


class CartController @Inject()(cache: CacheApi, basicInfoDao: BasicInfoDao, submissionDao: SubmissionDao,
                               allInfoDao: AllInfoDao,cc: ControllerComponents) extends AbstractController(cc) {

  def toIndex = Action {
    Ok(views.html.cn.cart.index())
  }

  def getCartInfo = Action.async { implicit request =>
    val ip = request.remoteAddress
    val map = cache.get[Map[String, Int]](ip)
    if (map == None) {
      Future.successful(Ok(Json.obj("valid" -> "false")))
    } else {
      basicInfoDao.selectByIds(map.get.keys.toBuffer).map { x =>
        val array = x.map { y =>
          getObj((y, map.get(y.id)))
        }

        val allTotalPrice = array.map(x => x.value("totalPrice").toString().toInt).sum
        Ok(Json.obj("valid" -> "true", "data" -> array, "allTotalPrice" -> allTotalPrice))
      }
    }

  }

  def addCart(id: String) = Action { implicit request =>
    val ip = request.remoteAddress
    var map = cache.getOrElse[Map[String, Int]](ip)(Map[String, Int]())
    map += (id -> (map.getOrElse(id, 0) + 1))
    cache.set(ip, map, 1.day)
    Ok(Json.toJson("success!"))
  }

  def downloadInfo = Action.async { implicit request =>
    val ip = request.remoteAddress
    val map = cache.get[Map[String, Int]](ip)
    allInfoDao.selectByIds(map.get.keys.toBuffer).map { x =>
      val header = Array("﻿菌种编号", "原始编号", "中文名称", "来源历史", "收藏时间", "原产国", "分离基物", "培养温度（℃）",
        "培养基编号（名称或配方）", "主要特征特性", "保藏类型", "保存方法", "生物危害程度", "实物状态", "门名", "纲名", "目名",
        "科名", "属名", "种名加词", "ITS序列", "18S序列")
      val buffer = ArrayBuffer[String]()
      buffer += header.mkString("\t")
      buffer ++= x.map {
        case (basicInfo, classfiy) =>
          Array(basicInfo.id, basicInfo.originalid, basicInfo.chinesename, basicInfo.source, basicInfo.storetime,
            basicInfo.opcountry, basicInfo.habitat, basicInfo.ctemperature, basicInfo.cnumber, basicInfo.mcharacteristic,
            basicInfo.pkind, basicInfo.pmethod, basicInfo.bhlevel, basicInfo.pstate, classfiy.phylum, classfiy.outline,
            classfiy.order, classfiy.family, classfiy.genus, classfiy.species, basicInfo.its, basicInfo.eighteens).mkString("\t")
      }
      val dataFile = Files.createTempFile("data", ".txt").toFile
      FileUtils.writeLines(dataFile, buffer.asJava)
      Ok.sendFile(dataFile, onClose = () => {
        Files.deleteIfExists(dataFile.toPath)
      }).withHeaders(
        CACHE_CONTROL -> "max-age=3600",
        CONTENT_DISPOSITION -> ("attachment; filename=data.txt"),
        CONTENT_TYPE -> "application/x-download"
      )
    }
  }

  def deleteCart(id: String) = Action {
    implicit request =>
      val ip = request.remoteAddress
      var map = cache.get[Map[String, Int]](ip).get
      map += (id -> (map(id) - 1))
      cache.set(ip, map, 1.day)
      Ok(Json.toJson("success!"))
  }

  def deleteId(id: String) = Action {
    implicit request =>
      val ip = request.remoteAddress
      var map = cache.get[Map[String, Int]](ip).get
      map -= id
      cache.set(ip, map, 1.day)
      Ok(Json.toJson("success!"))
  }

  def deleteAll = Action {
    implicit request =>
      val ip = request.remoteAddress
      cache.remove(ip)
      Ok(Json.toJson("success!"))
  }

  def getNum = Action {
    implicit request =>
      val ip = request.remoteAddress
      var map = cache.get[Map[String, Int]](ip)
      val num = if (map == None) 0 else map.get.values.sum
      Ok(Json.toJson(num))
  }

  def updateNum(id: String, num: String) = Action {
    implicit request =>
      val ip = request.remoteAddress
      var map = cache.get[Map[String, Int]](ip).get
      val trueNum = try {
        num.toDouble.toInt
      } catch {
        case _ => 1
      }
      map += id -> trueNum
      cache.set(ip, map, 1.day)
      Ok(Json.toJson("success!"))
  }

  case class SubmissionData(name: String, department: String, phone: String, email: String, address: String)

  val submissionForm = Form(
    mapping(
      "name" -> text,
      "department" -> text,
      "phone" -> text,
      "email" -> text,
      "address" -> text
    )(SubmissionData.apply)(SubmissionData.unapply)
  )

  def submit = Action.async {
    implicit request =>
      val data = submissionForm.bindFromRequest.get
      val ip = request.remoteAddress
      val map = cache.get[Map[String, Int]](ip).get
      val content = map.map {
        case (id, num) => "菌种编号：" + id + " 数量：" + num.toString
      }.mkString("；")
      basicInfoDao.selectByIds(map.keys.toBuffer).flatMap { x =>
        val totalPrice = x.map ( y => y.price * map(y.id)
        ).sum
        val row = SubmissionRow(0, data.name, data.department, data.phone, data.email, content, new DateTime(new Date()),
          address = data.address, totalprice =totalPrice,isdeal = 0)
        submissionDao.insert(row)
      }.map {
      x =>
        cache.remove(ip)
        Ok(Json.toJson("success!"))
    }




  }

  def getObj(y: (BasicinfoRow, Int)) = {
    val basicInfo = y._1
    val num = y._2

    val numStr = if (num == 1) {
      "<div class='input-group col-sm-3'>" +
        "<span class='input-group-btn'>" +
        "<button class='btn btn-default' onclick=\"deleteNum('" + basicInfo.id + "')\" disabled='disabled'>-</button>" +
        "</span><input type='text' class='form-control' value='" + num + "' onblur=\"updateNum(this,'" + basicInfo.id + "')\">" +
        "<span class='input-group-btn'>" +
        " <button class='btn btn-default' onclick=\"addNum('" + basicInfo.id + "')\">+</button></span>" +
        "</div>"
    } else {
      "<div class='input-group col-sm-3'>" +
        "<span class='input-group-btn'>" +
        "<button class='btn btn-default' onclick=\"deleteNum('" + basicInfo.id + "')\">-</button>" +
        "</span><input type='text' class='form-control' value='" + num + "' onblur=\"updateNum(this,'" + basicInfo.id + "')\">" +
        "<span class='input-group-btn'>" +
        " <button class='btn btn-default' onclick=\"addNum('" + basicInfo.id + "')\">+</button></span>" +
        "</div>"
    }
    val operateStr = "<button class='btn btn-danger' onclick=\"deleteId('" + basicInfo.id + "')\">移除</button>"
    Json.obj("id" -> basicInfo.id, "num" -> numStr, "operate" -> operateStr, "chinesename" -> basicInfo.chinesename,
      "price" -> basicInfo.price, "totalPrice" -> basicInfo.price * num)
  }

}
