package net.chrisrichardson.vme.vmemanagement.externalservices

import net.chrisrichardson.vme.common.messages.FindNearbyFriendsResponse
import net.chrisrichardson.vme.common.messages.NearbyFriendsRequest
import java.util.concurrent.Future

trait FriendService {
  
    def findNearbyFriends(request : NearbyFriendsRequest) : Future[FindNearbyFriendsResponse]
}