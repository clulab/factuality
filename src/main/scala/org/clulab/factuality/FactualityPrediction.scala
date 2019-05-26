package org.clulab.factuality

import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.{FileWriter, PrintWriter}

import org.clulab.embeddings.word2vec.Word2Vec
import org.clulab.struct.Counter
import org.slf4j.{Logger, LoggerFactory}
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import edu.cmu.dynet._
import edu.cmu.dynet.Expression._
import Factuality._
import org.clulab.fatdynet.utils.CloseableModelSaver
import org.clulab.fatdynet.utils.Closer.AutoCloser
import org.clulab.struct.MutableNumber
import org.clulab.utils.{MathUtils, Serializer, StringUtils}

import org.clulab.sequences._
import scala.collection.mutable
import scala.io.Source
import scala.util.Random

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
		  rnn.evaluate(testSentences)

		}
	}
}