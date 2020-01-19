package controllers

import java.io.File
import java.nio.file.Files

import javax.inject.Inject
import play.api.mvc._
import play.utils.UriEncoding

import scala.concurrent.Future

class UtilController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def getImage(id: String, fileName: String) = Action { implicit request =>
    val ifModifiedSinceStr = request.headers.get(IF_MODIFIED_SINCE)
    val file = new File(Utils.imagePath, id + File.separator + fileName)
    val lastModifiedStr = file.lastModified().toString
    val MimeType = "image/png"
    val byteArray = Files.readAllBytes(file.toPath)
    if (ifModifiedSinceStr.isDefined && ifModifiedSinceStr.get == lastModifiedStr) {
      NotModified
    } else {
      Ok(byteArray).as(MimeType).withHeaders(LAST_MODIFIED -> file.lastModified().toString)
    }
  }


}
