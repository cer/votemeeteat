package net.chrisrichardson.survey.web.controllers

import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import net.chrisrichardson.survey.backend.services._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import scala.collection.JavaConversions._
import org.apache.commons.logging.LogFactory

@Controller
class TwilioController {

  val logger = LogFactory.getLog(getClass())

  @Autowired
  var surveyManagementService: SurveyManagementService = _

  /*
	 * Invoked by twilio when a user calls in
	 */
  
  @RequestMapping(value = Array("/begincall.html")) 
  @ResponseBody
  def beginCall(@RequestParam("From") callerId: String) = {
    surveyManagementService.findSurveyByCallerId(callerId) match {
      case None => 
        <Response>
          <Say>Sorry don't recognize your number</Say>
          <Hangup/>
        </Response>
      case Some(survey) =>
        <Response>
          <Say>{ survey.prompt }</Say>
          <Gather action="handleresponse.html" method="POST" numDigits="1">
            {
              for ((choice, index) <- survey.choices zipWithIndex)
                yield <Say>Press { index } for { choice }</Say>
            }
          </Gather>
          <Say>We are sorry you could not decide</Say>
          <Hangup/>
        </Response>
    }
  }

  @RequestMapping(value = Array("/handleresponse.html"))
  @ResponseBody
  def handleUserResponse(@RequestParam("From") callerId: String, @RequestParam("Digits") digits: Int) = {
    val survey = surveyManagementService.recordVote(callerId, digits)
    <Response>
       <Say>Thank you for choosing. 
            The most popular place so far is { survey.map(_.mostPopularChoice) getOrElse "oops" } 
        </Say>
       <Pause/>
       <Say>You will hear from us soon. Good bye</Say>
       <Hangup/>
    </Response>
  }

  @RequestMapping(value = Array("/callstatus.html"))
  def callStatus(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    logger.info("callStatus params=" + request.getParameterMap())
  }

  @RequestMapping(value = Array("/getresults.html")) // , method = Array(RequestMethod.POST)
  def getresults(request: HttpServletRequest, response: HttpServletResponse, @RequestParam("surveyId") surveyId: Long): Unit = {
    val survey = surveyManagementService.findSurvey(surveyId)
    survey.mostPopularChoice match {
      case Some(mostPopularChoice) => <Response>
                                        <Say>It's lunch time!</Say>
                                        <Pause/>
                                        <Say>Let's meet for lunch at { mostPopularChoice }</Say>
                                        <Pause/>
                                        <Say>Once again, let's meet for lunch at { mostPopularChoice } . See you there</Say>
                                        <Hangup/>
                                      </Response>
      case None => <Response>
                     <Say>Sadly none of your friends like you</Say>
                     <Pause/>
                     <Say>Once again, you are eating alone</Say>
                     <Hangup/>
                   </Response>
    }
  }

}