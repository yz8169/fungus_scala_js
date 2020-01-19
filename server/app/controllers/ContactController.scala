package controllers

import play.api.mvc._

class ContactController extends Controller{

  def toIndex = Action {
    Ok(views.html.cn.contact.index())
  }



}
