package test

import java.io.File

import controllers.Utils
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import utils.ImageUtils

import scala.annotation.tailrec
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.math._
import scala.util.Random
import scala.collection.JavaConverters._
import sys.process._
import utils.Implicits._

import scala.collection.mutable

object Test {
  def main(args: Array[String]): Unit = {
    val startTime = System.currentTimeMillis()


    val endTime = System.currentTimeMillis()
    println((endTime - startTime) / 1000.0)

  }

  def test3={
    val file=new File(Utils.imagePath)
    file.listFiles().foreach{dir=>
      dir.listFiles().foreach{imageFile=>
        ImageUtils.convert(imageFile.getAbsolutePath,"jpg",imageFile.getAbsolutePath)
      }
    }
  }

  def test1={
    val xlsxFile=new File("E:\\testData\\out1.xlsx")
    val file=new File("E:\\testData\\out2.txt")
    Utils.xlsx2Txt(xlsxFile,file)
    val taxFile=new File("E:\\testData\\silva.18s_eukaryota.tax")
    val taxLines=FileUtils.readLines(taxFile,"UTF-8").asScala
    val lines=FileUtils.readLines(file,"UTF-8").asScala
    val newLines=ArrayBuffer(lines.head+"\t"+"18s序列\t价格\t库存")
    val faFile=new File("E:\\testData\\silva.18s_eukaryota.fasta")
    val faLines=FileUtils.readLines(faFile).asScala
    val names=faLines.filter(_.startsWith(">"))
    val seqs=faLines.filter(! _.startsWith(">"))
    val map=names.zip(seqs).toMap
    lines.drop(1).foreach{line=>
      val columns=line.split("\t")
      val newColumns=columns.take(21).map(x=>if(StringUtils.isBlank(x)) "NA" else x).toBuffer
      val genus=columns(19)
      if(taxLines.filter(_.contains(genus)).size>0){
        val id=taxLines.filter(_.contains(genus)).head.split("\t")(0)
        newColumns++=ArrayBuffer(map.getOrElse(">"+id,"NA"))
      }else{
        newColumns++=ArrayBuffer("NA")
      }
      newColumns++=ArrayBuffer("1","999")
      newColumns(20)=if(newColumns(20)!="NA") newColumns(20).map{x=>
        if(x!='A' && x!='C' && x!='T' && x!='G') 'N' else x
      } else "NA"
      newColumns(21)=if(newColumns(21)!="NA") newColumns(21).map{x=>
        if(x!='A' && x!='C' && x!='T' && x !='G') 'N' else x
      } else "NA"
      newLines+=newColumns.mkString("\t")
    }
    FileUtils.writeLines(new File("E:\\testData\\out3.txt"),newLines.asJava)
    val txtFile=new File("E:\\testData\\out3.txt")

    Utils.txt2Excel(txtFile,new File("E:\\testData\\out2.xlsx"))
  }

  def test={
    val startTime = System.currentTimeMillis()
    val xlsxFile=new File("E:\\testData\\菌种库资源-6-28.xlsx")
    val file=new File("E:\\testData\\菌种库资源-6-28.txt")
    Utils.xlsx2Txt(xlsxFile,file)
    val taxFile=new File("E:\\testData\\unite.its_fungi.tax")
    val taxLines=FileUtils.readLines(taxFile,"UTF-8").asScala
    val lines=FileUtils.readLines(file,"UTF-8").asScala
    val headers=lines.head.split("\t").toBuffer
    val newHeaders=headers.clone()
    newHeaders.insert(4,"门名","纲名","目名","科名")
    val newLines=ArrayBuffer(newHeaders.mkString("\t"))
    lines.drop(1).foreach{line=>
      val columns=line.split("\t").toBuffer
      val newColumns=columns.clone()
      val genus=columns(4)
      if(taxLines.filter(_.contains("g__"+genus)).size>0){
        val txInfo=taxLines.filter(_.contains("g__"+genus)).head.split("\t")(1)
        val phy=txInfo.split(";").filter(_.contains("p__")).head.replaceAll("p__","")
        val outline=txInfo.split(";").filter(_.contains("c__")).head.replaceAll("c__","")
        val order=txInfo.split(";").filter(_.contains("o__")).head.replaceAll("o__","")
        val family=txInfo.split(";").filter(_.contains("f__")).head.replaceAll("f__","")
        newColumns.insertAll(4,ArrayBuffer(phy,outline,order,family))
        newLines+=newColumns.mkString("\t")
      }

    }
    val txtFile=new File("E:\\testData\\out.txt")
    FileUtils.writeLines(txtFile,newLines.asJava)

    Utils.txt2Excel(txtFile,new File("E:\\testData\\out.xlsx"))

    val endTime = System.currentTimeMillis()
    println((endTime - startTime) / 1000.0)
  }
}


