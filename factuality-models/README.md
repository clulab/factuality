# factuality-model
This project is used to deploy factuality models to maven and only need concern
those people who have the ability to release to maven.  There is a clulab/processors
wiki page on the subject that explains the steps required to set that up.

## Instructions
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
