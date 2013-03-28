package net.chrisrichardson.vme.testutils.misc

import org.junit.Assert
import net.chrisrichardson.testutils.streams.DelayedStream._

object VmeAsserts {

  def assertStringContains(string : String, substring: String) {
    if (string.indexOf(substring) == -1)
      Assert.fail(String.format("<%s> does not contain <%s>", string, substring))
  }

  def assertExists[T](sequence: Seq[T])(predicate: T => Boolean) {
    if (!sequence.exists(predicate))
      Assert.fail(String.format("%s does not contain an element that satisfies predicate", sequence))
  }

  def eventually(body : => Unit) {
    import scala.util.control.Exception.catching

    if (Stream.continually(catching(classOf[AssertionError]).either { body }).withDelay().take(10).dropWhile(_.isLeft).isEmpty)
      body
  }

}
