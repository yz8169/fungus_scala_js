package controllers

import java.io.{File, FileInputStream, FileOutputStream}
import java.net.URLEncoder
import java.nio.file.Files
import java.text.SimpleDateFormat

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.{Cell, CellType, DateUtil}
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import utils.Implicits._

/**
  * Created by yz on 2017/6/28.
  */
object Utils {
  val path = {
    val windowsPath = "E:\\database"
    val linuxPath = "/root/projects/play/database"
    if (new File(windowsPath).exists()) windowsPath else linuxPath
  }
  val imagePath = path + File.separator + "images"
  var error: String = _
  val sep = "__SEP__"
  val downloadDir = new File(path, "download")
  val dataDir = new File(path, "data")
  val imageDir = new File(Utils.dataDir, "常见丝状真菌（44种）镜下照片")

  def isInteger(value: String): Boolean = {
    try {
      value.toInt
    } catch {
      case _: Exception =>
        return false
    }
    true
  }

  def urlEncode(url: String) = {
    URLEncoder.encode(url, "UTF-8")
  }

  def getContentDisposition(url: String) = {
    val encodeUrl = urlEncode(url)
    s"attachment; filename*=utf-8''${encodeUrl}"
  }

  def getInfoByFile(file: File) = {
    val lines = FileUtils.readLines(file).asScala
    val columnNames = lines.head.split("\t")
    val array = lines.drop(1).map { line =>
      val columns = line.split("\t")
      columnNames.zip(columns).toMap
    }
    (columnNames, array)
  }

  def createDir(dir: File) = {
    if (!dir.exists()) {
      FileUtils.forceMkdir(dir)
    }

  }

  def isDouble(value: String): Boolean = {
    try {
      value.toDouble
    } catch {
      case _: Exception =>
        return false
    }
    true
  }

  def isIntegerP(value: String, p: Int => Boolean): Boolean = {
    try {
      val int = value.toInt
      if (!p(int)) return false
    } catch {
      case _: Exception =>
        return false
    }
    true
  }

  def isATCG(value: String): Boolean = {
    val ATCG = ArrayBuffer("A", "T", "C", "G", "N")
    if (value.toUpperCase != "NA" && value.exists(x => !ATCG.contains(x.toString))) {
      return false
    }
    true
  }

  def txt2Excel(txtFile: File, xlsxFile: File) = {
    val buffer = FileUtils.readLines(txtFile).asScala
    val outputWorkbook = new XSSFWorkbook
    val outputSheet = outputWorkbook.createSheet("Sheet1")
    buffer.zipWithIndex.foreach { case (line, index) =>
      val row = outputSheet.createRow(index)
      line.split("\t").zipWithIndex.foreach { case (column, i) =>
        val cell = row.createCell(i)
        cell.setCellValue(column)
      }
    }
    val fileOutputStream = new FileOutputStream(xlsxFile)
    outputWorkbook.write(fileOutputStream)
    fileOutputStream.close()
    outputWorkbook.close()
  }

  def xlsx2Lines(xlsxFile: File, sheetIndex: Int): ArrayBuffer[String] = {
    val is = new FileInputStream(xlsxFile.getAbsolutePath)
    val xssfWorkbook = new XSSFWorkbook(is)
    val xssfSheet = xssfWorkbook.getSheetAt(sheetIndex)
    val lines = ArrayBuffer[String]()
    for (i <- 0 to xssfSheet.getLastRowNum) {
      val xssfRow = xssfSheet.getRow(i)
      val columns = ArrayBuffer[String]()
      for (j <- 0 until xssfRow.getLastCellNum) {
        val cell = xssfRow.getCell(j)
        var value = ""
        if (cell != null) {
          cell.getCellType match {
            case CellType.STRING =>
              value = cell.getStringCellValue
            case CellType.NUMERIC =>
              if (DateUtil.isCellDateFormatted(cell)) {
                val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
                value = dateFormat.format(cell.getDateCellValue)
              } else {
                val doubleValue = cell.getNumericCellValue
                value = if (doubleValue == doubleValue.toInt) {
                  doubleValue.toInt.toString
                } else doubleValue.toString
              }
            case CellType.BLANK =>
              value = ""
            case _ =>
              value = ""
          }
        }

        columns += value.replaceAll("\n", " ")
      }
      val line = columns.mkString("\t")
      lines += line
    }
    xssfWorkbook.close()
    lines.filter(StringUtils.isNotBlank(_))
  }

  def xlsx2Lines(xlsxFile: File): ArrayBuffer[String] = {
    xlsx2Lines(xlsxFile, 0)
  }

  def file2Lines(file: File) = {
    FileUtils.readLines(file, "GBK").asScala
  }

  def file2Str(file: File) = {
    FileUtils.readFileToString(file)
  }

  def getSeqMap(file: File) = {
    val map = mutable.LinkedHashMap[String, String]()
    var key = ""
    file.lines.foreach { line =>
      if (line.startsWith(">")) {
        key = line
        map += (key -> "")
      } else if (key != "") {
        map(key) += line
      }
    }
    map

  }

  def splitByTab(str: String) = str.split("\t").toBuffer

  def xlsx2Txt(xlsxFile: File, txtFile: File, sheetIndex: Int = 0) = {
    val lines = xlsx2Lines(xlsxFile, sheetIndex)
    FileUtils.writeLines(txtFile, lines.asJava)
  }

  def execFuture[T](f: Future[T]): T = {
    Await.result(f, Duration.Inf)
  }

  def checkFile(txtFile: File): Boolean = {
    val buffer = FileUtils.readLines(txtFile).asScala.drop(1)

    val ids = buffer.map(_.split("\t")(0))
    val repeatid = ids.diff(ids.distinct).distinct.headOption
    repeatid match {
      case Some(x) => val nums = ids.zipWithIndex.filter(_._1 == x).map(_._2 + 2).mkString("(", "、", ")")
        Utils.error = "菌种编号:" + x + "在第" + nums + "行重复出现！"
        return false
      case None =>
    }

    for (i <- buffer.indices) {
      val columns = buffer(i).split("\t")
      if (columns.size != 24) {
        Utils.error = "数据文件第" + (i + 2) + "行列数不为24！"
        return false
      }
      for (j <- 0 until columns.size) {
        if (StringUtils.isBlank(columns(j))) {
          Utils.error = "数据文件第" + (i + 2) + "行第" + (j + 1) + "列为空！"
          return false
        }
        if (j == 20 && !Utils.isATCG(columns(j))) {
          println(columns(j))
          Utils.error = "数据文件第" + (i + 2) + "行第" + (j + 1) + "列不为基因序列片段！"
          return false
        }
        if (j == 21 && !Utils.isATCG(columns(j))) {
          val seq = columns(j).filter(_ != 'A').filter(_ != 'T').filter(_ != 'C').filter(_ != 'G')
          println(columns(j))
          println(seq)
          Utils.error = "数据文件第" + (i + 2) + "行第" + (j + 1) + "列不为基因序列片段！"
          return false
        }
        if (j == 22 && !Utils.isIntegerP(columns(j), _ > 0)) {
          Utils.error = "数据文件第" + (i + 2) + "行第" + (j + 1) + "列价格不为正整数！"
          return false
        }
        if (j == 23 && !Utils.isIntegerP(columns(j), _ >= 0)) {
          Utils.error = "数据文件第" + (i + 2) + "行第" + (j + 1) + "列库存不为非负整数！"
          return false
        }
      }
    }
    true
  }

}
