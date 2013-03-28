package net.chrisrichardson.survey.web.controllers
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import javax.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.RequestParam
import net.chrisrichardson.survey.backend.services.SurveyManagementService
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.cloudfoundry.org.codehaus.jackson.map.ObjectMapper
import net.chrisrichardson.vme.common.messages.CreateSurveyRequest
import net.chrisrichardson.survey.backend.domain.Survey
import scala.collection.JavaConversions._
import net.chrisrichardson.survey.backend.domain.Participant
import org.joda.time.DateTime
import net.chrisrichardson.vme.common.domain.PhoneNumber
import org.apache.commons.logging.LogFactory

@Controller
class TempSurveyController {
  val logger = LogFactory.getLog(getClass())

  @Autowired
  var surveyManagementService : SurveyManagementService = _

   @RequestMapping(value=Array("/survey"), method = Array(RequestMethod.POST))
  def createSurvey(@RequestBody json : String, resp : HttpServletResponse )  {
     logger.debug("json=" + json)
     val mapper = new ObjectMapper();
     val request = mapper.readValue(json, classOf[CreateSurveyRequest])
     val survey = Survey(request.prompt, 
                           request.participants.map(x => new Participant(x.name, x.phoneNumber)).toSet,
                           request.choices.toList,
                           new DateTime().plusSeconds(request.durationInSeconds));
     // FIXME request.responsesNeeded
     surveyManagementService.add(survey)
  }

}