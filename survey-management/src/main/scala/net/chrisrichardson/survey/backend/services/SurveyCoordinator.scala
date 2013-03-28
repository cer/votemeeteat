package net.chrisrichardson.survey.backend.services

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import scala.collection.JavaConversions._
import net.chrisrichardson.survey.backend.domain.Participant
import org.springframework.beans.factory.annotation.Value
import SurveyUtil._
import net.chrisrichardson.survey.backend.domain.Survey
import org.joda.time.DateTime
import net.chrisrichardson.vme.common.messages.CreateSurveyRequest
import net.chrisrichardson.vme.twilio.TwilioService
import org.apache.commons.logging.LogFactory

@Component
class SurveyCoordinator {

  val logger = LogFactory.getLog(getClass())

  @Autowired
  var surveyService: SurveyManagementService = _

  @Autowired
  var twilioService: TwilioService = _

  @Value("${twilio.phone.number}")
  var twilioPhoneNumber: String = _

  @Scheduled(fixedDelay = 4 * 1000)
  def announceResultsForExpiredSurveys = surveyService.findExpiredSurveys().foreach (survey => surveyService.announceResults(survey.getId))

  @Scheduled(fixedDelay = 4 * 1000)
  def findParticipantsToNotify {
    for (participants <- Iterator.continually(surveyService.findParticipantsToNotify).takeWhile(!_.isEmpty)) {
      logger.info("Notifying participants: " + participants.size)
      notifyParticipants(participants)
      surveyService.noteParticipantsNotified(participants)
    }
  }

  def notifyParticipants(participants: List[Participant]) = participants.foreach(notifyParticipant)

  def notifyParticipant(participant: Participant) =
    ignoringErrors(twilioService.sendSms(participant.phoneNumber, participant.survey.prompt +  " Please call " + twilioPhoneNumber))

  def createSurvey(request : CreateSurveyRequest) {
    logger.info("Creating survey: " + request)
    val survey = Survey(request.prompt, 
                           request.participants.map(x => new Participant(x.name, x.phoneNumber)).toSet,
                           request.choices.toList,
                           new DateTime().plusSeconds(request.durationInSeconds))
    surveyService.add(survey)
    
  }
}