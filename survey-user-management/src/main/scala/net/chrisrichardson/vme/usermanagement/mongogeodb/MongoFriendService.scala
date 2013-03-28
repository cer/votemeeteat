package net.chrisrichardson.vme.usermanagement.mongogeodb

import com.mongodb.BasicDBObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.geo.Point
import collection.JavaConversions._
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import net.chrisrichardson.vme.common.messages.NearbyFriendsRequest
import net.chrisrichardson.vme.common.messages.FindNearbyFriendsResponse
import net.chrisrichardson.vme.common.messages.FriendInfo
import org.springframework.data.mongodb.core.query.NearQuery
import org.springframework.data.mongodb.core.geo.Distance
import org.springframework.data.mongodb.core.geo.Metrics
import net.chrisrichardson.vme.usermanagement.geodb.FriendService
import net.chrisrichardson.vme.usermanagement.messages.AddOrUpdateUserRequest
import org.apache.commons.logging.LogFactory

@Component
class MongoFriendService extends FriendService {

  val logger = LogFactory.getLog(getClass())

  @Autowired
  var mongoTemplate: MongoTemplate = _

  @PostConstruct
  def createGeoIndex {
    val dbo = new BasicDBObject
    dbo.put("location", "2d")
    mongoTemplate.getCollection("friendRecord").ensureIndex(dbo)
  }

  override def addOrUpdate(request: AddOrUpdateUserRequest) = {
    logger.info("addOrUpdate called: " + request)
    val name = request.name
    val phoneNumber = request.phoneNumber
    val fr = new FriendRecord(name, phoneNumber, new Point(request.longitude, request.latitude))
    mongoTemplate.save(fr)
  }

  override def findNearbyFriends(request: NearbyFriendsRequest) = {
    logger.info("findNearbyFriends called: " + request)
    val location = new Point(request.longitude, request.latitude)
    val query = NearQuery.near(location).maxDistance(new Distance(3, Metrics.MILES))
    val result = mongoTemplate.geoNear(query, classOf[FriendRecord])
    val nearby = result.getContent.map(_.getContent)
    FindNearbyFriendsResponse(nearby.map(f => FriendInfo(f.name, f.id)))
  }
  
  override def deleteAllFriends() {
    mongoTemplate.remove(new Query, classOf[FriendRecord])
  }
}