package org.clulab.factuality.client

import java.io.File
import java.net.JarURLConnection
import java.net.URI

import edu.cmu.dynet.Initialize
import edu.cmu.dynet.ParameterCollection
import org.clulab.factuality.Factuality
import org.clulab.factuality.Factuality._
import org.clulab.factuality.LstmParameters
import org.clulab.factuality.client.utils.Timer
import org.clulab.fatdynet.utils.CloseableModelLoader
import org.clulab.fatdynet.utils.CloseableZipModelLoader
import org.clulab.fatdynet.utils.Closer.AutoCloser
import org.clulab.sequences.ColumnReader
import org.clulab.utils.StringUtils

import scala.io.Source

/**
  * Simple main method to run the evaluation portion of Factuality
  */
object Client {
  protected val myClassLoader = this.getClass.getClassLoader

  protected def populateModel(filename: String, model: ParameterCollection, key: String = "" ): Unit = {
    val url = myClassLoader.getResource(filename)
    if (Option(url).isEmpty)
      throw new RuntimeException(s"ERROR: cannot locate the model file $filename!")
    val protocol = url.getProtocol
    if (protocol == "jar") {
      // The resource has been jarred, and must be extracted with a ZipModelLoader.
      val jarUrl = url.openConnection().asInstanceOf[JarURLConnection].getJarFileURL
      val protocol2 = jarUrl.getProtocol
      assert(protocol2 == "file")
      val uri = new URI(jarUrl.toString)
      // This converts both percent encoded characters and file separators.
      val nativeJarFileName = new File(uri).getPath

      new CloseableZipModelLoader(filename, nativeJarFileName).autoClose { zipModelLoader =>
        zipModelLoader.populateModel(model, key)
      }
    }
    else if (protocol == "file") {
      // The resource has not been jarred, but lives in a classpath directory.
      val uri = new URI(url.toString)
      // This converts both percent encoded characters and file separators.
      val nativeFileName = new File(uri).getPath

      new CloseableModelLoader(nativeFileName).autoClose { modelLoader =>
        modelLoader.populateModel(model, key)
      }
    }
    else
      throw new RuntimeException(s"ERROR: cannot locate the model file $filename with protocol $protocol!")
  }

  // Compare this to Factuality.load().
  protected def loadFromResource(modelFilename: String): LstmParameters = {
//    val base = "org/clulab/factuality/models/" // This may not start with /
    val base = "./data/"
    val dynetFilename = base + modelFilename + ".rnn"
    val x2iFilename = base + modelFilename + ".x2i"

    // testing
    val url1 = myClassLoader.getResource(dynetFilename)
    val url2 = myClassLoader.getResource(x2iFilename)
    println(url1)
    println(url2)

    val (w2i, c2i, dim) = Timer.time("Read x2i") {
//      Source.fromInputStream(myClassLoader.getResourceAsStream(x2iFilename)).autoClose { source =>
      Source.fromFile(x2iFilename).autoClose { source =>
        def stringToString(string: String): String = string
        def stringToChar(string: String): Char = string.charAt(0)

        val byLineStringMapBuilder = new ByLineMapBuilder(stringToString)
        val byLineCharMapBuilder = new ByLineMapBuilder(stringToChar)

        val lines = source.getLines()
        val w2i = byLineStringMapBuilder.build(lines)
        val c2i = byLineCharMapBuilder.build(lines)
        val dim = new ByLineIntBuilder().build(lines)

        (w2i, c2i, dim)
      }
    }
    val model = {
      val model = mkParams(w2i, c2i, dim)

      Timer.time("Read rnn") {
//        populateModel(dynetFilename, model.parameters, "/all")
        new CloseableModelLoader(dynetFilename).autoClose { modelLoader =>
          modelLoader.populateModel(model.parameters, "/all")
        }
      }

      model
    }

    model
  }

  protected def newRnn(modelFilename:String): Factuality = {
    // make sure DyNet is initialized!
    Initialize.initialize() // No random seed should be necessary for testing.

    // now load the saved model
    val rnn = new Factuality()
    rnn.model = Timer.time("Loading models") {
       loadFromResource(modelFilename) // changed
    }
    rnn
  }

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

      val rnn = newRnn(props.getProperty("model"))
      rnn.evaluate(testSentences, "model." + props.getProperty("model") + ".eval." + props.getProperty("test").split('/').last+".")
    }
  }
}
