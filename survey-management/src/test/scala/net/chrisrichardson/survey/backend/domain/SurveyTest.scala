package net.chrisrichardson.survey.backend.domain

import junit.framework.Assert._
import Survey._
import org.junit.Test
import junit.framework.Assert
import net.chrisrichardson.vme.testcommon.Numbers

class SurveyTest {

  @Test
  def test {
    val survey = SurveyMother.makeSurvey
    survey.recordVote(Numbers.TEST_RECIPIENT_PHONE_NUMBER, 1)
    assertEquals(Some("Bar"), survey.mostPopularChoice)
  }

  @Test
  def testNoResponseYet {
    val survey = Survey("Chris wants to ask a question",
      Participants(Participant("Chris", Numbers.TEST_RECIPIENT_PHONE_NUMBER)),
      Choices("Foo", "Bar"),
      DurationInMinutes(10)
    )
    assertEquals(None, survey.mostPopularChoice)
  }

  @Test
  def testMultipleResponses {
    val survey = SurveyMother.makeSurvey
    survey.recordVote(Numbers.TEST_RECIPIENT_PHONE_NUMBER, 0)
    survey.recordVote(Numbers.TEST_RECIPIENT_PHONE_NUMBER_2, 0)
    survey.recordVote(Numbers.TEST_RECIPIENT_PHONE_NUMBER_3, 1)
    assertEquals(Some("Foo"), survey.mostPopularChoice)
    Assert.assertTrue(survey.isReadyToComplete)
  }

  @Test
  def testMultipleTie {
    val survey = SurveyMother.makeSurvey
    survey.recordVote(Numbers.TEST_RECIPIENT_PHONE_NUMBER, 0)
    survey.recordVote(Numbers.TEST_RECIPIENT_PHONE_NUMBER_2, 1)
    assertEquals(Some("Foo"), survey.mostPopularChoice)
  }

  @Test
  def oneRespondent {
    val survey = SurveyMother.makeSurvey
    survey.recordVote(Numbers.TEST_RECIPIENT_PHONE_NUMBER_3, 1)
    assertEquals(Some("Bar"), survey.mostPopularChoice)
  }
}