package net.chrisrichardson.vme.restaurantservice.factual

import scala.collection.JavaConversions._
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.{Qualifier, Autowired, Value}
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import com.factual.driver.Circle
import com.factual.driver.Factual
import com.factual.driver.Query
import javax.annotation.PostConstruct
import net.chrisrichardson.vme.restaurantservice.{FindNearbyRestaurantResponse, RestaurantInfo, RestaurantService}
import com.netflix.hystrix.{HystrixCommandKey, HystrixCommand}
import net.chrisrichardson.vme.common.domain.Location


@Service
class FactualRestaurantService extends RestaurantService {

  @Value("${factual_consumer_key}")
  var consumerKey: String = _

  @Value("${factual_consumer_secret}")
  var consumerSecret: String = _

  var factual: Factual = _

  @PostConstruct
  def validateCredentials {
    Assert.isTrue(StringUtils.isNotBlank(consumerKey))
    Assert.isTrue(StringUtils.isNotBlank(consumerSecret))
    factual = new Factual(consumerKey, consumerSecret, true)
  }

  @Autowired
  @Qualifier("factualHystrixConfig")
  var factualHystrixConfig: HystrixCommand.Setter = _

  override def findNearbyRestaurants(location: Location) = {

    class FindRestaurantsCommand extends HystrixCommand[FindNearbyRestaurantResponse](factualHystrixConfig.andCommandKey(HystrixCommandKey.Factory.asKey("FindRestaurantsCommand"))) {

      override def run() = {
        val restaurants = factual.fetch("restaurants", new Query().within(new Circle(location.lat, location.lon, 1000)).limit(5))
        val rs = restaurants.getData map { map => RestaurantInfo(map.get("name").asInstanceOf[String]) }
        FindNearbyRestaurantResponse(rs.toList)
      }
    }

    new FindRestaurantsCommand().queue()
  }

}