package net.chrisrichardson.vme.common.messages
import scala.reflect.BeanInfo
import scala.reflect.BeanProperty

@BeanInfo
case class FindNearbyFriendsResponse(@BeanProperty var friends : java.util.List[FriendInfo]) {
  def this() = this(null)
}

@BeanInfo
case class FriendInfo(
    @BeanProperty var name : String, 
    @BeanProperty var phoneNumber : String
    ) {
  
  def this() = this(null, null)
}