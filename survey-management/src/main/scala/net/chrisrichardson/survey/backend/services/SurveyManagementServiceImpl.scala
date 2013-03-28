package net.chrisrichardson.survey.backend.services

import org.joda.time.DateTime
import net.chrisrichardson.survey.backend.domain._
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import scala.collection.JavaConversions._
import org.springframework.beans.factory.annotation.Value

import SurveyUtil._
import net.chrisrichardson.vme.twilio.TwilioService
import org.apache.commons.logging.LogFactory

@Service
class SurveyManagementServiceImpl extends SurveyManagementService {
  val logger = LogFactory.getLog(getClass());

  @Autowired
  var surveyRepository: SurveyRepository = _

  @Autowired
  var twilioService: TwilioService = _

  @Value("${twilio.phone.number}")
  var twilioPhoneNumber: String = _

  @Autowired
  var surveyProgressTracker : SurveyProgressTracker = _
  
  override def findSurveyByCallerId(callerId: String) = surveyRepository.findSurveyByCallerId(callerId)

  override def add(survey: Survey) = {
    surveyRepository.save(survey)
  }

  override def recordVote(callerId: String, vote: Int) = {
    val optionalSurvey = surveyRepository.findSurveyByCallerId(callerId)
    for (survey <- optionalSurvey;
        participant <- survey.recordVote(callerId, vote)) {
      surveyProgressTracker.noteVoted(participant) 
    }
    optionalSurvey
  }

  override def announceResults(surveyId: java.lang.Long) = announceResultsOfSurvey(surveyRepository.findOne(surveyId))

  def announceResultsOfSurvey(survey: Survey) = survey.participantsWhoResponded.foreach(announceResultsToParticipant(_, survey))

  def announceResultsToParticipant(participant: Participant, survey: Survey) = {
    ignoringErrors(twilioService.call(participant.phoneNumber, "getresults.html?surveyId=" + survey.getId))
    val message = survey.mostPopularChoice match {
      case Some(popularChoice) => "Lunch is at " + popularChoice
      case None => "No one wants lunch today. Sorry"
    }
    ignoringErrors(twilioService.sendSms(participant.phoneNumber, message))
  }

  override def findSurvey(surveyId: Long) = surveyRepository.findOne(surveyId)

  override def findExpiredSurveys() = {
    logger.info("Finding expired")
    val expired = surveyRepository.findExpiredSurveys(new DateTime())
    if (expired.isEmpty())
      logger.info("Nothing to expire")
    else {
      logger.info("***expired=" + expired)
      for (survey <- expired) {
        survey.noteCompleted
        surveyProgressTracker.noteCompleted(survey)
      }
    }
    expired
  }
  
  
  def findParticipantsToNotify() = {
    val participants = surveyRepository.findParticipantsToNotify
    for (p <- participants) {
      p.noteNotifying()
      surveyProgressTracker.noteNotifyingParticipant(p)
    }
    participants
  }
  
  def noteParticipantsNotified(participants : List[Participant]) {
    for (p <- participants) {
      val q = surveyRepository.merge(p)
      q.noteNotified()
      surveyProgressTracker.noteNotifyingParticipant(q)
    }
  }


}