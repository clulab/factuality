package org.clulab.factuality.client

import java.io.File
import java.net.JarURLConnection
import java.net.URI
import java.nio.charset.StandardCharsets

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

  abstract class Loader(base: String) {
    val utf8: String = StandardCharsets.UTF_8.toString

    protected def getSource(filename: String): Source
    protected def loadRnn(modelFilename: String, model: ParameterCollection, key: String = ""): Unit

    def mkDynetFilename(modelFilename: String): String = base + modelFilename + ".rnn"

    def mkX2iFilename(modelFilename: String): String = base + modelFilename + ".x2i"

    // Compare this to Factuality.load().
    protected def loadX2i(modelFilename: String): (Map[String, Int], Map[Char, Int], Int) = {
      val x2iFilename = mkX2iFilename(modelFilename)

      getSource(x2iFilename).autoClose { source =>
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

    def load(modelFilename: String): LstmParameters = {
      val (w2i, c2i, dim) = Timer.time("Read x2i") { loadX2i(modelFilename) }
      val model = mkParams(w2i, c2i, dim)

      Timer.time("Read rnn") { loadRnn(modelFilename, model.parameters) }
      model
    }
  }

  class FileLoader(base: String, modelFilename: String) extends Loader(base) {
    println(s"Wanting to load file ${mkDynetFilename(modelFilename)}")
    println(s"Wanting to load file ${mkX2iFilename(modelFilename)}")

    def getSource(filename: String): Source = Source.fromFile(filename, utf8)

    protected def loadRnn(modelFilename: String, model: ParameterCollection, key: String = ""): Unit = {
      val dynetFilename = mkDynetFilename(modelFilename)

      new CloseableModelLoader(dynetFilename).autoClose { modelLoader =>
        modelLoader.populateModel(model, key)
      }
    }
  }

  class ResourceLoader(base: String, modelFilename: String) extends Loader(base) {
    protected val myClassLoader: ClassLoader = this.getClass.getClassLoader

    println(s"Wanting to load resource ${myClassLoader.getResource(mkDynetFilename(modelFilename))}")
    println(s"Wanting to load resource ${myClassLoader.getResource(mkX2iFilename(modelFilename))}")

    def getSource(filename: String): Source = {
      val url = myClassLoader.getResource(mkX2iFilename(modelFilename))
      Source.fromURL(url, utf8)
    }

    protected def loadRnn(modelFilename: String, model: ParameterCollection, key: String = ""): Unit = {
      val dynetFilename = mkDynetFilename(modelFilename)
      val url = myClassLoader.getResource(dynetFilename)
      if (Option(url).isEmpty)
        throw new RuntimeException(s"ERROR: cannot locate the model file $dynetFilename!")
      val protocol = url.getProtocol
      if (protocol == "jar") {
        // The resource has been jarred, and must be extracted with a ZipModelLoader.
        val jarUrl = url.openConnection().asInstanceOf[JarURLConnection].getJarFileURL
        val protocol2 = jarUrl.getProtocol
        assert(protocol2 == "file")
        val uri = new URI(jarUrl.toString)
        // This converts both percent encoded characters and file separators.
        val nativeJarFileName = new File(uri).getPath

        new CloseableZipModelLoader(dynetFilename, nativeJarFileName).autoClose { zipModelLoader =>
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
        throw new RuntimeException(s"ERROR: cannot locate the model file $dynetFilename with protocol $protocol!")
    }
  }

  protected def load(modelFilename: String): Factuality = {
    // make sure DyNet is initialized!
    Initialize.initialize() // No random seed should be necessary for testing.

    // now load the saved model
    val rnn = new Factuality()
//    val loader = new FileLoader("./data/", modelFilename)
    val loader = new ResourceLoader("org/clulab/factuality/models/", modelFilename)

    rnn.model = Timer.time("Loading models") { loader.load(modelFilename) }
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

      val rnn = load(props.getProperty("model"))
      rnn.evaluate(testSentences, "model." + props.getProperty("model") + ".eval." + props.getProperty("test").split('/').last+".")
    }
  }
}
