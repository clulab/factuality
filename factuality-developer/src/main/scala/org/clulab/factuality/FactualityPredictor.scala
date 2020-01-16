package org.clulab.factuality

import Factuality._
import org.clulab.utils.StringUtils

import org.clulab.sequences._

/**
  * Simple main method to run the evaluation portion of Factuality
  */
object FactualityPredictor {

  def main(args: Array[String]): Unit = {
    val props = StringUtils.argsToProperties(args)

    if(props.size() < 2) {
      usage()
      System.exit(1)
    }

    if(props.containsKey("test") && props.containsKey("model")) {
      logger.debug("Starting evaluation procedure...")

      val rawtestSentences = ColumnReader.readColumns(props.getProperty("test"))
      val testSentences = Factuality.sentences2Instances(rawtestSentences)

      val modelFilePrefix = props.getProperty("model")
      val rnn = Factuality(modelFilePrefix) // kwa which version is this?

      val testFileStr = props.getProperty("test").split('/').last
      val testOutputPrefix = "model_" + modelFilePrefix + ".test_" + testFileStr + ".epoch_"

      rnn.evaluate(testSentences, testOutputPrefix)
    }
  }

  def usage(): Unit = {
    println("Usage: " + this.getClass.getName.replace('$', ' ') + "<ARGUMENTS>")
    println("Accepted arguments:")
    println("\t-model <prefix of the model file name>")
    println("\t-test <test corpus in the CoNLL BIO or IO format>")
  }
}
