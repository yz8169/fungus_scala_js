package tool

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.format.Formats.doubleFormat


/**
  * Created by yz on 2018/7/25
  */

case class SubmissionData(name: String, email: String, subject: String, message: String)

case class HDGSearchData(chr: Option[String], start: Option[Int], end: Option[Int], min: Option[Double], max: Option[Double],
                         tissue: Option[String], platform: Option[String], species: Option[String])

class FormTool {

  case class FileNameData(fileName: String)

  val fileNameForm = Form(
    mapping(
      "fileName" -> text
    )(FileNameData.apply)(FileNameData.unapply)
  )

  case class GoData(kinds: Seq[String], species: Seq[String])

  val goForm = Form(
    mapping(
      "kinds" -> seq(text),
      "species" -> seq(text)
    )(GoData.apply)(GoData.unapply)
  )

  case class GroupData(geneId: String, groupName: String)

  val groupForm = Form(
    mapping(
      "geneId" -> text,
      "groupName" -> text
    )(GroupData.apply)(GroupData.unapply)
  )

  case class ProjectNameData(projectName: String)

  val projectNameForm = Form(
    mapping(
      "projectName" -> text
    )(ProjectNameData.apply)(ProjectNameData.unapply)
  )

  case class ExpV1Data(projectName: String, tissue: String)

  val expV1Form = Form(
    mapping(
      "projectName" -> text,
      "tissue" -> text
    )(ExpV1Data.apply)(ExpV1Data.unapply)
  )


  case class ExpData(projectName: String, ud: String, min: Option[Double], max: Option[Double],
                     pMin: Option[Double], pMax: Option[Double], comparisons: Seq[String]
                    )

  val expForm = Form(
    mapping(
      "projectName" -> text,
      "ud" -> text,
      "min" -> optional(of(doubleFormat)),
      "max" -> optional(of(doubleFormat)),
      "pMin" -> optional(of(doubleFormat)),
      "pMax" -> optional(of(doubleFormat)),
      "comparisons" -> seq(text)
    )(ExpData.apply)(ExpData.unapply)
  )

  case class KeggData(species: Seq[String])

  val keggForm = Form(
    mapping(
      "species" -> seq(text)
    )(KeggData.apply)(KeggData.unapply)
  )

  case class KeggEnrichData(geneId: Option[String], database: String, method: String, fdr: String, cutoff: String, pValue: String)

  val keggEnrichForm = Form(
    mapping(
      "geneId" -> optional(text),
      "database" -> text,
      "method" -> text,
      "fdr" -> text,
      "cutoff" -> text,
      "pValue" -> text
    )(KeggEnrichData.apply)(KeggEnrichData.unapply)
  )

  case class GoEnrichData(geneId: Option[String], database: String, twa: String, ewa: String)

  val goEnrichForm = Form(
    mapping(
      "geneId" -> optional(text),
      "database" -> text,
      "twa" -> text,
      "ewa" -> text
    )(GoEnrichData.apply)(GoEnrichData.unapply)
  )

  case class MuscleData(queryText: Option[String], tree: String)

  val muscleForm = Form(
    mapping(
      "queryText" -> optional(text),
      "tree" -> text
    )(MuscleData.apply)(MuscleData.unapply)
  )

  case class QueryData(queryText: Option[String], evalue: String, wordSize: String, maxTargetSeqs: String)

  val queryForm = Form(
    mapping(
      "queryText" -> optional(text),
      "evalue" -> text,
      "wordSize" -> text,
      "maxTargetSeqs" -> text
    )(QueryData.apply)(QueryData.unapply)
  )

  case class BlastpData(queryText: Option[String], evalue: String, wordSize: String, maxTargetSeqs: String)

  val blastpForm = Form(
    mapping(
      "queryText" -> optional(text),
      "evalue" -> text,
      "wordSize" -> text,
      "maxTargetSeqs" -> text
    )(BlastpData.apply)(BlastpData.unapply)
  )

  val submitForm = Form(
    mapping(
      "name" -> text,
      "email" -> text,
      "subject" -> text,
      "message" -> text
    )(SubmissionData.apply)(SubmissionData.unapply)
  )

  case class ImageData(dir: String, fileName: String)

  val imageForm = Form(
    mapping(
      "dir" -> text,
      "fileName" -> text
    )(ImageData.apply)(ImageData.unapply)
  )

  case class ImageSizeData(width: Option[Int], height: Option[Int])

  val imageSizeForm = Form(
    mapping(
      "width" -> optional(number),
      "height" -> optional(number)
    )(ImageSizeData.apply)(ImageSizeData.unapply)
  )

  case class IdData(id: String)

  val idForm = Form(
    mapping(
      "id" -> text
    )(IdData.apply)(IdData.unapply)
  )

  case class MummerData(targetText: Option[String], queryText: Option[String])

  val mummerForm = Form(
    mapping(
      "targetText" -> optional(text),
      "queryText" -> optional(text)
    )(MummerData.apply)(MummerData.unapply)
  )

  case class GeneIdData(geneId: String)

  val geneIdForm = Form(
    mapping(
      "geneId" -> text
    )(GeneIdData.apply)(GeneIdData.unapply)
  )

  case class KeywordData(keyword: String)

  val keywordForm = Form(
    mapping(
      "keyword" -> text
    )(KeywordData.apply)(KeywordData.unapply)
  )

  val hDGSearchForm = Form(
    mapping(
      "chr" -> optional(text),
      "start" -> optional(number),
      "end" -> optional(number),
      "min" -> optional(of(doubleFormat)),
      "max" -> optional(of(doubleFormat)),
      "tissue" -> optional(text),
      "platform" -> optional(text),
      "species" -> optional(text)
    )(HDGSearchData.apply)(HDGSearchData.unapply)
  )


}
