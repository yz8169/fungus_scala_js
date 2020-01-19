package controllers

import java.io.File
import java.nio.file.Files

import com.drew.imaging.tiff.TiffReader
import com.sksamuel.scrimage.{Format, FormatDetector, Image}
import com.sksamuel.scrimage.nio.PngWriter
import com.sksamuel.scrimage.nio._
import javax.inject.Inject
import org.apache.commons.io.FileUtils
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.routing.JavaScriptReverseRouter
import tool.FormTool


/**
  * Created by yz on 2019/4/9
  */
class AppController @Inject()(cc: ControllerComponents, formTool: FormTool) extends AbstractController(cc) {

  def getImage = Action { implicit request =>
    val data = formTool.imageForm.bindFromRequest().get
    val sizeData = formTool.imageSizeForm.bindFromRequest().get
    val originalFile = new File(Utils.imageDir, s"${data.dir}/${data.fileName}")
    val file = if (sizeData.height.isEmpty) {
      originalFile
    } else {
      val height = sizeData.height.get
      val sizeDir = new File(Utils.dataDir, s"${Utils.imageDir.getName}_${height}/${data.dir}")
      Utils.createDir(sizeDir)
      val newFile = new File(sizeDir, data.fileName)
      val bytes = FileUtils.readFileToByteArray(originalFile)
      implicit val writer = JpegWriter()

//      val format = FormatDetector.detect(bytes)
//      if (format.isEmpty || format.get != Format.JPEG) {
//        Image.fromFile(originalFile).output(originalFile)
//      }

      if (!newFile.exists()) {
        Image.fromFile(originalFile).scaleToHeight(height).output(newFile)
      }
      newFile
    }
    val ifModifiedSinceStr = request.headers.get(IF_MODIFIED_SINCE)
    val lastModifiedStr = file.lastModified().toString
    val MimeType = "image/png"
    val byteArray = Files.readAllBytes(file.toPath)
    if (ifModifiedSinceStr.isDefined && ifModifiedSinceStr.get == lastModifiedStr) {
      NotModified
    } else {
      Ok(byteArray).as(MimeType).withHeaders(LAST_MODIFIED -> file.lastModified().toString)
    }

  }

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        controllers.routes.javascript.DataController.getAllDirNames,
        controllers.routes.javascript.DataController.imageDetailBefore,
        controllers.routes.javascript.DataController.getAllFiles,
        controllers.routes.javascript.DataController.getFileData,

        controllers.routes.javascript.BrowseController.getDetailInfo,

        controllers.routes.javascript.AppController.getImage,

      )
    ).as("text/javascript")
  }


}
