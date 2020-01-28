package org.clulab.factuality

trait Embedder {
  def dimensions: Int
  def size: Int
  def words: Seq[String]
  def apply(word: String): Array[Double]
}
