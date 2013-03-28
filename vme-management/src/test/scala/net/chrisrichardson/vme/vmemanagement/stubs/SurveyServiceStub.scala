package net.chrisrichardson.vme.vmemanagement.stubs

import org.springframework.stereotype.Component
import net.chrisrichardson.vme.common.messages.CreateSurveyRequest
import collection.mutable.ArrayBuffer

@Component
class SurveyServiceStub extends ExternalServiceStub[CreateSurveyRequest] {


  def createSurvey(request : CreateSurveyRequest) {
    _requests += request
  }


}
