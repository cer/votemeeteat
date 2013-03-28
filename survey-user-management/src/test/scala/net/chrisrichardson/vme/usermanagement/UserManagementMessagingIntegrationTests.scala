package net.chrisrichardson.vme.usermanagement

import geodb.FriendService
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.ContextConfiguration
import org.junit.{Before, Test}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.amqp.rabbit.core.RabbitTemplate
import net.chrisrichardson.vme.common.messages.NearbyFriendsRequest
import scala.collection.JavaConversions._
import net.chrisrichardson.vme.testutils.json.JsonMap._
import net.chrisrichardson.vme.testcommon.{Locations, Numbers}
import net.chrisrichardson.vme.testutils.misc.VmeAsserts._

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations = Array("/appctx/**/*.xml"))
class UserManagementMessagingIntegrationTests {

  @Autowired
  var rabbitTemplate: RabbitTemplate = _

  @Autowired
  var friendsService: FriendService = _


  @Before
  def initialize {
    friendsService.deleteAllFriends()
  }

  val loc = Locations.TEST_LOCATION

  val addUserMessage =
      Map( "name" ->  "C23343", "phoneNumber" -> Numbers.TEST_RECIPIENT_PHONE_NUMBER, "longitude" -> loc.lon, "latitude" -> loc.lat).toJson


  @Test
  def shouldCreateFriend {

    rabbitTemplate.convertAndSend("crudUsers", "crudUsers", addUserMessage)

    eventually { assertExists(friendsService.findNearbyFriends(NearbyFriendsRequest.fromLocation(loc)).friends) {_.name == "C23343"} }
  }

  @Test
  def shouldRespondToFindRequest {

    rabbitTemplate.convertAndSend("crudUsers", "crudUsers", addUserMessage)

    eventually { assertExists(friendsService.findNearbyFriends(NearbyFriendsRequest.fromLocation(loc)).friends) {_.name == "C23343"} }

    val findUsersMessage =
      Map( "longitude" -> loc.lon, "latitude" -> loc.lat).toJson

    val findUsersReply = rabbitTemplate.convertSendAndReceive("nearbyFriendsRequestExchange", "nearbyFriendsRequest", findUsersMessage)

    assertStringContains(findUsersReply.asInstanceOf[String], "C23343")
  }


}
