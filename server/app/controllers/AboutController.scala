package controllers

import play.api.mvc._

class AboutController extends Controller{

  def toIndex = Action {
    Ok(views.html.cn.about.index())
  }
  def toIndex1 = Action {
    Ok(views.html.cn.about.index1())
  }
  def toIndex2 = Action {
    Ok(views.html.cn.about.index2())
  }

}
