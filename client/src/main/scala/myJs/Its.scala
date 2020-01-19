package myJs

import myJs.Utils._
import myJs.myPkg.Implicits._
import myJs.myPkg._
import org.querki.jquery._
import org.scalajs.dom.Element

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

/**
  * Created by yz on 2019/4/9
  */
@JSExportTopLevel("Its")
object Its {

  @JSExport("init")
  def init = {


  }

  @JSExport("showExample")
  def showExample(y: Element, name: String) = {
    val example =
      s"""
         |CAGAGTTCATGCCCGAAAGGGTAGACCTCCCACCCTTGTGTATTATTACTTTGTTGCTTTGGCGAGCTGCTCTTCGGGGCCTTGTATGCTCGCCAGAGAATATCAAAACTCTTTTTATTAATGTCGTCTGAGTACTATATAATAGTTAAAACTTTCAACAACGGATCTCTTGGTTCTGGCATCGATGAAGAACGCAGCGAGATGCGATAAGTAATGTGAATTGCAGAATTCAGTGAATCATCGAATCTTTGAACGCACATTGCGCCCCTTGGTATTCCGGGGGGCATGCCTGTTCGAGCGTCATTTCAACCCTCAAGCTCAGCTTGGTATTGAGTCCATGTCAGTAATGGCAGGCTCTAAAATCAGTGGCGGCGCCGCTGGGTCCTGAACGTAGTAATATCTCTCGTTACAGGTTCTCGGTGTGCTTCTGCCAAAACCCAAATTTTCTATG
       """.stripMargin
    $(s"textarea[name='${name}']").`val`(example.trim)
    g.$(s"#form").formValidation("revalidateField", name)
  }


}
