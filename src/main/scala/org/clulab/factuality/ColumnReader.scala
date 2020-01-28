package org.clulab.factuality

import org.clulab.factuality.utils.Serializer

import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.util.Try

/**
  * Reads the CoNLL-like column format
  */
object ColumnReader {
  def readColumnsFromFile(fn: String): Array[Array[Row]] =
      Serializer.using(Source.fromFile(fn, "UTF-8")) { source =>
        readColumns(source)
      }

  def readColumnsFromResource(fn: String): Array[Array[Row]] =
      Serializer.using(Source.fromResource(fn)) { source =>
        readColumns(source)
      }

  def readColumns(fn: String): Array[Array[Row]] = {
    val result1 = Try(readColumnsFromFile(fn))
    val result2 = result1.orElse(Try(readColumnsFromResource(fn)))

    result2.get
  }

  def readColumns(source: Source): Array[Array[Row]] = {
    var sentence = new ArrayBuffer[Row]()
    val sentences = new ArrayBuffer[Array[Row]]()
    for (line <- source.getLines()) {
      val l = line.trim
      if (l.isEmpty) {
        // end of sentence
        if (sentence.nonEmpty) {
          sentences += sentence.toArray
          sentence = new ArrayBuffer[Row]
        }
      } else {
        // within the same sentence
        val bits = l.split("\\s")
        if (bits.length < 2)
          throw new RuntimeException(s"ERROR: invalid line [$l]!")
        sentence += Row(bits)
      }
    }

    if (sentence.nonEmpty) {
      sentences += sentence.toArray
    }

    source.close()
    sentences.toArray
  }
}

case class Row(val tokens:Array[String]) {
  def get(idx:Int): String =
    if(idx < tokens.length) tokens(idx)
    else ""
}

