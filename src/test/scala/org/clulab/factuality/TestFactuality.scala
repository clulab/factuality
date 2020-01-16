package org.clulab.factuality
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable.ArrayBuffer

class TestFactuality extends FlatSpec with Matchers {
  // val proc:Processor = new BioCluProcessor
  // val s1 = "Cells were additionally stimulated with 10 ng/ml NRG and cell extracts analyzed for ErbB3 tyrosine phosphorylation"
  val rnn: Factuality = Factuality("fact", fromResource = false)
  // rnn.evaluate(testSentences)

  // sci dataset
  "Factuality" should "be about 3 as definitedly happened if the predicate is in the form of -ed and in a descriptive statment" in {
    val words = Array("Parthenolide", "induced", "apoptosis", "and", "inhibited", "cell", "proliferation", "and", "the", "expression", "of", "VEGF", "in", "vitro", ".")
    val p = 1
    val gold_fact = 3.0
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }

  it should "be about 3 as definitedly happened if the predicate is a present tense verb describing a finding, obeservation or conclusion" in {
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

  it should "be about 2.25 as probably happened if use quantifiers, such as most, to talk about quantities, amounts and degree" in {
    val text_tokenization = "(0,In,IN), (1,most,JJS), (2,cases,NNS), (3,cPLA2,NN), (4,is,VBZ), (5,upregulated,VBN), (6,;,:), (7,for,IN), (8,example,NN), (9,,,,), (10,in,IN), (11,human,NN), (12,lung,NN), (13,tumor,NN), (14,cells,NNS), (15,cPLA2,NN), (16,activation,NN), (17,by,IN), (18,oncogenic,JJ), (19,Ras,NN), (20,has,VBZ), (21,been,VBN), (22,reported,VBN), (23,[,-LRB-), (24,88,CD), (25,],-RRB-), (26,.,.)"
    val words = tokenization2words(text_tokenization)
    val p = 16
    val gold_fact = 2.25
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }

  it should "be about 0.75 as possiblely happened if the predicate is used in a hypothesis" in {
    val text_tokenization = "(0,To,TO), (1,identify,VB), (2,whether,IN), (3,the,DT), (4,downregulation,NN), (5,of,IN), (6,KRAS,NNP), (7,alone,RB), (8,inhibits,VBZ), (9,the,DT), (10,proliferation,NN), (11,of,IN), (12,the,DT), (13,TE-1,NN), (14,cell,NN), (15,line,NN), (16,,,,), (17,a,DT), (18,small,JJ), (19,interfering,VBG), (20,(,-LRB-), (21,si,FW), (22,),-RRB-), (23,K-ras,FW), (24,expression,NN), (25,vector,NN), (26,was,VBD), (27,constructed,VBN), (28,.,.)"
    val words = tokenization2words(text_tokenization)
    val p = 8
    val gold_fact = 0.75
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }

  // UDS-IH2 dataset
  it should "be about -1.875 as possible not happened if the predicate is in doubt" in {
    val words = Array("do", "n't", "they", "know", "we", "have", "better", "things", "to", "do")
    val p = 9
    val gold_fact = -1.875
    val pred_fact = rnn.predict(words, p)
 
    (pred_fact - gold_fact) should be (0.0 +- 0.5)
  }


  it should "be about -2.25 as probably not happened if the predicate is under assumption" in {
    val words = Array("What", "if", "Google", "expanded", "on", "its", "search", "-", "engine", "(", "and", "now", "e-mail", ")", "wares", "into", "a", "full", "-", "fledged", "operating", "system", "?")
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
