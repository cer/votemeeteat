package net.chrisrichardson.vme.restaurantservice

import java.util.concurrent.Future
import net.chrisrichardson.vme.common.domain.Location

trait RestaurantService {
  
  def findNearbyRestaurants(location: Location) : Future[FindNearbyRestaurantResponse]

}