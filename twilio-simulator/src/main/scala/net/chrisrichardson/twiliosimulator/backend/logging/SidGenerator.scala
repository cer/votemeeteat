package net.chrisrichardson.twiliosimulator.backend.logging

object SidGenerator {
  var counter = 0

  def nextSid() = {
    counter += 1
    "sid" + counter
  }
}
