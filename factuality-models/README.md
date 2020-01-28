# factuality-models

There are at least four ways to deal with the models: via

1. the filesystem
1. a project resource
1. sbt publishLocal
1. sbt release

## Filesystem

In most cases, training data can be read from either a file or a resource.  The filesystem is checked for the file
first and it takes precedence.  New models don't necessarily need any more deployment than this.

## Project Resource

Files placed into the `src/main/resources` directory of a project can be accessed as resources.  `sbt` seems to always
add these files to jars and that strategy should work with this project, although creating the large jar files can be time
consuming.  IntelliJ can leave resources as is and manipulate the classpath to enable access.  This is temporarily
not compatible with model reading code in `fatdynet` until the dependency is updated.

## sbt publishLocal

This technique is certain to package the model into jar files.  

1. Copy the model into the resource directory of this project, possibly into the place reserved for them at
`org/clulab/factuality/models`.
1. Double check that `version.sbt` is as desired so that other models are not overwritten.  They generally land in
`~/.ivy2/local/org.clulab/factuality-models/jars`.  Version numbers of locally published packages usually end with
`-SNAPSHOT`.
1. You may want to update `CHANGES.md` or add some documentation to this `README.md` about the new model because
these files are copied into the jar file along with the model.
1. Run `$ sbt factuality-models/publishLocal`.
1. Update library dependencies.  For instance, `factuality-client/build.sbt` may need an updated line
`"org.clulab" % "factuality-models" % "0.2.0-SNAPSHOT"`.
1. If the name of your model is no longer `FTrainFDevScim3` as is used in some of the code, update the code.
If it is no longer located at `org/clulab/factuality/models`, make similar modifications.

## sbt release

This option (and also `sbt publish`) is only available to authorized personnel who have access to
`artifactory.cs.arizona.edu`.  With this option, one can publish to the server so that others (the public)
can then have read access to the model and use it in their own factuality clients.  Because of the size
of the models, they are not released to the standard providers.  The instructions are almost the same as
for `sbt publishLocal`.  `release` adds some automatic versioning and also manipulates the GitHub repository,
for which one also needs access.

1. Copy the model into the resource directory of this project, possibly into the place reserved for them at
`org/clulab/factuality/models`.
1. `version.sbt` will be managed by the `release` plugin.
1. You may want to update `CHANGES.md` or add some documentation to this `README.md` about the new model because
these files are copied into the jar file along with the model.
1. In `sbt` run `sbt:factuality> project factuality-models` and then `sbt:factuality-models release`.
1. Answer questions about versions, enter passwords, etc.  Wait a long time.  Generally answer no about
the push to GitHub at the end, because some additional files need to be updated.
1. Update library dependencies.  For instance, `factuality-client/build.sbt` may need an updated line
`"org.clulab" % "factuality-models" % "0.3.0"`.
1. If the name of your model is no longer `FTrainFDevScim3` as is used in some of the code, update the code.
If it is no longer located at `org/clulab/factuality/models`, similar modifications may be necessary.
1. Commit these later changes to GitHub, push, and start a pull request.
