package net.chrisrichardson.vme.vmemanagement.messages

import scala.reflect.BeanProperty

case class CreateVmeRequest(
    @BeanProperty var name: String,
    @BeanProperty var phoneNumber: String,
    @BeanProperty var longitude: Double,
    @BeanProperty var latitude: Double) {
  
  def this() = this(null, null, 0, 0)
  
}