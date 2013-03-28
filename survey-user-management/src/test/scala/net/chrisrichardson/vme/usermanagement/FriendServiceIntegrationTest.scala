package net.chrisrichardson.vme.usermanagement

import geodb.FriendService
import messages.AddOrUpdateUserRequest
import org.springframework.beans.factory.annotation.Autowired
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import net.chrisrichardson.vme.common.messages.NearbyFriendsRequest
import org.junit.Assert
import scala.collection.JavaConversions._
import net.chrisrichardson.vme.common.messages.FindNearbyFriendsResponse
import net.chrisrichardson.vme.testcommon.{Numbers, Locations}

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations = Array("/appctx/**/*.xml"))
class FriendServiceIntegrationTest {

  @Autowired
  var friendService: FriendService = _

  @Test
  def testAddFriend {
    friendService.addOrUpdate(AddOrUpdateUserRequest("John Doe", Numbers.TEST_RECIPIENT_PHONE_NUMBER, Locations._609Mission))
    friendService.addOrUpdate(AddOrUpdateUserRequest("Chris E", Numbers.TEST_RECIPIENT_PHONE_NUMBER_2, Locations._71Stevenson))
    friendService.addOrUpdate(AddOrUpdateUserRequest("Mary Smith", Numbers.TEST_RECIPIENT_PHONE_NUMBER_3, Locations._7ClaudeLane))
    friendService.addOrUpdate(AddOrUpdateUserRequest("Faraway Friend", Numbers.TEST_RECIPIENT_PHONE_NUMBER_4, Locations._44W44thNY))
  }

  def toNames(friends : FindNearbyFriendsResponse) = friends.friends.map(_.name).toSet
  
  @Test
  def testNearbyFriends {
    val friends = friendService.findNearbyFriends(NearbyFriendsRequest.fromLocation(Locations._609Mission))
    Assert.assertEquals(Set("John Doe", "Chris E", "Mary Smith"), toNames(friends))
  }

  @Test
  def testNearbyFriends2 {
    val friends = friendService.findNearbyFriends(NearbyFriendsRequest.fromLocation(Locations._7ClaudeLane))
    Assert.assertEquals(Set("John Doe", "Chris E", "Mary Smith"), toNames(friends))
  }

  @Test
  def testFarawayFriend{
    val friends = friendService.findNearbyFriends(NearbyFriendsRequest.fromLocation(Locations._44W44thNY))
    Assert.assertEquals(Set("Faraway Friend"), toNames(friends))
  }


  @Test
  def testDeleteAllFriends {
    friendService.deleteAllFriends()
    val friends = friendService.findNearbyFriends(NearbyFriendsRequest.fromLocation(Locations._609Mission))
    Assert.assertEquals(Set(), toNames(friends))
  }

}