# factuality-model

Deployment to Maven Central is presently not possible because of file size limitations
there.  The recommended alternative is to publish locally and then copy the generated
directory structure to the cloud or clulab servers from which the files can be retrieved
and used by client programs.  See the factuality-client for hints on how to access the
models.

## Local Deployment

### Introduction

This project is used to publish factuality models locally and only need concern
those people who are able to able to train the models, which generally requires an HPC
cluster.  The local models can be transferred to the cloud where others can retrieve
them for use with the factuality-client.  These instructions generally match those
for deployment to Maven Central, but stop short of the release step.  Manual intervention
is required after that.

### Instructions

### Instructions
1. Replace the empty placeholder model files, fact.rnn and fact.x2i, in
`src/main/resources/org/clulab/factuality/models` with the actual models
that should be released.
1. Update the `README.md` file in `src/main/resources` to document the model files.
1. Increment the version number in `version.sbt`.
1. Update `CHANGES.md` to record what has changed about the models.
1. Create a local version using `sbt publishLocal`
1. Update the dependency for the `factuality-client` project to the new version
   and make sure the client runs.  This may require "touching" `../build.sbt`.
1. Do not both with `sbt release`, because the files are too large for Maven Central.
1. Manually copy the locally published model files from your `ivy2` directory, usually
   `~/.ivy2/local/org.clulab/factuality-models` to, for example, the CluLab google drive.
1. If necessary, update the `README.md` file for the `factuality-client` project so that
   people know where to find the files.   
1. Leave the local version in the ivy2 directory for your own use.
1. Delete the models, which are too large for github, from their `src` directory and
   restore the placeholders.
1. Push the changes in `factuality-models` and `factuality-client` to github.


## ~~Deployment to Maven Central~~

### ~~Introduction~~

This project is used to deploy factuality models to maven and only need concern
those people who have the ability to release to maven.  There is a clulab/processors
wiki page on the subject that explains the steps required to set that up.

### ~~Instructions~~
1. Replace the empty placeholder model files, fact.rnn and fact.x2i, in
`src/main/resources/org/clulab/factuality/models` with the actual models
that should be released.
1. Update the `README.md` file in `src/main/resources` to document the model files.
1. Increment the version number in `version.sbt`.
1. Update `CHANGES.md` to record what has changed about the models.
1. Create a local version using `sbt publishLocal`
1. Update the dependency for the `factuality-client` project to the new version
   and make sure the client runs.  This may require "touching" `../build.sbt`.
1. Assuming that you are set up to do so, run `sbt release`.
1. Remove the local version from the ivy2 directory.
1. Make sure the client version runs with the remote version.
1. Delete the models, which are too large for github, from their directory and restore the placeholders.
1. Push the changes in `factuality-models` and `factuality-client` to github.
