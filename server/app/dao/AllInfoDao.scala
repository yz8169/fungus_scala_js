package dao

import javax.inject.Inject

import controllers.routes
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

/**
  * Created by yz on 2017/7/12.
  */
class AllInfoDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def selectAll: Future[Seq[(BasicinfoRow, ClassifyRow)]] = {
    val q = for {
      (b, c) <- Basicinfo join Classify on (_.id === _.id)
    } yield (b, c)
    db.run(q.result)
  }

  def selectByIds(ids: Seq[String]): Future[Seq[(BasicinfoRow, ClassifyRow)]] = {
    db.run(Basicinfo.join(Classify).on(_.id === _.id).filter(_._1.id.inSetBind(ids)).result)
  }

}
