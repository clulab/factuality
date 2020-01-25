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
  val rnn = Factuality("FTrainFDevScim3")

  // sci dataset
  "Factuality" should "be about 3 as definitly happened if the predicate is in the form of -ed and in a descriptive statment" in {
    val words = Array("Parthenolide", "induced", "apoptosis", "and", "inhibited", "cell", "proliferation", "and", "the", "expression", "of", "VEGF", "in", "vitro", ".")
    val p = 1
    val gold_fact = 3.0
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }

  it should "be about 3 as definitly happened if the predicate is a present tense verb describing a finding, obeservation or conclusion" in {
    val text_tokenization = "(0,These,DT), (1,findings,NNS), (2,indicate,VBP), (3,that,IN), (4,JTP-74057,NN), (5,and,CC), (6,PD0325901,NN), (7,suppress,VBP), (8,ERK,NN), (9,phosphorylation,NN), (10,and,CC), (11,hence,RB), (12,affect,VB), (13,the,DT), (14,subsequent,JJ), (15,downstream,JJ), (16,signaling,NN), (17,cascade,NN), (18,,,,), (19,thereby,RB), (20,inhibiting,VBG), (21,to,TO), (22,cell,NN), (23,cycle,NN), (24,progression,NN), (25,.,.)"
    val words = tokenization2words(text_tokenization)
    val p = 7
    val gold_fact = 3.0
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }

  it should "be about 2.25 as probably happened if event is reported" in {
    val text_tokenization = "(0,Homocysteine,NN), (1,has,VBZ), (2,been,VBN), (3,reported,VBN), (4,to,TO), (5,enhance,VB), (6,endothelial,JJ), (7,LOX-1,NN), (8,gene,NN), (9,expression,NN), (10,and,CC), (11,TNF,NN), (12,alpha,NN), (13,release,NN), (14,upon,IN), (15,oxLDL,NN), (16,stimulation,NN), (17,[,-LRB-), (18,[,-LRB-), (19,76,77,CD), (20,],-RRB-), (21,Figure,NNP), (22,5,CD), (23,,,,), (24,branch,NN), (25,3,CD), (26,],-RRB-), (27,.,.)"
    val words = tokenization2words(text_tokenization)
    val p = 5
    val gold_fact = 2.25
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }

  it should "be about 2.25 as probably happened if under certain condition" in {
    val text_tokenization = "(0,In,IN), (1,summary,NN), (2,,,,), (3,renal,JJ), (4,mineralocorticoid,NN), (5,receptor,NN), (6,activation,NN), (7,does,VBZ), (8,not,RB), (9,appear,VB), (10,to,TO), (11,be,VB), (12,the,DT), (13,main,JJ), (14,determinant,NN), (15,of,IN), (16,hypertension,NN), (17,in,IN), (18,most,JJS), (19,patients,NNS), (20,with,IN), (21,CS,NNP), (22,,,,), (23,except,IN), (24,in,IN), (25,those,DT), (26,with,IN), (27,extremely,RB), (28,elevated,JJ), (29,circulating,VBG), (30,cortisol,NN), (31,levels,NNS), (32,where,WRB), (33,renal,JJ), (34,mineralocorticoid,NN), (35,receptor,NN), (36,hyperactivation,NN), (37,contributes,VBZ), (38,to,TO), (39,additional,JJ), (40,sodium,NN), (41,and,CC), (42,fluid,NN), (43,retention,NN), (44,,,,), (45,beyond,IN), (46,the,DT), (47,increased,JJ), (48,excretion,NN), (49,of,IN), (50,potassium,NN), (51,and,CC), (52,consequent,JJ), (53,hypokalemia,NN), (54,.,.)"
    val words = tokenization2words(text_tokenization)
    val p = 37
    val gold_fact = 2.25
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }

  it should "be about 0.75 as possiblely happened if a modal auxiliary verb is used to indicate possibility" in {
    val text_tokenization = "(0,Thus,RB), (1,,,,), (2,besides,IN), (3,the,DT), (4,PM,NNP), (5,pool,NN), (6,of,IN), (7,Raf,NN), (8,,,,), (9,H-Ras,JJ), (10,(,-LRB-), (11,12V,NN), (12,)-8RK,-RRB-), (13,may,MD), (14,accumulate,VB), (15,at,IN), (16,high,JJ), (17,enough,JJ), (18,levels,NNS), (19,in,IN), (20,many,JJ), (21,other,JJ), (22,cell,NN), (23,compartments,NNS), (24,to,TO), (25,activate,VB), (26,Raf,NN), (27,,,,), (28,as,RB), (29,well,RB), (30,as,IN), (31,additional,JJ), (32,Ras,NN), (33,effectors,NNS), (34,.,.)"
    val words = tokenization2words(text_tokenization)
    val p = 25
    val gold_fact = 0.75
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }

  // UDS-IH2 dataset
  it should "be about -1.875 as possible not happened if the predicate is in a request" in {
    val words = Array("Can", "you", "pass", "this", "along", "to", "Elizabeth", "to", "ensure", "Sanders", "is", "on", "board", "as", "well", "?")
    val p = 2
    val gold_fact = -1.875
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }


  it should "be about -2.25 as probably not happened if the predicate is under assumption" in {
    val words = Array("If", "the", "PX", "comes", "back", "again", ",", "I", "will", "call", "their", "in", "-", "house", "attys", ".")
    val p = 3
    val gold_fact = -2.25
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

  it should "be about 3 as definitely happened if the predicate is past tense" in {
    val words = Array("On", "the", "next", "two", "pictures", "he", "took", "screenshots", "of", "two", "beheading", "video's", ".")
    val p = 6
    val gold_fact = 3.0
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }

  it should "be about -2.625 as very likely not happened when say the action is required or necessary" in {
    val words = Array("In", "such", "case", ",", "you", "should", "destroy", "this", "message", "and", "kindly", "notify", "the", "sender", "by", "reply", "email", ".")
    val p = 6
    val gold_fact = -2.625
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }


  def tokenization2words(tokenization:String): Array[String] = {
    val words = new ArrayBuffer[String]()
    val tokens = tokenization.split(' ')    //such as: (1,induced,VBD)
    for(t <- tokens) {
        if(t.split(',')(1)!= ""){
          words += t.split(',')(1)
        }
        else{
          words += ","            //since it splitted by ',', so when the word itself is ',', it would be empty
        }
    }
    words.toArray
  }

}
