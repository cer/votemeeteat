package net.chrisrichardson.survey.backend.services

import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.ContextConfiguration
import org.springframework.beans.factory.annotation.Autowired
import net.chrisrichardson.vme.twilio.TwilioService
import org.junit.{Test, Before}
import net.chrisrichardson.survey.backend.domain.{SurveyTransactionHelper, SurveyMother, Survey}
import scala.collection.JavaConversions._
import net.chrisrichardson.twiliosimulator.backend.TwilioSimulator
import net.chrisrichardson.twiliosimulator.backend.logging.TwilioLogMetrics

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations = Array("classpath*:/appctx/**/*.xml"))
class SurveyManagementServiceEndToEndTest {

  @Autowired
  var twilioService: TwilioService = _

  @Autowired
  var twilioSimulator: TwilioSimulator = _

  @Autowired
  var surveyTransactionHelper : SurveyTransactionHelper = _

  @Before
  def initializeTwilioSimulator {
    twilioSimulator.ensureInitialized()
    twilioService.setTwilioUrl(twilioSimulator.baseUrl)
    System.setProperty("twilio.url", twilioSimulator.baseUrl)

    surveyTransactionHelper.deleteAllSurveys()

  }

  @Autowired
  var surveyManagementService: SurveyManagementService = _

  @Test
  def surveyManagementShouldContactParticipants {

    twilioSimulator.resetLogs()

    val survey = SurveyMother.makeSurvey

    surveyManagementService.add(survey)

    assertAllParticipantsNotified(survey)

    recordVotesForAllParticipants(survey)

    // FIXME Doing this prevents assertAllRecipientsNotifiedForResults from failing sometimes
    twilioSimulator.resetLogs()

    assertAllRecipientsNotifiedForResults(survey)

  }


  def assertAllRecipientsNotifiedForResults(s : Survey) {

    val numberOfParticipants = s.participants.size

    twilioSimulator.assertTwilioLogMetricsEventuallyEquals(TwilioLogMetrics(calls = numberOfParticipants, messages = numberOfParticipants))
  }

  def recordVotesForAllParticipants(survey: Survey) {
    survey.participants.foreach {
      participant => surveyManagementService.recordVote(participant.phoneNumber, 1)
    }
  }

  def assertAllParticipantsNotified(survey: Survey) {

    val allParticipants = survey.participants.map(_.phoneNumber).toSet

    twilioSimulator.assertSmsRecipientsEventuallyEquals(allParticipants)
  }

}
