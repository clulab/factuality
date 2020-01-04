# Event Factuality Detection

# What is it?

This is the repository of a pre-training event factuality detection library which implements a deep learning method to quantify the factuality of an event (i.e., did it happen or not?). To count different ingredients (including lexical, syntactic, and structural information) expressing the factuality nature of influence statements, we re-implement recent neural models of factuality (Rudinger et al., 2018) in Scala and package the software as a standalone library. 

This repository contains:
 
+ A factuality-client package (`org.clulab.factuality.client`), which loads the pre-trained factuality model, and detects the factuality score for each conll formatted influence statement in the test file.  
+ A factuality-models placeholder directory. Deployment of a pre-trained model is presently not possible because of file size limitations. The actual model files, `fact.rnn` and `fact.x2i`, need be retrieved to replace their empty placeholders to be used by the client programs.
+ Scala source code `Factuality.scala` for training the neural model (which is mostly a standard 2-layer bidirectional linear chain LSTM, followed by a 2-layer regression model), and `FactualityPrediction.scala` for evaluation.


# Installation

This software requires Java 1.8, Scala 2.12, and CoreNLP 3.x or higher.

The SBT dependencies are captured in the build file. They are:

```scala
libraryDependencies ++= Seq(
  "org.clulab" %% "processors-main" % "7.5.1",
  "org.clulab" %% "processors-corenlp" % "7.5.1",
  "org.clulab" %% "processors-modelscorenlp" % "7.5.1",
  "org.clulab" %% "processors-modelsmain" % "7.5.1",
  "org.clulab" %% "fatdynet" % "0.2.4", 
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)
```

# How to Use
## Option 1: make a jar file 
### How to compile?

This is a standard sbt project, so use the usual commands (i.e. `sbt compile`, `sbt assembly`, etc.) to compile. Add the generated jar files under `target/` to your `$CLASSPATH`, along with the other necessary dependency jars.

### How to publish factuality models? 
See [`instructions`](https://github.com/clulab/factuality/blob/master/factuality-models/README.md).

### How to access the jar file and evaluate on a test file? 
See [`instructions`](https://github.com/clulab/factuality/tree/master/factuality-client).

An example of evaluate on a test file:

```java -cp factuality-assembly-1.0-SNAPSHOT.jar org.clulab.factuality.client.Client -model fact -test src/main/resources/sci.conll -embed src/main/resources/glove.42B.300d.txt```

## Option 2: run source code 

### How to run train a model?
```sbt 'runMain org.clulab.factuality.Factuality -train src/main/resources/train.conll -dev src/main/resources/dev.conll -test src/main/resources/test.conll -model fact -embed src/main/resources/glove.42B.300d.txt'```

### How to evaluate a test file?
``` sbt 'runMain org.clulab.factuality.FactualityPrediction -test src/main/resources/sci.conll -model fact -embed glove.txt'``` 

### How to customize code to evaluate a single/few event(s)?
```val rnn = Factuality("fact")
val words = Array("Parthenolide", "induced", "apoptosis", "and", "inhibited", "cell", "proliferation", "and", "the", "expression", "of", "VEGF", "in", "vitro", ".")
val p = 1   # predicate's index position
val pred_fact = rnn.predict(words, p)                     
```

