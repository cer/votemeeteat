package net.chrisrichardson.vme.testutils.standalone

import java.net.URLClassLoader
import java.io.File
import org.junit.Assert
import scala.collection.JavaConversions._
import org.apache.commons.io.{IOUtils, FileUtils}
import org.apache.commons.logging.LogFactory

abstract class StandaloneApplicationLauncher(directory: String, mainClassName: String) {

  val logger = LogFactory.getLog(getClass)

  def start(): Unit
  def cleanUp(): Unit

  def jarsInRepo(directory: String) = {
    val possibleDir = new File(directory)
    Assert.assertTrue(possibleDir.isDirectory)
    FileUtils.listFiles(possibleDir, Array("jar"), true).map(_.getAbsolutePath)
  }

  def withStandaloneApp(body: => Unit) {
    start()
    try {
      body
    } finally {
      cleanUp()
    }
  }

}

class ClassLoaderBasedStandaloneApplicationLauncher(directory: String, mainClassName: String) extends StandaloneApplicationLauncher(directory, mainClassName) {
  def start() {
    class SinceWeAreCallingSetContextClassLoader extends Runnable {
      def run {
        val classLoader = new URLClassLoader(
          jarsInRepo(directory).map(name => new File(name)).map(_.toURI.toURL).toArray,
          ClassLoader.getSystemClassLoader().getParent());
        Thread.currentThread().setContextClassLoader(classLoader);
        val mainClass = classLoader.loadClass(mainClassName);
        val main = mainClass.getDeclaredMethods()(0)
        logger.info("calling main()" + mainClassName)
        main.invoke(null, null);
      }
    }
    (new Thread(new SinceWeAreCallingSetContextClassLoader)).start()
  }

  def cleanUp() {

  }
}


class ProcessBasedStandaloneApplication(directory: String, mainClassName: String) extends StandaloneApplicationLauncher(directory, mainClassName) {
  var process: Option[Process] = None

  def readStdout(process: Process) {
    class MyRunnable extends Runnable {
      def run() {
        IOUtils.copy(process.getInputStream, System.out)
      }
    }
    (new Thread(new MyRunnable)).start()

  }

  def start() {
    def systemPropertiesAsArgList = System.getProperties.map(p=> "-D" + p._1 + "=" + p._2).toList

    val pb = new ProcessBuilder(List("java","-classpath", jarsInRepo(directory).mkString(":")) ++ systemPropertiesAsArgList ++ List(mainClassName))
    pb.redirectErrorStream()
    val p = pb.start()
    process = Some(p)
    readStdout(p)
    logger.info("Started=" + directory)
  }

  def cleanUp() {
    process.foreach {
      _.destroy()
    }
  }
}

object StandaloneApplicationLauncher {
  def withStandaloneApp(directory: String, mainClassName: String) (body: => Unit) {
    val app = new ClassLoaderBasedStandaloneApplicationLauncher(directory, mainClassName)
    app.withStandaloneApp { body }
  }
}

