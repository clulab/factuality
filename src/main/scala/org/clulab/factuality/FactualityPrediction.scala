package org.clulab.factuality

import Factuality._
import org.clulab.utils.StringUtils

import org.clulab.sequences._

/**
	* Simple main method to run the evaluation portion of Factuality
	*/
object FactualityPrediction {
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

		  val rnn = Factuality(props.getProperty("model"))
		  rnn.evaluate(testSentences, "model of " + props.getProperty("model") + " eval on " + props.getProperty("test"))

		}
	}
}