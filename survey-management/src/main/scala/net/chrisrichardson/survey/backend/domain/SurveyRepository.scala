package net.chrisrichardson.survey.backend.domain

import org.joda.time.DateTime

trait SurveyRepository {

  def save(survey : Survey)
  def findOne(id : Long) : Survey
	def findSurveyByCallerId(callerId : String) : Option[Survey]
	def findExpiredSurveys(cutoffTime : DateTime) : java.util.List[Survey]
  
  def findParticipantsToNotify : List[Participant]
  
  def merge(participant : Participant) : Participant

}