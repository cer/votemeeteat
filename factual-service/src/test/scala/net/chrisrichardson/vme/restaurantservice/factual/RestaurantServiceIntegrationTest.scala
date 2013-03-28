package net.chrisrichardson.vme.restaurantservice.factual

import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.junit.Test
import org.junit.Assert
import net.chrisrichardson.vme.restaurantservice.RestaurantService
import java.util.concurrent.TimeUnit
import net.chrisrichardson.vme.testcommon.Locations

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations = Array("classpath*:/appctx/**/*.xml"))
class RestaurantServiceIntegrationTest {

    @Autowired
    var restaurantService : RestaurantService = _
    
    @Test
    def restaurantShouldBeFound {
      val loc = Locations.TEST_LOCATION

      val response = restaurantService.findNearbyRestaurants(loc).get(2, TimeUnit.SECONDS)
      Assert.assertTrue(response.restaurants.map(_.name).contains("Hunan Yuan Restaurant"))

      val response2 = restaurantService.findNearbyRestaurants(loc).get(2, TimeUnit.SECONDS)
      Assert.assertTrue(response2.restaurants.map(_.name).contains("Hunan Yuan Restaurant"))
    }
}