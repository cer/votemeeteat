package net.chrisrichardson.survey.backend.domain

import org.springframework.beans.factory.annotation.Autowired
import junit.framework.Assert._
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import net.chrisrichardson.vme.testcommon.Numbers

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations = Array("classpath*:/appctx/*.xml"))
class SurveyPersistenceTest {
	
	@Autowired
	var surveyRepository : SurveyRepository = _

  @Autowired
  var transactionHelper : SurveyTransactionHelper = _
  
	@Test
	def testSomething() {
		val survey = SurveyMother.makeSurvey
	
		transactionHelper.withTransaction { 
		  surveyRepository.save(survey)
		} 
		
		val loadedSurvey = surveyRepository.findOne(survey.getId);
		
		assertEquals(survey.prompt, loadedSurvey.prompt)
	}


  @Test
	def testFindByCallerId_none() {
		assertEquals(None, surveyRepository.findSurveyByCallerId(Numbers.BOGUS_NUMBER_THAT_IS_NEVER_USED))
	}

  @Test
	def testFindByCallerId_one() {
		val survey = SurveyMother.makeSurvey
	  transactionHelper.withTransaction { 
      surveyRepository.save(survey)
    } 

		val loadedSurvey = surveyRepository .findSurveyByCallerId(Numbers.TEST_RECIPIENT_PHONE_NUMBER)
		assertEquals(survey.getId, loadedSurvey.get.getId)
	}

  @Test
  def testFindParticipantsToNotify {
    val survey = SurveyMother.makeSurvey

    transactionHelper.deleteAllSurveys()

    transactionHelper.withTransaction { 
      surveyRepository.save(survey)
    } 
    
    val participants = surveyRepository.findParticipantsToNotify
    assertEquals(survey.participants.size(), participants.size)
  }
	
}