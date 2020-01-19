package controllers

import java.io.File
import java.nio.file.Files

import play.api.mvc._

/**
  * Created by yz on 2017/6/27.
  */
class TestController extends Controller {

  def test = Action {
    Ok(views.html.cn.test())
  }

  def getImage = Action {
    val MimeType = "imaage/png"
    val byteArray = Files.readAllBytes(new File("E:\\icons\\01.jpg").toPath)
    Ok(byteArray).as(MimeType)
  }


}