package net.chrisrichardson.survey.backend.services
import net.chrisrichardson.survey.backend.domain.Participant
import net.chrisrichardson.survey.backend.domain.Survey

trait SurveyProgressTracker {
  
  def noteNotifyingParticipant(participant : Participant)
  def noteNotifiedParticipant(participant : Participant)
  def noteVoted(participant : Participant)
  
  def noteCompleted(survey : Survey)
  

}