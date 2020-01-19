package myJs

import myJs.myPkg.LayerOptions
import scalatags.Text.all._

import scala.scalajs.js


/**
  * Created by yz on 2019/4/10
  */
object Tool {

  val zhInfo="信息"
  val layerOptions = LayerOptions.title(zhInfo).closeBtn(0).skin("layui-layer-molv").btn(js.Array())

  val zhRunning="正在运行"
  val element = div(id:="content")(
    span(id:="info")(zhRunning),
    " ",
    img(src := "/assets/images/running2.gif", width := 30, height := 20,cls:="runningImage")
  ).render


}
