package org.clulab.factuality.client

import org.clulab.factuality.Factuality

object FactualityClient extends App {
  // This particular model is provided in the library dependency.
  val factuality = Factuality("org/clulab/factuality/models/FTrainFDevScim3")
  val words = "Parthenolide induced apoptosis and inhibited cell proliferation and the expression of VEGF in vitro .".split(' ')
  val predicateIndex = 1 // induced
  val prediction: Float = factuality.predict(words, predicateIndex)

  println(s"Prediction: $prediction")
}
