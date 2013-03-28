package net.chrisrichardson.survey.backend.services

import net.chrisrichardson.survey.backend.domain.Survey
import net.chrisrichardson.survey.backend.domain.Participant

trait SurveyManagementService {
	
	def add(survey : Survey) : Unit
	def findSurveyByCallerId(callerId : String) : Option[Survey]
	def recordVote(callerId : String, vote : Int) : Option[Survey]
	def announceResults(surveyId : java.lang.Long) : Unit
	def findSurvey(surveyId : Long) : Survey
	def findExpiredSurveys() : java.util.List[Survey]
	
	def findParticipantsToNotify() : List[Participant]
	
	def noteParticipantsNotified(participants : List[Participant])

}