package myJs

import myJs.Utils._
import org.querki.jquery._
import org.scalajs.dom.Element

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

/**
  * Created by yz on 2019/4/9
  */
@JSExportTopLevel("EighteenS")
object EighteenS {

  @JSExport("init")
  def init = {


  }

  @JSExport("showExample")
  def showExample(y: Element, name: String) = {
    val example =
      s"""
         |TCTGGTTGATTCTGCCAGTAGTCATATGCTTGTCTCAAAGATTAAGCCATGCATGTCTAAGTATAAGCACTCTTTTACTGTGAAACTGCGAATGGCTCATTAAATCAGTTATCGTTTATTTGATAGTACCCTACTACATGGATACCTGTGGTAATTCTAGAGCTAATACATGCGCAAAACCCCGACTTCGGAAGGGGTGTATTTATTAGATAAAAAACCAATGCCCTTCGGGGCTCCTTGGTGATTCATAATAACTTCACGAATCGCATGGCCTTGCGCCGGCGATGGTTCATTCAAATTTCTGCCCTATCAACTTTCGATGGTAGGATAGTGGCCTACCATGGTGGCAACGGGTAACGGGGAATTAGGGTTCGATTCCGGAGAGGGAGCCTGAGAAACGGCTACCACATCCAAGGAAGGCAGCAGGCGCGCAAATTACCCAATCCCGATACGGGGAGGTAGTGACAATAAATACTGATACAGGGCTCTTTTGGGTCTTGTAATTGGAATGAGAACAATTTAAATCCCTTAACGAGGAACAATTGGAGGGCAAGTCTGGTGCCAGCAGCCGCGGTAATTCCAGCTCCAATAGCGTATATTAAAGTTGTTGCAGTTAAAAAGCTCGTAGTTGAACCTTGGGCCCGTCCTGCCGGTCCGCCTCACCGCGAGTACTGGTCCGGATGGGCCTTTCTTTCTGGGGAATCCCATGGCCTTCACTGGCTGTGGCGGGGAACCAGGACTTTTACTGTGAAAAAATTAGAGTGTTCAAAGCAGGCCTTTGCTCGGATACATTAGCATGGAATAATAGAATAGGACGTGCGGTTCTATTTTGTTGGTTTCTAGGACCGCCGTAATGATTAATAGGGATAGTCGGGGGCGTCAGTATTCAGCTGTCAGAGGTGAAATTCTTGGATTTGCTGAAGACTAACTACTGCGAAAGCATTCGCCAAGGATGTTTTCATTAATCAGGGAACGAAAGTTAGGGGATCGAAGACGATCAGATACCGTCGTAGTCTTAACCATAAACTATGCCGACTAGGGATCGGGCGGGGTTTCTATGATGACCCGCTCGGCACCTTACGAGAAATCAAAGTTTTTGGGTTCTGGGGGGAGTATGGTCGCAAGGCTGAAACTTAAAGAAATTGACGGAAGGGCACCACAAGGCGTGGAGCCTGCGGCTTAATTTGACTCAACACGGGGAAACTCACCAGGTCCAGACAAAATAAGGATTGACAGATTGAGAGCTCTTTCTTGATCTTTTGGATGGTGGTGCATGGCCGTTCTTAGTTGGTGGAGTGATTTGTCTGCTTAATTGCGATAACGAACGAGACCTCGGCCCTTAAATAGCCCGGTCCGCGTTTGCGGGCCGCTGGCTTCTTAGGGGGACTATCGGCTCAAGCCGATGGAAGTACGTGGCAATAACAGGTCTGTGATGCCCTTAGATGTTCTGGGCCGCACGCGCGCTACACTGACAGGGCCAGCGAGTACATCACCTTGGCCGAGAGGTCTGGGTAATCTTGTTAAACCCTGTCGTGCTGGGGATAGAGCATTGCAATTATTGCTCTTCAACGAGGAATGCCTAGTAGGCACGAGTCATCAGCTCGTGCCGATTACGTCCCTGCCCTTTGTACACACCGCCCGTCGCTACTACCGATTGAATGGCTCAGTGAGGCCTTCGGACTGGCTCAGAGGGGTTGGCAACGACCGCTCAGAGCCGGAAAGTTGGTCAAACTTGGTCATTTAGAGGAAGTAAAAGTCGTAACAAGGTTTCCGTAGGTGAACCTGCGGAAG
       """.stripMargin
    $(s"textarea[name='${name}']").`val`(example.trim)
    g.$(s"#form").formValidation("revalidateField", name)
  }


}