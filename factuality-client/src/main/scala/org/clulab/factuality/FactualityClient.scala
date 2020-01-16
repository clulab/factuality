package org.clulab.factuality

object FactualityClient extends App {
  val rnn = Factuality("org/clulab/factuality/models/fact")
  val words = Array("Parthenolide", "induced", "apoptosis", "and", "inhibited", "cell", "proliferation", "and", "the", "expression", "of", "VEGF", "in", "vitro", ".")
  val p = 1   // predicate's index position
  val pred_fact = rnn.predict(words, p)
}
