package dao

import javax.inject.Inject

import controllers.PhenotypeData
import play.api.db.slick._
import slick.jdbc.JdbcProfile
import models.Tables._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by yz on 2017/6/28.
  */
class BasicInfoDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._


  def selectAll: Future[Seq[BasicinfoRow]] = db.run(Basicinfo.result)

  def selectGetAllHabitat: Future[Seq[String]] = db.run(Basicinfo.map(_.habitat).distinct.result)

  def selectGetAllBHLevel: Future[Seq[String]] = db.run(Basicinfo.map(_.bhlevel).distinct.result)

  def selectGetAllIdByPhenotypeData(data: PhenotypeData): Future[Seq[String]] = db.run(Basicinfo.
    filter { x =>
      data.habitat match {
        case None => x.habitat === x.habitat
        case Some(y) => x.habitat === y.head
      }
    }.filter { x =>
    data.bHLevel match {
      case None => x.habitat === x.habitat
      case Some(y) => x.bhlevel === y.head
    }
  }.filter { x =>
    data.mCharacteristic match {
      case None => x.habitat === x.habitat
      case Some(y) => y.split("\\s+").map(x.mcharacteristic like "%" + _ + "%").foldLeft(x.habitat === x.habitat)(_ && _)
    }
  }.map(_.id).result)

  def selectGetIds: Future[Seq[String]] = db.run(Basicinfo.map(_.id).result)

  def insertAll(rows: Seq[BasicinfoRow]): Future[Unit] = {
    db.run(Basicinfo ++= rows).map(_ => ())
  }

  def insert(info: BasicinfoRow): Future[Unit] = {
    db.run(Basicinfo += info).map(_ => ())
  }

  def update(row: BasicinfoRow): Future[Unit] = {
    db.run(Basicinfo.filter(_.id === row.id).update(row)).map(_ => ())
  }

  def insertOrUpdate(info: BasicinfoRow): Future[Unit] = {
    db.run(Basicinfo.insertOrUpdate(info)).map(_ => ())
  }

  def selectById(id: String): Future[Option[BasicinfoRow]] = db.run(Basicinfo.filter(_.id === id).result.headOption)

  def deleteAll(): Future[Unit] = {
    db.run(Basicinfo.delete).map(_ => ())
  }

  def deleteByIds(ids: Seq[String]): Future[Unit] = {
    db.run(Basicinfo.filter(_.id.inSetBind(ids)).delete).map(_ => ())
  }

  def selectByIds(ids: Seq[String]): Future[Seq[BasicinfoRow]] = {
    db.run(Basicinfo.filter(_.id.inSetBind(ids)).result)
  }

  def selectAllInfos: Future[Seq[(BasicinfoRow, ClassifyRow)]] = {
    db.run(Basicinfo.join(Classify).on(_.id === _.id).result)
  }
}
