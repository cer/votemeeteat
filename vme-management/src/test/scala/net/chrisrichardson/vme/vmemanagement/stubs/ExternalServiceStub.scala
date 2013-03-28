package net.chrisrichardson.vme.vmemanagement.stubs

import collection.mutable.ArrayBuffer

trait ExternalServiceStub[T] {

  val _requests = ArrayBuffer[T]()
  def requests = _requests.toSeq

  def clear() = _requests.clear()

}
