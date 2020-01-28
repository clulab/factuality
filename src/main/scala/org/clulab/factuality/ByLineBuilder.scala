package org.clulab.factuality

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


class MutableNumber[T](var value:T) extends Serializable {
  override def hashCode: Int = value.hashCode

  override def equals(other:Any):Boolean = {
    other match {
      case that:MutableNumber[T] => value == that.value
      case _ => false
    }
  }

  override def toString:String = value.toString
}

abstract class ByLineBuilder[IntermediateValueType] {

  protected def addLines(intermediateValue: IntermediateValueType, lines: Iterator[String]): Unit = {
    lines.next() // Skip the comment line

    def nextLine(): Boolean = {
      val line = lines.next()

      if (line.nonEmpty) {
        addLine(intermediateValue, line)
        true // Continue on non-blank lines.
      }
      else
        false // Stop at first blank line.
    }

    while (nextLine()) { }
  }

  def addLine(intermediateValue: IntermediateValueType, line: String): Unit
}

// This is a little fancy because it works with both String and Char keys.
class ByLineMapBuilder[KeyType](val converter: String => KeyType) extends ByLineBuilder[mutable.Map[KeyType, Int]] {
  def addLine(mutableMap: mutable.Map[KeyType, Int], line: String): Unit = {
    val Array(key, value) = line.split('\t')

    mutableMap += ((converter(key), value.toInt))
  }

  def build(lines: Iterator[String]): Map[KeyType, Int] = {
    val mutableMap: mutable.Map[KeyType, Int] = new mutable.HashMap

    addLines(mutableMap, lines)
    mutableMap.toMap
  }
}

// This only works with Strings.
class ByLineArrayBuilder extends ByLineBuilder[ArrayBuffer[String]] {

  def addLine(arrayBuffer: ArrayBuffer[String], line: String): Unit = {
    arrayBuffer += line
  }

  def build(lines: Iterator[String]): Array[String] = {
    val arrayBuffer: ArrayBuffer[String] = ArrayBuffer.empty

    addLines(arrayBuffer, lines)
    arrayBuffer.toArray
  }
}

// This only works with Strings.
class ByLineIntBuilder extends ByLineBuilder[MutableNumber[Option[Int]]] {

  def addLine(mutableNumberOpt: MutableNumber[Option[Int]], line: String): Unit = {
    mutableNumberOpt.value = Some(line.toInt)
  }

  def build(lines: Iterator[String]): Int = {
    val mutableNumberOpt: MutableNumber[Option[Int]] = new MutableNumber(None)

    addLines(mutableNumberOpt, lines)
    mutableNumberOpt.value.get
  }
}
