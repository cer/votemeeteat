package net.chrisrichardson.vme.twilio

import scala.xml.XML
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager
import org.apache.commons.httpclient.params.HttpClientParams
import org.apache.commons.httpclient.UsernamePasswordCredentials
import org.apache.commons.httpclient.auth.AuthScope
import org.springframework.http.client.CommonsClientHttpRequestFactory
import javax.annotation.PostConstruct
import org.springframework.util.LinkedMultiValueMap
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestTemplate
import scala.collection.JavaConversions._
import org.springframework.util.Assert
import org.apache.commons.lang.StringUtils
import org.springframework.web.client.HttpClientErrorException
import org.springframework.http.HttpStatus
import org.apache.commons.logging.LogFactory

@Component
class TwilioService {

  val logger = LogFactory.getLog(getClass())

	@Autowired
	var restTemplate : RestTemplate = _
	
	@Autowired
	var commonsHttpClientFactory : CommonsClientHttpRequestFactory = _
	
	@Value("${twilio.account.sid}")
	var accountSid : String = _

	@Value("${twilio.auth.token}")
	var authToken : String = _

	@Value("${twilio.phone.number}")
	var twilioPhoneNumber : String = _

	@Value("${twilio.url}" )
	var twilioUrl : String = _
	
	@Autowired
	var serverBaseUrlProvider : ServerBaseUrlProvider = _

	def serverBaseUrl = serverBaseUrlProvider.baseUrl
	
	def getServerBaseUrl = serverBaseUrl

  def setTwilioUrl(url : String) {
    logger.info("Setting TwilioURl to " + url)
    twilioUrl = url
  }

  def effectiveTwilioUrl = System.getProperty("twilio.url", twilioUrl)

  @PostConstruct
	def validateDependencies {
	  assertNotBlank(accountSid)
	  assertNotBlank(authToken)
	  assertNotBlank(accountSid)
	  assertNotBlank(twilioPhoneNumber)
	  assertNotBlank(twilioPhoneNumber)
	  assertNotBlank(twilioUrl)
	}
	
	def assertNotBlank(s : String) {
	  Assert.isTrue(StringUtils.isNotBlank(s))
	  Assert.isTrue(!s.startsWith("$"), s)
	}
	
	@PostConstruct
	def initializeHttpClient {
		commonsHttpClientFactory.getHttpClient().getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(accountSid, authToken));
		val params = new HttpClientParams
		params.setAuthenticationPreemptive(true)
		params.setConnectionManagerClass(classOf[MultiThreadedHttpConnectionManager])
		commonsHttpClientFactory.getHttpClient().setParams(params)
		
		logger.info("twilio.account.sid=" + accountSid)
    logger.info("TwilioURL=" + twilioUrl)

	}
	
	def postToTwilio(resourcePath : String, requestParams : Map[String, String]) = {
		val entity = makeEntity(requestParams)
 
		try {
  		val response = restTemplate.postForObject(effectiveTwilioUrl + "/Accounts/{accountSid}/{resource}",
  		                                            entity, classOf[String], accountSid, resourcePath)
  		XML.loadString(response)
		} catch {
		  case e : HttpClientErrorException if e.getStatusCode == HttpStatus.BAD_REQUEST =>
		    val body = e.getResponseBodyAsString()
		    val xmlBody = XML.loadString(body)
		    val code = Integer.parseInt((xmlBody \\ "Code").text)
		    val message = (xmlBody \\ "Message").text
		    throw new TwilioRestException(message, code)
		}
	}


  def makeEntity(requestParams: Map[String, String]) = {
    val request = new LinkedMultiValueMap[String, String]()
    for ((key, value) <- requestParams)
      request.put(key, List(value))

    val headers = new HttpHeaders()
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED)
    val entity = new HttpEntity(request, headers)
    entity
  }

  def getFromTwilio(resourcePath : String) = {
	  val response = restTemplate.getForObject(effectiveTwilioUrl + "/Accounts/{accountSid}/{resource}", classOf[String], accountSid, resourcePath)
	  XML.loadString(response)
	}

	def sendSms(recipient : String, message : String) = {
		val response = postToTwilio("SMS/Messages", 
		    Map("From" -> twilioPhoneNumber, "To" -> recipient, "Body" -> message))
		(response \ "SMSMessage" \ "Sid").text
	}

	def getSmsStatus(sid : String) = {
	  val response = getFromTwilio("SMS/Messages/" + sid)
	  (response \ "SMSMessage" \ "Status").text
	}
	
	def call(recipient : String, url : String) = {
		val response = postToTwilio("Calls", Map("From" -> twilioPhoneNumber, "To" -> recipient, "Url" -> (serverBaseUrl + url)))
    (response \ "Call" \ "Sid").text
	}
	
}