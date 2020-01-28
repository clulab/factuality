package org.clulab.factuality.utils

object MathUtils {

  def randomize[T](l: Array[T], rand:util.Random): Array[T] = {
    for (i <- l.length - 1 to 1 by -1) {
      val j = rand.nextInt(i)
      val tmp = l(j)
      l(j) = l(i)
      l(i) = tmp
    }
    l
  }
}
