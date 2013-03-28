package net.chrisrichardson.survey.backend.services
import net.chrisrichardson.survey.backend.domain.Participant
import net.chrisrichardson.survey.backend.domain.Survey
import org.springframework.stereotype.Component
import org.apache.commons.logging.LogFactory

@Component
class SurveyProgressTrackerImpl extends SurveyProgressTracker {

  val logger = LogFactory.getLog(getClass())

  def noteNotifyingParticipant(participant: Participant) {
    logger.info("notifying participant: " + participant.phoneNumber)
  }
  
  def noteNotifiedParticipant(participant: Participant) {
    logger.info("notified participant: " + participant.phoneNumber)
  }
  
  def noteVoted(participant: Participant) {
    logger.info("note voted: " + participant.phoneNumber)
  }

  def noteCompleted(survey: Survey) {
    logger.info("note completed: " + survey.getId)
  }

}