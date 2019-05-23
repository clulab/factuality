package org.clulab.factuality
import org.scalatest.{FlatSpec, Matchers}
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

 
class TestFactuality extends FlatSpec with Matchers {
  // val proc:Processor = new BioCluProcessor
  // val s1 = "Cells were additionally stimulated with 10 ng/ml NRG and cell extracts analyzed for ErbB3 tyrosine phosphorylation"
  val rnn = Factuality("fact")
  // rnn.evaluate(testSentences)

  // sci dataset
  "Factuality" should "be 3 as definitedly happened if the predicate is in the form of -ed and in a descriptive statment" in {
    val text_tokenization = "(0,Parthenolide,NN), (1,induced,VBD), (2,apoptosis,NN), (3,and,CC), (4,inhibited,VBD), (5,cell,NN), (6,proliferation,NN), (7,and,CC), (8,the,DT), (9,expression,NN), (10,of,IN), (11,VEGF,NN), (12,in,FW), (13,vitro,FW), (14,.,.)"
    val words = tokenization2words(text_tokenization)
    val p = 1
    val gold_fact = 3.0
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }

  it should "be 3 as definitedly happened if the predicate is a present tense verb describing a finding, obeservation or conclusion" in {
    val text_tokenization = "(0,These,DT), (1,findings,NNS), (2,indicate,VBP), (3,that,IN), (4,JTP-74057,NN), (5,and,CC), (6,PD0325901,NN), (7,suppress,VBP), (8,ERK,NN), (9,phosphorylation,NN), (10,and,CC), (11,hence,RB), (12,affect,VB), (13,the,DT), (14,subsequent,JJ), (15,downstream,JJ), (16,signaling,NN), (17,cascade,NN), (18,,,,), (19,thereby,RB), (20,inhibiting,VBG), (21,to,TO), (22,cell,NN), (23,cycle,NN), (24,progression,NN), (25,.,.)"
    val words = tokenization2words(text_tokenization)
    val p = 7
    val gold_fact = 3.0
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }

  it should "be 3 as definitedly happened if the predicate is a noun and in a descriptive statment" in {
    val text_tokenization = "(0,Silencing,NN), (1,of,IN), (2,HDAC7,NN), (3,in,IN), (4,the,DT), (5,breast,NN), (6,cancer,NN), (7,cell,NN), (8,line,NN), (9,MCF7,NN), (10,negatively,RB), (11,influences,VBZ), (12,17-beta-estradiol,JJ), (13,(,-LRB-), (14,E2,NN), (15,)-mediated,-RRB-), (16,repression,NN), (17,of,IN), (18,RPRM,NNP), (19,,,,), (20,as,RB), (21,well,RB), (22,as,IN), (23,that,DT), (24,of,IN), (25,other,JJ), (26,E2,NN), (27,repressed,VBD), (28,genes,NNS), (29,such,JJ), (30,as,IN), (31,ENC1,NN), (32,,,,), (33,NEDD9,NN), (34,,,,), (35,OPG,NNP), (36,,,,), (37,CXCR4,NN), (38,and,CC), (39,CERK,NN), (40,.,.)"
    val words = tokenization2words(text_tokenization)
    val p = 16
    val gold_fact = 3.0
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }

  it should "be about 2.25 as probably happened if the predicate is to certain exent" in {
    val text_tokenization = "(0,In,IN), (1,most,JJS), (2,cases,NNS), (3,cPLA2,NN), (4,is,VBZ), (5,upregulated,VBN), (6,;,:), (7,for,IN), (8,example,NN), (9,,,,), (10,in,IN), (11,human,NN), (12,lung,NN), (13,tumor,NN), (14,cells,NNS), (15,cPLA2,NN), (16,activation,NN), (17,by,IN), (18,oncogenic,JJ), (19,Ras,NN), (20,has,VBZ), (21,been,VBN), (22,reported,VBN), (23,[,-LRB-), (24,88,CD), (25,],-RRB-), (26,.,.)"
    val words = tokenization2words(text_tokenization)
    val p = 16
    val gold_fact = 3 * 3.0 / 4.0
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }

  it should "be about 2.25 as probably happened if the predicate is modified by modal verbs" in {
    val text_tokenization = "(0,Elevated,JJ), (1,LIP,NN), (2,expression,NN), (3,can,MD), (4,,,,), (5,however,RB), (6,,,,), (7,induce,VBP), (8,proliferation,NN), (9,and,CC), (10,hyperplasias,NN), (11,that,WDT), (12,may,MD), (13,be,VB), (14,more,RBR), (15,susceptible,JJ), (16,to,TO), (17,additional,JJ), (18,oncogenic,JJ), (19,hits,NNS), (20,.,.)"
    val words = tokenization2words(text_tokenization)
    val p = 7
    val gold_fact = 3 * 3.0 / 4.0
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }

  // UDS-IH2 dataset
  it should "be about -2.25 as probably not happened if the predicate is under assumption" in {
    val words = Array("What", "if", "Google", "Morphed", "Into", "GoogleOS", "?")
    val p = 3
    val gold_fact = -2.25
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }


  it should "be about 1.5 as possible happened if the predicate is in doubt" in {
    val words = Array("Does", "anybody", "use", "it", "for", "anything", "else", "?")
    val p = 2
    val gold_fact = 1.5
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }


  it should "be about 2.625 as very likely happened if the predicate is present tense" in {
    val words = Array("They", "own", "blogger", ",", "of", "course", ".")
    val p = 1
    val gold_fact = 2.625
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }

  it should "be 3 as definitedly happened if the predicate is past tense" in {
    val words = Array("On", "the", "next", "two", "pictures", "he", "took", "screenshots", "of", "two", "beheading", "video's", ".")
    val p = 6
    val gold_fact = 3.0
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }

  it should "be -2.625 as very likely not happened when say the action is required or necessary" in {
    val words = Array("You", "have", "to", "see", "these", "slides", "....", "they", "are", "amazing", ".")
    val p = 3
    val gold_fact = -2.625
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }


  def tokenization2words(tokenization:String): Array[String] = {
    val words = new ArrayBuffer[String]()
    val tokens = tokenization.split(' ')    //such as: (1,induced,VBD)
    for(t <- tokens) {
        if(t.split(',').size != 0){
          words += t.split(',')(1)
        }
        else{
          words += ","            //since it splitted by ',', so when the word itself is ',', it would be empty
        }
    }
    words.toArray
  }

}
