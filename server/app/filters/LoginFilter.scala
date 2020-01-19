package filters

import javax.inject.Inject

import akka.stream.Materializer
import controllers.routes
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by yz on 2017/6/13.
  */
class LoginFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  override def apply(f: (RequestHeader) => Future[Result])(rh: RequestHeader): Future[Result] = {
    if (rh.session.get("phone").isEmpty && rh.path.contains("/admin") && !rh.path.contains("/assets/") &&
      !rh.path.contains("/login")) {
      Future.successful(Results.Redirect(routes.AdminController.loginBefore()).flashing("info"->"请先登录!"))
    } else {
      f(rh)
    }
  }
}
