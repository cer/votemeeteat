package net.chrisrichardson.vme.common.messages
import scala.reflect.BeanInfo
import scala.reflect.BeanProperty
import net.chrisrichardson.vme.common.domain.Location

@BeanInfo
case class NearbyFriendsRequest(@BeanProperty var longitude : Double, @BeanProperty var latitude : Double) {
  
  def this() = this(0, 0);

}

object NearbyFriendsRequest {
  def fromLocation(location : Location) = NearbyFriendsRequest(longitude=location.lon, latitude=location.lat)
  def toLocation(request : NearbyFriendsRequest) = Location(lon=request.longitude, lat=request.latitude)
}