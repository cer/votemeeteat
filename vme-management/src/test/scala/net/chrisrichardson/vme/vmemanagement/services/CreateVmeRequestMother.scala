package net.chrisrichardson.vme.vmemanagement.services

import net.chrisrichardson.vme.vmemanagement.messages.CreateVmeRequest
import net.chrisrichardson.vme.testcommon.{Locations, Numbers}

object CreateVmeRequestMother {

  def makeCreateVmeRequest() = CreateVmeRequest(name="foo", phoneNumber=Numbers.TEST_RECIPIENT_PHONE_NUMBER, longitude= Locations.TEST_LONGITUDE, latitude= Locations.TEST_LATITUDE)
}
