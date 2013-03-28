package net.chrisrichardson.survey.backend.domain

import net.chrisrichardson.survey.backend.domain.Survey.{DurationInMinutes, Choices, Participants}
import net.chrisrichardson.vme.testcommon.Numbers

object SurveyMother {

  def makeSurvey =
    Survey("Chris wants to ask a question",
      Participants(Participant("Chris", Numbers.TEST_RECIPIENT_PHONE_NUMBER), Participant("Joe", Numbers.TEST_RECIPIENT_PHONE_NUMBER_2), Participant("Tom", Numbers.TEST_RECIPIENT_PHONE_NUMBER_3)),
      Choices("Foo", "Bar"),
      DurationInMinutes(10))

}
