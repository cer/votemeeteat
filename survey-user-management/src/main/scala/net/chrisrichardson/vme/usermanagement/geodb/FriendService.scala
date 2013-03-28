package net.chrisrichardson.vme.usermanagement.geodb

import net.chrisrichardson.vme.common.messages.NearbyFriendsRequest
import net.chrisrichardson.vme.common.messages.FindNearbyFriendsResponse
import net.chrisrichardson.vme.usermanagement.messages.AddOrUpdateUserRequest

trait FriendService {

    def addOrUpdate(request : AddOrUpdateUserRequest)
    def deleteAllFriends() : Unit
    def findNearbyFriends(request : NearbyFriendsRequest) : FindNearbyFriendsResponse

    
}