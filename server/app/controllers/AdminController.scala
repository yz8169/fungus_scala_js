package controllers

import java.io.File
import java.nio.file.Files

import javax.inject.Inject
import dao._
import models.Tables._
import org.apache.commons.io.FileUtils
import play.api.data.Forms._
import play.api.data._
import play.api.libs.json.Json
import play.api.mvc._
import tool.FormTool
import utils.ImageUtils

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by yz on 2017/6/27.
  */

class AdminController @Inject()(basicInfoDao: BasicInfoDao, classifyDao: ClassifyDao, submissionDao: SubmissionDao,
                                appendixDao: AppendixDao, accountDao: AccountDao,formTool:FormTool,
                                cc: ControllerComponents) extends AbstractController(cc) {

  def loginBefore = Action { implicit request =>
    Ok(views.html.cn.admin.login())
  }


  def login(phone: String, password: String) = Action.async { implicit request =>
    accountDao.selectById1.map { x =>
      if (phone == x.account && password == x.password) {
        Redirect(routes.AdminController.manageData()).withSession("phone" -> phone)
      } else {
        Redirect(routes.AdminController.loginBefore()).flashing("info" -> "账号或密码错误!")
      }
    }
  }

  def logout = Action {
    Redirect(routes.AdminController.loginBefore()).flashing("info" -> "退出登录成功!").withNewSession
  }

  def addByFile = Action.async(parse.multipartFormData) { implicit request =>
    val file = request.body.file("file").get
    val tmpXlsxFile = Files.createTempFile("tmp", ".xlsx").toFile
    val tmpTxtFile = Files.createTempFile("tmp", ".txt").toFile
    file.ref.moveTo(tmpXlsxFile, true)
    val checkBox = request.body.dataParts.get("isUpdate") match {
      case Some(x) => true
      case None => false
    }
    Utils.xlsx2Txt(tmpXlsxFile, tmpTxtFile)
    if (!Utils.checkFile(tmpTxtFile)) {
      Future.successful(Ok(Json.obj("error" -> Utils.error)))
    } else {
      val buffer = FileUtils.readLines(tmpTxtFile).asScala.drop(1)
      val ids = buffer.map(_.split("\t")(0))
      var rows = buffer.map { x =>
        val columns = x.split("\t")
        (BasicinfoRow(columns(0), columns(1), columns(2), columns(3), columns(4), columns(5), columns(6), columns(7),
          columns(8), columns(9), columns(10), columns(11), columns(12), columns(13), columns(20), columns(21),
          columns(22).toInt, columns(23).toInt,columns(24)), ClassifyRow(columns(0), columns(14), columns(15), columns(16),
          columns(17), columns(18), columns(19)))
      }

      basicInfoDao.selectGetIds.flatMap { x =>
        val deleteIds = x.intersect(ids)
        if (checkBox) {
          basicInfoDao.deleteByIds(deleteIds).zip(classifyDao.deleteByIds(deleteIds))
        } else {
          rows = rows.filter(x => !deleteIds.contains(x._1.id))
          Future {}
        }
      }.flatMap { x =>
        val basicInfoFuture = basicInfoDao.insertAll(rows.map(_._1))
        val classifyFuture = classifyDao.insertAll(rows.map(_._2))
        basicInfoFuture.zip(classifyFuture)
      }.map { _ =>
        Files.deleteIfExists(tmpXlsxFile.toPath)
        Files.deleteIfExists(tmpTxtFile.toPath)
        Ok(Json.toJson("success!"))
      }
    }
  }

  def addByFileBefore = Action {
    Ok(views.html.cn.admin.addByFile())
  }

  def addByHandBefore = Action { implicit request =>
    Ok(views.html.cn.admin.addByHand())
  }

  def changePasswordBefore = Action { implicit request =>
    Ok(views.html.cn.admin.changePassword())
  }

  case class ChangePasswordData(account: String, password: String, newPassword: String)

  val changePasswordForm = Form(
    mapping(
      "account" -> text,
      "password" -> text,
      "newPassword" -> text
    )(ChangePasswordData.apply)(ChangePasswordData.unapply)
  )

  def changePassword = Action.async { implicit request =>
    val data = changePasswordForm.bindFromRequest().get
    accountDao.selectById1.flatMap { x =>
      if (data.account == x.account && data.password == x.password) {
        val row = AccountRow(x.id, x.account, data.newPassword)
        accountDao.update(row).map { y =>
          Redirect(routes.AdminController.loginBefore()).flashing("info" -> "密码修改成功!").withNewSession
        }
      } else {
        Future.successful(Redirect(routes.AdminController.changePasswordBefore()).flashing("info" -> "账号或密码错误!"))
      }
    }
  }


  val basicInfoForm = Form(
    mapping(
      "id" -> text,
      "originalId" -> text,
      "chineseName" -> text,
      "source" -> text,
      "storeTime" -> text,
      "oPCountry" -> text,
      "habitat" -> text,
      "cTemperature" -> text,
      "cNumber" -> text,
      "mCharacteristic" -> text,
      "pKind" -> text,
      "pMethod" -> text,
      "bHLevel" -> text,
      "pState" -> text,
      "its" -> text,
      "eighteenS" -> text,
      "price" -> number,
      "inventory" -> number,
      "shareMode" -> text
    )(BasicinfoRow.apply)(BasicinfoRow.unapply)
  )

  val classifyForm = Form(
    mapping(
      "id" -> text,
      "phylum" -> text,
      "outline" -> text,
      "order" -> text,
      "family" -> text,
      "genus" -> text,
      "species" -> text
    )(ClassifyRow.apply)(ClassifyRow.unapply)
  )

  def addByHand = Action.async { implicit request =>
    val basicInfo = basicInfoForm.bindFromRequest.get
    val classify = classifyForm.bindFromRequest.get
    DaoUtils.addRow(basicInfo, classify).map { _ =>
      val info = "菌种信息： " + basicInfo.id + " 新增成功！"
      Redirect(routes.AdminController.addByHandBefore()).flashing("info" -> info)
    }
  }

  def updateAllInfo = Action.async { implicit request =>
    val basicInfo = basicInfoForm.bindFromRequest.get
    val classify = classifyForm.bindFromRequest.get
    DaoUtils.updateRow(basicInfo, classify).map(_ => Ok(Json.toJson("success")))
  }

  def updateItsDatabase = Action.async { implicit request =>
    basicInfoDao.selectAll.map { x =>
      val itsArray = x.filter(_.its != "NA").flatMap(y => ArrayBuffer(">" + y.id, y.its))
      val eighteenSArray = x.filter(_.eighteens != "NA").flatMap(y => ArrayBuffer(">" + y.id, y.eighteens))
      FileUtils.writeLines(new File(Utils.path, "itsSeq.fasta"), itsArray.asJava)
      FileUtils.writeLines(new File(Utils.path, "eighteenSSeq.fasta"), eighteenSArray.asJava)
      Ok(Json.toJson("success"))
    }
  }

  def manageData = Action { implicit request =>
    Ok(views.html.cn.admin.manageData())
  }

  def manageSubmission = Action { implicit request =>
    Ok(views.html.cn.admin.manageSubmission())
  }

  def searchAllBasicInfos = Action.async { implicit request =>
    basicInfoDao.selectAll.map { x =>
      val array = x.map { y =>
        getArrayByBasicInfo(y)
      }
      Ok(Json.toJson(array))
    }
  }

  def searchAllSubmission = Action.async { implicit request =>
    submissionDao.selectAll.map { x =>
      val array = x.map { y =>
        getObjBySubmission(y)
      }
      Ok(Json.toJson(array))
    }
  }

  def getObjBySubmission(y: SubmissionRow) = {
    val operateStr = if (y.isdeal == 0) {
      "<a title='处理完成' onclick=\"deal('" + y.id + "')\" style='cursor: pointer;'><span><em class='fa fa-check'></em></span></a>"
    } else "已处理"

    Json.obj("name" -> y.name, "department" -> y.department, "phone" -> y.phone, "email" -> y.email,
      "content" -> y.content, "time" -> y.time.toString("yyyy-MM-dd HH:mm:ss"), "address" -> y.address,
      "totalPrice" -> y.totalprice, "operate" -> operateStr)
  }

  def deal(id: Int) = Action.async {
    submissionDao.selectById(id).flatMap { x =>
      val row = x.copy(isdeal = 1)
      submissionDao.update(row)
    }.flatMap(x => submissionDao.selectAll).map { x =>
      val array = x.map { y =>
        getObjBySubmission(y)
      }
      Ok(Json.toJson(array))
    }
  }


  case class IdData(id: String)

  val idForm = Form(
    mapping(
      "id" -> text
    )(IdData.apply)(IdData.unapply)
  )

  def deleteAllInfos = Action.async { implicit request =>
    val id = idForm.bindFromRequest.get.id
    val ids = id.split(Utils.sep)
    DaoUtils.deleteRows(ids).flatMap(_ => basicInfoDao.selectAll).map { x =>
      val array = x.map { y =>
        getArrayByBasicInfo(y)
      }
      Ok(Json.toJson(array))
    }
  }


  def updateAllInfoBefore(id: String) = Action.async { implicit request =>
    basicInfoDao.selectById(id).map(_.get).zip(classifyDao.selectById(id).map(_.get)).map { x =>
      Ok(views.html.cn.admin.update(x))
    }
  }

  def addAppendixBefore(id: String) = Action { implicit request =>
    Ok(views.html.cn.admin.addAppendix(id))
  }

  def deleteAppendixBefore(id: String) = Action { implicit request =>
    Ok(views.html.cn.admin.deleteAppendix(id))
  }

  def viewAppendixBefore(id: String) = Action { implicit request =>
    Ok(views.html.cn.admin.viewAppendix(id))
  }

  def getAllFiles(id: String) = Action { implicit request =>
    val json = jsonByCopyFiles(id)
    Ok(json)
  }

  def imageConvert = Action {
    val file = new File(Utils.imagePath)
    file.listFiles().foreach { dir =>
      dir.listFiles().foreach { imageFile =>
        try {
          ImageUtils.convert(imageFile.getAbsolutePath, "jpg", imageFile.getAbsolutePath)
        } catch {
          case _ =>
        }

      }
    }
    Ok("success!")
  }


  def jsonByCopyFiles(id: String) = {
    val src = new File(Utils.imagePath, id)
    val files = if (!src.exists()) Array[String]() else {
      src.listFiles().map(_.getName)
    }
    Json.obj("id" -> id, "files" -> files)
  }

  def deleteFile(id: String, fileName: String) = Action.async { implicit request =>
    val file = new File(Utils.imagePath, id + File.separator + fileName)
    FileUtils.deleteQuietly(file)
    val json = jsonByCopyFiles(id)
    getRefreshImageFuture(id).map(x => Ok(json))
  }

  def getRefreshImageFuture(id: String) = {
    Future {
      id
    }.map { x =>
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

  def addAppendix(id: String) = Action.async(parse.multipartFormData) { implicit request =>
    val file = request.body.file("file").get
    val finalFile = new File(Utils.imagePath, id + File.separator + file.filename)
    FileUtils.writeStringToFile(finalFile, "")
    file.ref.moveTo(finalFile, true)
    getRefreshImageFuture(id).map(x => Ok(Json.toJson("success")))
  }

  def getArrayByBasicInfo(y: BasicinfoRow) = {
    val deleteStr = "<a title='删除' onclick=\"deleteAllInfo('" + y.id + "')\" style='cursor: pointer;'><span><em class='fa fa-close'></em></span></a>"
    val updateStr = "<a title='修改' href='" + routes.AdminController.updateAllInfoBefore(y.id) + "' target='_blank' style='cursor: pointer;'><span><em class='fa fa-edit'></em></span></a>"
    val detailStr = "<a title='详细信息' href='" + s"${routes.AdminController.getDetailInfo}?id=${y.id}" + "' target='_blank' style='cursor: pointer;'><span><em class='fa fa-eye'></em></span></a>"
    val addStr = "<a title='新增' href='" + routes.AdminController.addAppendixBefore(y.id) + "' target='_blank' style='cursor: pointer;'><span><em class='fa fa-plus'></em></span></a>"
    val removeStr = "<a title='删除' href='" + routes.AdminController.deleteAppendixBefore(y.id) + "' target='_blank' style='cursor: pointer;'><span><em class='fa fa-remove'></em></span></a>"
    val viewStr = "<a title='查看' href='" + routes.AdminController.viewAppendixBefore(y.id) + "' target='_blank' style='cursor: pointer;'><span><em class='fa fa-eye'></em></span></a>"
    Json.obj("id" -> y.id, "originalid" -> y.originalid, "chinesename" -> y.chinesename, "source" -> y.source,
      "opcountry" -> y.opcountry, "operate" -> (deleteStr + "&nbsp;" + updateStr + "&nbsp;" + detailStr), "aOperate" -> (addStr + "&nbsp;" + removeStr + "&nbsp;" + viewStr))
  }

  def getDetailInfo = Action.async { implicit request =>
    val data=formTool.idForm.bindFromRequest().get
    val id=data.id
    basicInfoDao.selectById(id).map(_.get).zip(classifyDao.selectById(id).map(_.get)).zip(
      getRefreshImageFuture(id).flatMap(_ => appendixDao.selectById(id).map(_.get))).
      map { x =>
        Ok(views.html.cn.admin.detailInfo((x._1._1, x._1._2, x._2)))
      }
  }

  def idCheck(id: String) = Action.async { implicit request =>
    basicInfoDao.selectById(id).map {
      case Some(y) => Ok(Json.obj("valid" -> false))
      case None => Ok(Json.obj("valid" -> true))
    }
  }

  def downloadFile() = Action.async { implicit request =>
    val header = Array("﻿菌种编号", "原始编号", "中文名称", "来源历史", "收藏时间", "原产国", "分离基物", "培养温度（℃）",
      "培养基编号（名称或配方）", "主要特征特性", "保藏类型", "保存方法", "生物危害程度", "实物状态", "门名", "纲名", "目名",
      "科名", "属名", "种名加词", "ITS序列", "18S序列", "价格", "库存")
    val buffer = ArrayBuffer[String]()
    buffer += header.mkString("\t")
    basicInfoDao.selectAllInfos.map { x =>
      buffer ++= x.map {
        case (basicInfo, classfiy) =>
          Array(basicInfo.id, basicInfo.originalid, basicInfo.chinesename, basicInfo.source, basicInfo.storetime,
            basicInfo.opcountry, basicInfo.habitat, basicInfo.ctemperature, basicInfo.cnumber, basicInfo.mcharacteristic,
            basicInfo.pkind, basicInfo.pmethod, basicInfo.bhlevel, basicInfo.pstate, classfiy.phylum, classfiy.outline,
            classfiy.order, classfiy.family, classfiy.genus, classfiy.species, basicInfo.its, basicInfo.eighteens,
            basicInfo.price, basicInfo.inventory).mkString("\t")
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

  def toIndex = Action {
    Redirect(routes.AdminController.loginBefore())
  }

  object DaoUtils {

    def addRow(basicInfo: BasicinfoRow, classify: ClassifyRow) = {
      basicInfoDao.insert(basicInfo).zip(classifyDao.insert(classify))
    }

    def deleteRows(ids: Seq[String]) = {
      basicInfoDao.deleteByIds(ids).zip(classifyDao.deleteByIds(ids)).
        zip(appendixDao.deleteByIds(ids).map { _ =>
          ids.foreach { x =>
            val file = new File(Utils.imagePath, x)
            FileUtils.deleteQuietly(file)
          }
        })
    }

    def updateRow(basicInfo: BasicinfoRow, classify: ClassifyRow) = {
      basicInfoDao.update(basicInfo).zip(classifyDao.update(classify))
    }
  }

}
