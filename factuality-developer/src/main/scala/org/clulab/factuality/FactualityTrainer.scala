package org.clulab.factuality

import org.clulab.embeddings.word2vec.Word2Vec
import org.clulab.factuality.Factuality._
import org.clulab.sequences._
import org.clulab.utils.StringUtils
import org.clulab.factuality.utils.Serializer

import scala.io.Source

object FactualityTrainer {

  class Word2VecEmbedder(w2v: Word2Vec) extends Embedder {

    def dimensions: Int = w2v.dimensions

    def size: Int = w2v.matrix.size

    def words: Seq[String] = w2v.matrix.keySet.toList

    def apply(word: String): Array[Double] = w2v.matrix(word)
  }

  object Word2VecEmbedder {

    def apply(embeddingsResource: String): Word2VecEmbedder = {
      logger.debug(s"Loading embeddings from file $embeddingsResource...")
      val w2v = Serializer.using(Source.fromResource(embeddingsResource, getClass.getClassLoader)) { source =>
          new Word2Vec(source, None)
      }
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
      // one sentence can conatin more than one predicates that annotated with factuality
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
      val rawtestSentences = ColumnReader.readColumns(props.getProperty("test"))
      val testSentences = sentences2Instances(rawtestSentences)

      val rnn = Factuality(modelFilePrefix) // kwa which version is this?
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
