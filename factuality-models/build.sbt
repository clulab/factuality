import ReleaseTransformations._

name := "factuality-models"

organization := "org.clulab"

scalaVersion := "2.11.8"


// these are the steps to be performed during release
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _)),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  pushChanges
)


//
// publishing settings
//

// publish to a maven repo
publishMavenStyle := true

// don't include scala version in artifact, we don't need it
crossPaths := false

// the standard maven repository
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

// let’s remove any repositories for optional dependencies in our artifact
pomIncludeRepository := { _ => false }

// mandatory stuff to add to the pom for publishing
pomExtra := (
  <url>https://github.com/clulab/factuality-models</url>
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/clulab/factuality-models</url>
    <connection>https://github.com/clulab/factuality-models</connection>
  </scm>
  <developers>
    <developer>
      <id>mihai.surdeanu</id>
      <name>Mihai Surdeanu</name>
      <email>mihai@surdeanu.info</email>
    </developer>
  </developers>)

//
// end publishing settings
//

packageBin in Compile := (packageBin in Compile) map { file =>
  println("(Re)packaging with zero compression...")
  import java.io.{FileInputStream,FileOutputStream,ByteArrayOutputStream}
  import java.util.zip.{CRC32,ZipEntry,ZipInputStream,ZipOutputStream}
  val zis = new ZipInputStream(new FileInputStream(file))
  val tmp = new File(file.getAbsolutePath + "_decompressed")
  val zos = new ZipOutputStream(new FileOutputStream(tmp))
  zos.setMethod(ZipOutputStream.STORED)
  Iterator.continually(zis.getNextEntry).
    takeWhile(ze => ze != null).
    foreach { ze =>
      val baos = new ByteArrayOutputStream
      Iterator.continually(zis.read()).
        takeWhile(-1 !=).
        foreach(baos.write)
      val bytes = baos.toByteArray
      ze.setMethod(ZipEntry.STORED)
      ze.setSize(baos.size)
      ze.setCompressedSize(baos.size)
      val crc = new CRC32
      crc.update(bytes)
      ze.setCrc(crc.getValue)
      zos.putNextEntry(ze)
      zos.write(bytes)
      zos.closeEntry
      zis.closeEntry
    } 
  zos.close
  zis.close
  tmp.renameTo(file)
  file
}
