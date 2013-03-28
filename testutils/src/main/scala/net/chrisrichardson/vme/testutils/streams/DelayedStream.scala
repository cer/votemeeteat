package net.chrisrichardson.testutils.streams

import java.util.concurrent.TimeUnit

class DelayedStream[T](stream : Stream[T]) {

  def withDelay() : Stream[T] = withDelay(stream)

  def withDelay[T](stream : Stream[T]) : Stream[T] = stream match {
    case x #:: xs  => x #:: {
      TimeUnit.SECONDS.sleep(1)
      withDelay(xs)
    }
    case empty => stream
  }

}

object DelayedStream {
  implicit def toDelayedStream[T](stream : Stream[T]) = new DelayedStream(stream)
}
