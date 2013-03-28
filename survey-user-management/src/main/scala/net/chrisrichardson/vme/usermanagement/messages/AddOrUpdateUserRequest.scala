package net.chrisrichardson.vme.usermanagement.messages
import scala.reflect.BeanInfo
import scala.reflect.BeanProperty
import net.chrisrichardson.vme.common.domain.Location

@BeanInfo
case class AddOrUpdateUserRequest(@BeanProperty var name: String, 
    @BeanProperty var phoneNumber: String, 
    @BeanProperty var longitude: Double, 
    @BeanProperty var latitude: Double) {
  
  def this() = this(null, null, 0, 0)
  
}

object AddOrUpdateUserRequest {
  def apply(name : String, phoneNumber : String, loc : Location) = new AddOrUpdateUserRequest(name, phoneNumber, loc.lon, loc.lat) 
}