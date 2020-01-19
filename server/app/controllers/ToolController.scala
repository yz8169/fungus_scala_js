package controllers

import java.io.File
import java.nio.file.Files
import javax.inject.Inject

import org.apache.commons.io.FileUtils
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._
import sys.process._
import dao._
import models.Tables._
import scala.concurrent.ExecutionContext.Implicits.global

class ToolController @Inject()(allInfoDao: AllInfoDao) extends Controller {

  def toEighteenSIndex = Action {
    Ok(views.html.cn.tool.eighteenSIndex())
  }

  def toItsIndex = Action {
    Ok(views.html.cn.tool.itsIndex())
  }

  case class SeqData(seq: String, evalue: String, wordSize: String, maxTargetSeqs: String)

  val seqForm = Form(
    mapping(
      "seq" -> text,
      "evalue" -> text,
      "wordSize" -> text,
      "maxTargetSeqs" -> text
    )(SeqData.apply)(SeqData.unapply)
  )

  def eighteenSQuery = Action.async { implicit request =>
    val data = seqForm.bindFromRequest.get
    val seqFile = Files.createTempFile("seq", ".fasta").toFile
    val seqs = ArrayBuffer(">query", data.seq)
    FileUtils.writeLines(seqFile, seqs.asJava)
    val blastFile = new File(Utils.path, "ncbi-blast-2.6.0+/bin/blastn")
    val dataFile = new File(Utils.path, "eighteenSSeq.fasta")
    val outFile = Files.createTempFile("data", ".fasta").toFile
    (blastFile.getAbsolutePath + " -query " + seqFile.getAbsolutePath + " -subject " + dataFile.getAbsolutePath +
      " -outfmt 6 -evalue " + data.evalue + " -max_target_seqs " + data.maxTargetSeqs + " -word_size " +
      data.wordSize + " -out " + outFile.getAbsolutePath).!
    val map = FileUtils.readLines(outFile).asScala.map(x => (x.split("\t")(1) -> x.split("\t")(10))).toMap
    allInfoDao.selectByIds(map.keys.toBuffer).map { x =>
      val array = x.map { y =>
        getObj(y, map(y._1.id))
      }
      Files.deleteIfExists(seqFile.toPath)
      Files.deleteIfExists(outFile.toPath)
      Ok(Json.toJson(array))
    }
  }

  def itsQuery = Action.async { implicit request =>
    val data = seqForm.bindFromRequest.get
    val seqFile = Files.createTempFile("seq", ".fasta").toFile
    val seqs = ArrayBuffer(">query", data.seq)
    FileUtils.writeLines(seqFile, seqs.asJava)
    val blastFile = new File(Utils.path, "ncbi-blast-2.6.0+/bin/blastn")
    val dataFile = new File(Utils.path, "itsSeq.fasta")
    val outFile = Files.createTempFile("data", ".fasta").toFile
    (blastFile.getAbsolutePath + " -query " + seqFile.getAbsolutePath + " -subject " + dataFile.getAbsolutePath +
      " -outfmt 6 -evalue " + data.evalue + " -max_target_seqs " + data.maxTargetSeqs + " -word_size " +
      data.wordSize + " -out " + outFile.getAbsolutePath).!
    val map = FileUtils.readLines(outFile).asScala.map(x => (x.split("\t")(1) -> x.split("\t")(10))).toMap
    allInfoDao.selectByIds(map.keys.toBuffer).map { x =>
      val array = x.map { y =>
        getObj(y, map(y._1.id))
      }
      Files.deleteIfExists(seqFile.toPath)
      Files.deleteIfExists(outFile.toPath)
      Ok(Json.toJson(array))
    }
  }

  def getObj(y: (BasicinfoRow, ClassifyRow), evalue: String) = {
    val basicInfo = y._1
    val classify = y._2
    val detailStr = "<a title='添加到购物车' onclick=\"addCart('" + basicInfo.id + "')\" style='cursor: pointer;'><span><em class='fa fa-plus' style='color:green;'></em></span></a>" +
      "&nbsp;&nbsp;<a title='详细信息' href='" +s"${routes.BrowseController.getDetailInfo}?id=${basicInfo.id}"+ "' target='_blank' style='cursor: pointer;'>" + basicInfo.id + "</a>"

    Json.obj("id" -> detailStr, "originalid" -> basicInfo.originalid, "chinesename" -> basicInfo.chinesename,
      "source" -> basicInfo.source, "storetime" -> basicInfo.storetime, "opcountry" -> basicInfo.opcountry,
      "habitat" -> basicInfo.habitat, "ctemperature" -> basicInfo.ctemperature, "cnumber" -> basicInfo.cnumber,
      "mcharacteristic" -> basicInfo.mcharacteristic, "pkind" -> basicInfo.pkind, "pmethod" -> basicInfo.pmethod,
      "bhlevel" -> basicInfo.bhlevel, "pstate" -> basicInfo.pstate, "phylum" -> classify.phylum,
      "outline" -> classify.outline, "order" -> classify.order, "family" -> classify.family,
      "genus" -> classify.genus, "species" -> classify.species, "evalue" -> evalue
    )
  }

}
