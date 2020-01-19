package utils

import java.io.File

import controllers.Utils

import scala.collection.mutable

/**
  * Created by yz on 2019/3/29
  */
object Implicits {

  implicit class MyFile(val file: File) {

    def lines = Utils.file2Lines(file)

    def str = Utils.file2Str(file)

  }

  implicit class MyLines(val lines: mutable.Buffer[String]) {

    def filterByColumns(f: mutable.Buffer[String] => Boolean) = {
      lines.filter { line =>
        val columns = Utils.splitByTab(line)
        f(columns)
      }
    }

    def mapByColumns(n: Int, f: mutable.Buffer[String] => mutable.Buffer[String]): mutable.Buffer[String] = {
      lines.take(n) ++= lines.drop(n).map { line =>
        val columns = Utils.splitByTab(line)
        val newColumns = f(columns)
        newColumns.mkString("\t")
      }

    }

    def mapByColumns(f: mutable.Buffer[String] => mutable.Buffer[String]): mutable.Buffer[String] = {
      mapByColumns(0, f)
    }

    def mapOtherByColumns[T](f: mutable.Buffer[String] => T) = {
      lines.map { line =>
        val columns = Utils.splitByTab(line)
        f(columns)
      }

    }

    def headers = lines.head.split("\t")

    def maps=lines.drop(1).mapOtherByColumns{columns=>
      headers.zip(columns).toMap
    }


  }

}
