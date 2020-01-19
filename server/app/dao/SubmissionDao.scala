package dao

import javax.inject.Inject

import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SubmissionDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insert(row: SubmissionRow): Future[Unit] = {
    db.run(Submission += row).map(_ => ())
  }

  def selectById(id: Int): Future[SubmissionRow] = db.run(Submission.filter(_.id === id).result.head)


  def update(row: SubmissionRow): Future[Unit] = {
    db.run(Submission.filter(_.id === row.id).update(row)).map(_ => ())
  }

  def selectAll: Future[Seq[SubmissionRow]] = db.run(Submission.result)


}
