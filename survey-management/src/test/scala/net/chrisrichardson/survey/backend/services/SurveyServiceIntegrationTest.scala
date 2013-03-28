package net.chrisrichardson.survey.backend.services

import org.springframework.beans.factory.annotation.Autowired
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.TransactionStatus
import scala.collection.JavaConversions._
import net.chrisrichardson.survey.backend.domain.{SurveyTransactionHelper, SurveyMother, Survey, Participant}
import net.chrisrichardson.survey.backend.domain.Survey._
import junit.framework.Assert
import net.chrisrichardson.vme.testcommon.Numbers

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations = Array("classpath*:/appctx/*.xml"))
class SurveyServiceIntegrationTest {

  @Autowired
  var surveyManagementService: SurveyManagementService = _

  @Autowired
  var transactionHelper : SurveyTransactionHelper = _

  @Test
  def testSomething() {
    val survey = SurveyMother.makeSurvey

    surveyManagementService.add(survey)

    Assert.assertEquals(survey.id, surveyManagementService.findSurveyByCallerId(Numbers.TEST_RECIPIENT_PHONE_NUMBER).get.id)
    Assert.assertEquals(None, surveyManagementService.findSurveyByCallerId(Numbers.BOGUS_NUMBER_THAT_IS_NEVER_USED))

    transactionHelper.withTransaction {
      val rs = surveyManagementService.recordVote(Numbers.TEST_RECIPIENT_PHONE_NUMBER, 1)
      Assert.assertEquals(survey.id, rs.get.id)
      Assert.assertEquals("Bar", rs.get.mostPopularChoice.get)
    }
    transactionHelper.withTransaction {
      surveyManagementService.recordVote(Numbers.TEST_RECIPIENT_PHONE_NUMBER_2, 1)
      surveyManagementService.recordVote(Numbers.TEST_RECIPIENT_PHONE_NUMBER_3, 1)
    }

    val expired = surveyManagementService.findExpiredSurveys()
    Assert.assertTrue(expired.exists(_.id == survey.id))
  }

}