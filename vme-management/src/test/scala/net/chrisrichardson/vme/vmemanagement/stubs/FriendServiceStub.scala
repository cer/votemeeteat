package net.chrisrichardson.vme.vmemanagement.stubs

import org.springframework.stereotype.Component
import net.chrisrichardson.vme.common.messages.{FriendInfo, FindNearbyFriendsResponse, NearbyFriendsRequest}
import scala.collection.JavaConversions._

@Component
class FriendServiceStub extends ExternalServiceStub[NearbyFriendsRequest] {

  def findNearbyFriends(request: NearbyFriendsRequest) = {
    _requests += request
    FindNearbyFriendsResponse(List(FriendInfo("John Doe", "+15105551212")))
  }
}
