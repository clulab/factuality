package org.clulab.factuality

import java.io.File
import java.io.FileInputStream

import org.clulab.embeddings.word2vec.Word2Vec
import org.clulab.factuality.Factuality._
import org.clulab.sequences._
import org.clulab.utils.StringUtils
import org.clulab.factuality.utils.Serializer

import scala.io.Source
import scala.util.Try

object FactualityTrainer {

  class Word2VecEmbedder(w2v: Word2Vec) extends Embedder {

    def dimensions: Int = w2v.dimensions

    def size: Int = w2v.matrix.size

    def words: Seq[String] = w2v.matrix.keySet.toList

    def apply(word: String): Array[Double] = w2v.matrix(word)
  }

  object Word2VecEmbedder {

    protected def applyFromFile(embeddingsResource: String): Word2Vec = {
      val stream = new FileInputStream(new File(embeddingsResource))

      Serializer.using(stream) { source =>
        new Word2Vec(source, None)
      }
    }

    protected def applyFromResource(embeddingsResource: String): Word2Vec = {
      val classLoader = getClass.getClassLoader
      // Important note: This must be a Stream rather than a Source (Source.fromResource)
      // because the file is encoded as iso-8859-1.  See Word2Vec.loadMatrixFromStream.
      val stream = classLoader.getResourceAsStream(embeddingsResource)

      Serializer.using(stream) { stream =>
        new Word2Vec(stream, None)
      }
    }

    def apply(embeddingsResource: String): Word2VecEmbedder = {
      logger.debug(s"Loading embeddings from $embeddingsResource...")
      val w2v1 = Try(applyFromFile(embeddingsResource))
      val w2v2 = w2v1.orElse(Try(applyFromResource(embeddingsResource)))
      val w2v = w2v2.get

      logger.debug(s"Completed loading embeddings for a vocabulary of size ${w2v.matrix.size}.")
      new Word2VecEmbedder(w2v)
    }
  }

  def main(args: Array[String]): Unit = {
    val props = StringUtils.argsToProperties(args)

    if(props.size() < 2) {
      usage()
      System.exit(1)
    }

    if(props.containsKey("train") && props.containsKey("dev") && props.containsKey("embed")) {
      logger.debug("Starting training procedure...")
 
      val rawtrainSentences = ColumnReader.readColumns(props.getProperty("train"))
      // one sentence can contain more than one predicates that annotated with factuality
      // convert to format Array[String]: position_predicate, factuality, sentence words
      val trainSentences = sentences2Instances(rawtrainSentences)
      val rawdevSentences = ColumnReader.readColumns(props.getProperty("dev"))
      val devSentences = sentences2Instances(rawdevSentences)
      val embeddingsResource = props.getProperty("embed")
      val embedder = Word2VecEmbedder(embeddingsResource)

      val rnn = new Factuality()

      if(props.containsKey("model")) {
        val modelFilePrefix = props.getProperty("model")
        val devFileStr = props.getProperty("dev").split('/').last
        val devOutputPrefix = "model_" + modelFilePrefix + ".dev_" + devFileStr + ".epoch_"
        rnn.initialize(trainSentences, embedder)
        rnn.train(trainSentences, devSentences, devOutputPrefix)
        save(modelFilePrefix, rnn.model)
      }
    }

    if(props.containsKey("model") && props.containsKey("test")) {
      logger.debug("Starting evaluation procedure...")
      val modelFilePrefix = props.getProperty("model")
      val testFileStr = props.getProperty("test").split('/').last
      val testOutputPrefix = "model_" + modelFilePrefix + ".test_" + testFileStr + ".epoch_"
      val rawtestSentences = ColumnReader.readColumnsFromFile(props.getProperty("test"))
      val testSentences = sentences2Instances(rawtestSentences)

      val rnn = Factuality(modelFilePrefix)
      rnn.evaluate(testSentences, testOutputPrefix)
    }
  }

  def usage(): Unit = {
    println("Usage: " + this.getClass.getName.replace('$', ' ') + "<ARGUMENTS>")
    println("Accepted arguments:")
    println("\t-train <training corpus in the CoNLL BIO or IO format>")
    println("\t-embed <embeddings resource in the word2vec format>")
    println("\t-model <prefix of the model file name>")
    println("\t-dev <development corpus in the CoNLL BIO or IO format>")
    println("\t-test <test corpus in the CoNLL BIO or IO format>")
  }
}
