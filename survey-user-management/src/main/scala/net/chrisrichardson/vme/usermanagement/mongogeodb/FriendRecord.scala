package net.chrisrichardson.vme.usermanagement.mongogeodb

import org.springframework.data.mongodb.core.geo.Point

case class FriendRecord(name : String, id : String, location : Point)