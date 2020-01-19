package dao

import javax.inject.Inject

import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by yz on 2017/7/3.
  */
class AppendixDao  @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def selectAll: Future[Seq[AppendixRow]] = db.run(Appendix.result)

  def dropTable(): Future[Unit] = {
    val sqlu = DBIO.seq(
      sqlu"""
          DROP TABLE IF EXISTS appendix;
        """)
    db.run(sqlu).map(_ => ())
  }

  def insertAll(rows: Seq[AppendixRow]): Future[Unit] = {
    db.run(Appendix ++= rows).map(_ => ())
  }

  def insert(row: AppendixRow): Future[Unit] = {
    db.run(Appendix += row).map(_ => ())
  }

  def deleteById(id: String): Future[Unit] = {
    db.run(Appendix.filter(_.id === id).delete).map(_ => ())
  }

  def deleteByIds(ids: Seq[String]): Future[Unit] = {
    db.run(Appendix.filter(_.id.inSetBind(ids)).delete).map(_ => ())
  }

  def selectById(id: String): Future[Option[AppendixRow]] = db.run(Appendix.filter(_.id === id).result.headOption)

  def insertOrUpdate(row: AppendixRow): Future[Unit] = {
    db.run(Appendix.insertOrUpdate(row)).map(_ => ())
  }

}
