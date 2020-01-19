package controllers

import java.io.File
import java.nio.file.Files

import dao.{AppendixDao, BasicInfoDao, ClassifyDao}
import javax.inject.Inject
import org.apache.commons.io.FileUtils
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future
import scala.collection.JavaConverters._
import models.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import utils.Implicits._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


/**
  * Created by yz on 2018/6/28
  */
class DataInsertController @Inject()(basicInfoDao: BasicInfoDao, classifyDao: ClassifyDao,
                                     cc: ControllerComponents,appendixDao:AppendixDao) extends AbstractController(cc) {

  def insert = Action { implicit request =>
    val parent = new File("E:\\fungus\\test")
    val taxFile = new File(parent, "unite.its_fungi.tax")
    case class ClassifyData(phylum: String, outline: String, order: String, family: String)
    val taxMap = taxFile.lines.mapOtherByColumns { columns =>
      val kinds = columns(1).split(";")
      val genus = kinds(6).replaceAll("g__", "")
      val family = kinds(5).replaceAll("f__", "")
      val order = kinds(4).replaceAll("o__", "")
      val outline = kinds(3).replaceAll("c__", "")
      val phylum = kinds(2).replaceAll("p__", "")
      (genus, ClassifyData(phylum, outline, order, family))
    }.toMap
    val itsFile = new File(parent, "67个标准株ITS序列.fas")
    val itsMap = Utils.getSeqMap(itsFile).map { case (key, value) =>
      val newKey = key.split("-")(1).toUpperCase
      (newKey, value)
    }
    val basics = Utils.execFuture(basicInfoDao.selectAll)
    val dbItsMap = basics.map { x =>
      (x.id.toUpperCase, x.its)
    }.toMap
    val db18s = basics.map { x =>
      (x.id, x.eighteens)
    }.toMap
    val file = new File(parent, "菌种库资源-6-28 - 复件.txt")
    val lines = Utils.file2Lines(file)
    val headers = lines.headers
    val maps = lines.maps
    val ids = maps.map(x => x("常用标号"))
    val classifys = Utils.execFuture(classifyDao.selectAll)
    val classifyMap = taxMap ++ classifys.map(x => (x.genus.capitalize, ClassifyData(x.phylum, x.outline, x.order, x.family))).toMap ++ Map(
      "Eurotium" -> ClassifyData("Ascomycota", "Eurotiomycetes", "Eurotiales", "Aspergillaceae"),
      "Scopulariopsis" -> ClassifyData(phylum = "Ascomycota", outline = "Sordariomycetes", order = "Microascales", family = "Microascaceae"),
      "Streptomyces" -> ClassifyData(phylum = "Actinobacteria", outline = "Actinobacteria", order = "Streptomycetales", family = "Streptomycetaceae"),
      "Saprochaete" -> ClassifyData(phylum = "Ascomycota", outline = "Saccharomycetes", order = "Saccharomycetales", family = "Dipodascaceae"),
      "Actinoplanes" -> ClassifyData(phylum = "Actinobacteria", outline = "Actinobacteria", order = "Micromonosporales", family = "Micromonosporaceae"),
      "Mycocladus" -> ClassifyData(phylum = "Mucoromycota", outline = "Mucoromycetes ", order = "Mucorales", family = "Lichtheimiaceae"),
      "Chlamydoabsidia" -> ClassifyData(phylum = "Mucoromycota", outline = "Mucoromycetes ", order = "Mucorales", family = "Cunninghamellaceae"),
      "Neotestudina" -> ClassifyData(phylum = "Ascomycota", outline = "Dothideomycetes", order = "Pleosporales", family = "Testudinaceae"),
      "Microsphaeropsis" -> ClassifyData(phylum = "Ascomycota", outline = "Dothideomycetes", order = "Pleosporales", family = "Microsphaeropsis"),
      "Hormographiella" -> ClassifyData(phylum = "Basidiomycota", outline = "Agaricomycetes", order = "Agaricales", family = "Psathyrellaceae"),
      "Groenewaldozyma" -> ClassifyData(phylum = "Ascomycota", outline = "Saccharomycetes", order = "Saccharomycetales", family = "Trichomonascaceae"),
      "Nocardia" -> ClassifyData(phylum = "Actinobacteria", outline = "Actinobacteria", order = "Corynebacteriales", family = "Nocardiaceae"),
      "Nadsoniella" -> ClassifyData(phylum = "Ascomycota", outline = "Eurotiomycetes", order = "Chaetothyriales", family = "Herpotrichiellaceae"),
      "Phaeoannellomyces" -> ClassifyData(phylum = "Ascomycota", outline = "Eurotiomycetes", order = "Chaetothyriales", family = "Phaeoannellomyces"),
      "Conidiobolus" -> ClassifyData(phylum = "Zoopagomycota", outline = "Entomophthoromycetes", order = "Entomophthorales", family = "Ancylistaceae"),
      "Torulopsis" -> ClassifyData(phylum = "Ascomycota", outline = "Saccharomycetes", order = "Saccharomycetales", family = "Torulopsis"),
      "Hansenula" -> ClassifyData(phylum = "Ascomycota", outline = "Saccharomycetes", order = "Saccharomycetales", family = "Pichiaceae"),
      "Oedocephalum" -> ClassifyData(phylum = "Ascomycota", outline = "Pezizomycetes", order = "Pezizales", family = "Pezizaceae"),
      "Prototheca" -> ClassifyData(phylum = "Chlorophyta", outline = "Trebouxiophyceae", order = "Chlorellales", family = "Chlorellaceae"),
    )
    val basicMap = basics.map { row =>
      (row.chinesename, row.mcharacteristic)
    }.toMap
    var rows = maps.map { map =>
      val chineseName = map("中文菌名")
      val id = map("常用标号")
      val upperId = id.toUpperCase
      val its = if (itsMap.isDefinedAt(upperId)) {
        itsMap(upperId)
      } else if (dbItsMap.isDefinedAt(upperId)) dbItsMap(upperId) else "NA"
      val eighteenS = db18s.getOrElse(id, "NA")
      val basic = BasicinfoRow(id = map("常用标号"),
        originalid = map("信息化编号"),
        chinesename = map("中文菌名"),
        source = map("来源历史"),
        storetime = map("收藏时间"),
        opcountry = map("原产国"),
        habitat = map("分离基物"),
        ctemperature = map("培养温度"),
        cnumber = map("培养基名称归类编码"),
        mcharacteristic = basicMap.getOrElse(chineseName, "无"),
        pkind = map("保藏类型"),
        pmethod = map("保存方法"),
        bhlevel = map("生物危害程度"),
        pstate = map("实物状态"),
        price = 1,
        inventory = 999,
        its = its,
        eighteens = eighteenS,
        shareMode = map("共享方式")
      )
      val genus = map("英文属名").capitalize.trim
      val tmpGenus = if (genus.contains("(")) {
        genus.split("\\(")(0).trim
      } else if (genus.contains("（")) {
        genus.split("（")(0).trim
      } else genus
      val trueGenus = tmpGenus match {
        case "Gibberelle" => "Gibberella"
        case "Glioclsdium" => "Gliocladium"
        case "Mycoladus" => "Mycocladus"
        case "Rhizipus" => "Rhizopus"
        case "Microsphaerosis" => "Microsphaeropsis"
        case "Neosytalidium" => "Neoscytalidium"
        case "Bisfusarium" => "Fusarium"
        case "Histoplasm" => "Histoplasma"
        case "Coccidiodes" => "Coccidioides"
        case "Grophium" => "Graphium"
        case "Veronea" => "Veronaea"
        case "Setosphoeria" => "Setosphaeria"
        case "Malasssezia" => "Malassezia"
        case "Arthoderma" => "Arthroderma"
        case "Cylindrocurpon" => "Cylindrocarpon"
        case "Pototheca" => "Prototheca"
        case _ => tmpGenus
      }
      val dbClassify = classifyMap(trueGenus)
      val classify = ClassifyRow(
        id = map("常用标号"),
        phylum = dbClassify.phylum,
        outline = dbClassify.outline,
        order = dbClassify.order,
        family = dbClassify.family,
        genus = trueGenus,
        species = map("英文种名")
      )
      (basic, classify)
    }
    val f = basicInfoDao.selectGetIds.flatMap { x =>
      val deleteIds = x.intersect(ids)
      if (true) {
        basicInfoDao.deleteByIds(deleteIds).zip(classifyDao.deleteByIds(deleteIds))
      } else {
        rows = rows.filter(x => !deleteIds.contains(x._1.id))
        Future {}
      }
    }.flatMap { x =>
      val basicInfoFuture = basicInfoDao.insertAll(rows.map(_._1))
      val classifyFuture = classifyDao.insertAll(rows.map(_._2))
      basicInfoFuture.zip(classifyFuture)
    }
    Utils.execFuture(f)
    Ok("success!")
  }

  def insertAppendix = Action { implicit request =>
    val parent = new File("E:\\fungus\\test")
    val basics = Utils.execFuture(basicInfoDao.selectAll)
    val nameIdsMap=basics.map{x=>
      (x.chinesename,x.id)
    }.groupBy(_._1).mapValues(x=>x.map(_._2))
    val dir = new File(parent, "常见丝状真菌（44种）镜下照片")
    val dirs=dir.listFiles().filter{file=>
      val noImages=ArrayBuffer("交链链格孢","喙状凸脐孢")
      !noImages.contains(file.getName)
    }.foreach{imageDir=>
      val name=imageDir.getName
      val trueName= name match {
        case "Bastina 喙枝孢" =>"斑斯坦那喙枝孢"
        case "暗孢节菱孢" =>"暗孢节菱孢(球形阜孢霉)"
        case "伞枝横梗霉" =>"伞枝横梗霉（伞枝犁头霉）"
        case "奔马赭霉-100倍油镜" =>"奔马赭霉"
        case _=>name
      }
      val ids=nameIdsMap(trueName)
      ids.foreach{id=>
        imageDir.listFiles().foreach{file=>
          val idDir = new File(Utils.imagePath, id)
          FileUtils.copyFileToDirectory(file,idDir)
        }
        getRefreshImageFuture(id)
      }
    }
    Ok("success!")

  }

  def getRefreshImageFuture(id: String) = {
    val f=Future {
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
    Utils.execFuture(f)
  }

}
