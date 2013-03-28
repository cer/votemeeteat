package net.chrisrichardson.vme.vmemanagement.externalservices

import net.chrisrichardson.vme.common.messages.CreateSurveyRequest

trait SurveyService {

    def createSurvey(request : CreateSurveyRequest)
}