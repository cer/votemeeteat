package net.chrisrichardson.vme.vmemanagement.services

import net.chrisrichardson.vme.common.messages._
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import scala.collection.JavaConversions._

import net.chrisrichardson.vme.restaurantservice.RestaurantService
import java.util.concurrent.TimeUnit
import net.chrisrichardson.vme.common.messages.CreateSurveyRequest
import net.chrisrichardson.vme.common.messages.ParticipantInfo
import net.chrisrichardson.vme.common.messages.FriendInfo
import net.chrisrichardson.vme.vmemanagement.messages.CreateVmeRequest
import net.chrisrichardson.vme.vmemanagement.externalservices.{SurveyService, FriendService}
import net.chrisrichardson.vme.common.domain.Location
import org.apache.commons.logging.LogFactory

@Service
class VmeManagementServiceImpl extends VmeManagementService {

  val logger = LogFactory.getLog(getClass())

  @Autowired
  var mongoTemplate: MongoTemplate = _

  @Autowired
  var friendsService: FriendService = _

  @Autowired
  var restaurantService: RestaurantService = _

  @Autowired
  var surveyService: SurveyService = _

  override def createVme(request: CreateVmeRequest) {
    logger.info("createVme called: " + request)
    val vmeRecord = new VmeRecord(name = request.name,
      phoneNumber = request.phoneNumber,
      location = new Location(lon = request.longitude, lat = request.latitude))

    mongoTemplate.save(vmeRecord)

    // TODO implement a mechanism to handle failure at this point:
    //  Look for VMEs without a survey and resend
    //  Duplicate detection on the other end

    val f1 = friendsService.findNearbyFriends(NearbyFriendsRequest.fromLocation(vmeRecord.location))
    val f2 = restaurantService.findNearbyRestaurants(vmeRecord.location)
    val nearbyFriends = f1.get(2, TimeUnit.SECONDS)
    val nearbyRestaurants = f2.get(2, TimeUnit.SECONDS)

    val csr = CreateSurveyRequest(
      correlationId = vmeRecord.id,
      prompt = vmeRecord.name + " would like to meet and eat",
      participants = nearbyFriends.friends.toSet.map((f: FriendInfo) => ParticipantInfo(f.name, f.phoneNumber)),
      choices = nearbyRestaurants.restaurants.map(_.name),
      responsesNeeded = 5,
      durationInSeconds = 600)

    logger.info("****** csr=" + csr)
    surveyService.createSurvey(csr)
  }


}