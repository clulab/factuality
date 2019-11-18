# factuality-client

In order to use this program, you'll need to acquire the factuality models.
In `build.sbt`, the line of code that makes use of the models is probably commented
out so that error messages are not generated.  I looks something like

`//"org.clulab" % "factuality-models" % "0.2.0"`
  
The most recently produced models are located on the clulab servers at

`/net/kate/storage/work/kalcock/models/org.clulab/factuality-models`

These files need to be copied to the appropriate place in your local ivy2 directory,
which is probably `~/.ivy2/local/org.clulab/factuality-models`.  Once this has been done,
the line in `build.sbt` can be uncommented.  You may need to "touch" the top level
`build.sbt` in order for programs like IntelliJ to notice the change.

Please update this documentation if you change the location (or version) of the model.
