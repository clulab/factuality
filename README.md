# Event Factuality Detection

## What is it?

This is the repository of a pre-training event factuality detection library which implements a deep learning method
to quantify the factuality of an event (i.e., did it happen or not).  To account for different components (including
lexical, syntactic, and structural information) expressing the factuality nature of influence statements, we
re-implement recent neural models of factuality (Rudinger et al., 2018) in Scala and package the software as a
standalone library. 

This repository contains:

+ A factuality library in the top-level project which provides in `org.clulab.factuality.Factuality` the neural model
(mostly a standard 2-layer bidirectional linear chain LSTM followed by a 2-layer regression model) and training
and prediction procedures needed for both factuality developers and clients.
 
+ A factuality-developer subproject for training the model (reading conll training and dev files and fitting to them)
in `FactualityTrainer` and for evaluating the model against test files in `FactualityPredictor`.

+ A factuality-client example which demonstrates how to load the pre-trained model and use it to calculate a
factuality score for a tokenized influence statement.  The code and build files can be copied to other projects.

+ A factuality-models directory for use in publishing and releasing factuality models.

## Regular Users (Clients)

### Installation

To use the published factuality library and the pretrained model, you do not need to "install" or clone this repo.
The factuality library has been published and can be incorporated into your project by adding the following
dependencies to your `build.sbt` file:
```scala
resolvers += "Artifactory" at "http://artifactory.cs.arizona.edu:8081/artifactory/sbt-release"

libraryDependencies ++= Seq(
  "org.clulab" %% "factuality" % "1.0.0",
  "org.clulab" % "factuality-models" % "0.2.0"
)
```

The factuality library is available through regular channels that dependency managers like `sbt` and `maven` consult,
but the additional resolver is needed for the large models can't be distributed through those same channels.

### Usage

If you simply want to access the factuality library in your project, add the above library dependencies to your
dependency manager and use the API demonstrated below to evaluate the factuality of an event in a given sentence:
```scala
import org.clulab.factuality.Factuality
  ...
  // This particular model is provided in the library dependency.
  val factuality = Factuality("org/clulab/factuality/models/FTrainFDevScim3")
  val words = "Parthenolide induced apoptosis and inhibited cell proliferation and the expression of VEGF in vitro .".split(' ')
  val predicateIndex = 1 // induced
  val prediction: Float = factuality.predict(words, predicateIndex)

  println(s"Prediction: $prediction")
}               
```

The factuality model is quite large, so it's important to configure Java with access to enough memory.
`-Xmx12G` is the suggested value.  See also the file `.sbtopts` for other Java options.  It will load
slowly, so batching predictions is advised for efficiency.  In this example, the model is contained
in a resource called `FTrainFDevScim3`.  The names of the models are subject to change and developers
may want to rename their own resources to distinguish models.

If you did go ahead and clone the repo, the factuality-client example program can be run with
```shell script
sbt:factuality> factuality-client/run
``` 

## Developers

### Installation

Developers should probably clone this repo:

```sh
$ git clone http://github.com/clulab/factuality
$ cd factuality
```

### Usage

### How to compile

This is a standard `sbt` project, so use the usual commands (i.e., `compile`, `run`, `assembly`, etc.) to
manipulate it (or open it with IntelliJ).  Training will likely require an HPC, so some of the examples below
are geared toward that.

```shell script
$ sbt
sbt:factuality> factuality-developer/compile
```

### How to train a model

If `sbt` is installed on your computer/HPC you can train the model with
```sh
sbt:factuality> factuality-developer/runMain org.clulab.factuality.FactualityTrainer -train train.conll -dev dev.conll -test test.conll -model model -embed org/clulab/glove/glove.42B.300d.txt
```

If you cannot use `sbt` interactively, quote all the arguments and send them en masse to `sbt`.
```sh
$ sbt 'factuality-developer/runMain org.clulab.factuality.FactualityTrainer -train train.conll -dev dev.conll -test test.conll -model model -embed org/clulab/glove/glove.42B.300d.txt'
```

If you have no access to `sbt` on the computer that does the training, but there is at least Java there (e.g., on
some HPCs), the project needs to be assembled on a computer that does have `sbt`, and then the assembly can be moved
to the training computer.  Run
```sh
sbt:factuality> factuality-developer/assembly
```
locally, and then copy the file `factuality-developer-assembly-1.0.0-SNAPSHOT.jar` (or similar, depending what
version we're on) in directory `factuality-developer/target/scala-2.12/` to the other computer.  There run
```sh
java -cp factuality-developer-assembly-1.0.0-SNAPSHOT.jar org.clulab.factuality.FactualityTrainer -train train.conll -dev dev.conll -test test.cnll -model model -embed org/clulab/glove/glove.42B.300d.txt
```

In these examples, the train, dev, and test "files" are really resources that are included in the project.
`glove.42B.300d.txt` is also a resource and is taken from the glove dependency.  The model will
be output in files `model.rnn` and `model.x2i`.

If you want to use different data sets, it is possible to override the conll resources and specify a local file
instead.  The following command reads the files from the disk that happen to be supplied with the source code.
You could use other ones.
```
sbt:factuality> factuality-developer/runMain org.clulab.factuality.FactualityTrainer -train factuality-developer/src/main/resources/train.conll -dev factuality-developer/src/main/resources/dev.conll -test factuality-developer/src/main/resources/test.cnll -model model -embed org/clulab/glove/glove.42B.300d.txt
``` 

To evaluate your model after training, use the `FactualityPredictor` program.  If you have just generated a
model called `model`, then the command would be
```sh
sbt:factuality> factuality-developer/runMain org.clulab.factuality.FactualityPredictor -test test.conll -model model
```

In this example, the model is read from a file in the current directory.  If the model is placed in the resource
directory of the factuality-developer project at `factuality-developer/src/main/resources/org/clulab/factuality/models`,
for example, then it could be read as a resource with
```sh
sbt:factuality> factuality-developer/runMain org.clulab.factuality.FactualityPredictor -test test.conll -model org/clulab/factuality/models/model
```
However, it takes a long time to package models, so this is only efficient if the package can be reused.
For more tips on deploying, publishing, and releasing the models, see the factuality-models subproject.
