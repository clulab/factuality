package org.clulab.factuality.utils

import scala.language.reflectiveCalls

object Serializer {

  type Closable = { def close(): Unit }

  def using[A <: Closable, B](resource: A)(f: A => B): B = {
    try {
      f(resource)
    } finally {
      resource.close()
    }
  }
}
