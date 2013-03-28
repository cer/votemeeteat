package net.chrisrichardson.twiliosimulator.web

import org.springframework.web.client.{HttpClientErrorException, RestTemplate}
import org.junit.{Assert, Test, Before}
import org.springframework.http.{HttpEntity, MediaType, HttpHeaders, HttpStatus}
import org.springframework.util.LinkedMultiValueMap
import xml.XML
import scala.collection.JavaConversions._
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.ContextConfiguration
import org.springframework.beans.factory.annotation.Autowired
import net.chrisrichardson.twiliosimulator.backend.TwilioSimulator
import net.chrisrichardson.twiliosimulator.backend.logging.RequestLog


@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations = Array("/appctx/**/*.xml"))
class TwilioSimulatorTest {

  var restTemplate: RestTemplate = _

  @Autowired
  var twilioSimulator : TwilioSimulator = _

  @Before
  def initialize() {
    twilioSimulator.ensureInitialized()
    restTemplate = new RestTemplate()
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

  case class TwilioRestException(message: String, code: Int) extends RuntimeException(message)

  def postToTwilio(resourcePath: String, requestParams: Map[String, String]) = {
    val entity = makeEntity(requestParams)

    try {
      val response = restTemplate.postForObject(twilioSimulator.makeUrl("Accounts/MySide/{resource}"),
        entity, classOf[String], resourcePath)
      XML.loadString(response)
    } catch {
      case e: HttpClientErrorException if e.getStatusCode == HttpStatus.BAD_REQUEST =>
        val body = e.getResponseBodyAsString()
        val xmlBody = XML.loadString(body)
        val code = Integer.parseInt((xmlBody \\ "Code").text)
        val message = (xmlBody \\ "Message").text
        throw new TwilioRestException(message, code)
    }
  }

  @Test
  def call {
    val from = "+15105551212"
    val to = "xxx"
    val callbackUrl = "http://somesite.com"
    val response = postToTwilio("Calls", Map("From" -> from, "To" -> to, "Url" -> callbackUrl))
    val sid = (response \ "Call" \ "Sid").text.trim()
    RequestLog.assertCallExists(RequestLog.Call(sid = sid, from = from, to = to, url = callbackUrl))

  }

  @Test
  def callToBadNumber {
    val from = "+15105551212"
    val to ="+1510555"
    val callbackUrl = "http://somesite.com"
    try {
      postToTwilio("Calls", Map("From" -> from, "To" -> to, "Url" -> callbackUrl))
      Assert.fail("Exception expected");
    } catch {
      case TwilioRestException(_, 21211)  =>
    }

  }

  @Test
  def sendSms {
    val from = "+15105551212"
    val to = "xxx"
    val body = "yyy"
    val response = postToTwilio("SMS/Messages", Map("From" -> from, "To" -> to, "Body" -> body))
    val sid = (response \ "SMSMessage" \ "Sid").text.trim()
    RequestLog.assertSmsExists(RequestLog.SmsMessage(sid = sid, from = from, to = to, body = body))
  }

}
