package net.chrisrichardson.vme.restaurantservice


case class FindNearbyRestaurantResponse(restaurants : List[RestaurantInfo])

case class RestaurantInfo(name : String)