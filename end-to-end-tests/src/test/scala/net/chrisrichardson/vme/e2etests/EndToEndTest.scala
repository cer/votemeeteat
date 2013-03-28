package net.chrisrichardson.vme.e2etests

import org.junit.{Assert, Test}
import org.springframework.beans.factory.annotation.{Value, Autowired}
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.ContextConfiguration
import java.util.concurrent.TimeUnit
import net.chrisrichardson.polyglotpersistence.webtestutil.JettyLauncher
import net.chrisrichardson.vme.testutils.standalone.StandaloneApplicationLauncher
import StandaloneApplicationLauncher._
import org.springframework.web.client.RestTemplate
import org.springframework.util.LinkedMultiValueMap
import org.springframework.http.{HttpEntity, MediaType, HttpHeaders}
import xml.XML
import scala.collection.JavaConversions._
import org.apache.commons.logging.LogFactory
import net.chrisrichardson.vme.testutils.json.JsonMap._
import net.chrisrichardson.vme.testcommon.{Numbers, Locations}
import net.chrisrichardson.twiliosimulator.backend.TwilioSimulator
import net.chrisrichardson.twiliosimulator.backend.logging.TwilioLogMetrics

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations = Array("classpath*:/appctx/**/*.xml"))
class EndToEndTest {

  val TEST_USER = "C23343"

  val logger = LogFactory.getLog(getClass)

  @Autowired
  var twilioSimulator: TwilioSimulator = _


  @Autowired
  var rabbitTemplate: RabbitTemplate = _

  @Value("${factual_consumer_key}")
  var consumerKey: String = _

  @Value("${factual_consumer_secret}")
  var consumerSecret: String = _

  var jettyForSurveyManagementApp: JettyLauncher = _


  @Test
  def endToEndTest {
    twilioSimulator.ensureInitialized()
    System.setProperty("twilio.url", twilioSimulator.baseUrl)

    // FIXME - Need to add hsqldb.jar so that it can use <scope>test</scope>
    // Or test against mysql

    jettyForSurveyManagementApp = new JettyLauncher();
    jettyForSurveyManagementApp.setContextPath("/webapp");
    jettyForSurveyManagementApp.setPort(-1);
    jettyForSurveyManagementApp.setSrcWebApp("../survey-management/target/survey-management-1.0-SNAPSHOT.war");
    jettyForSurveyManagementApp.start();

    val properties = Map("factual_consumer_key" -> consumerKey,
      "factual_consumer_secret" -> consumerSecret,
      "factual.execution.isolation.thread.timeoutInMilliseconds" -> "3000",
      "factual.threadpool.coreSize" -> "10"
    )

    properties.foreach {
      p => System.setProperty(p._1, p._2)
    }

    logger.info("Starting apps")

    withStandaloneApp("../vme-management/target/appassembler/repo", "net.chrisrichardson.vme.vmemanagement.backend.main.VmeManagementMain") {
      withStandaloneApp("../survey-user-management/target/appassembler/repo", "net.chrisrichardson.vme.usermanagement.main.UserManagementMainProgram") {

        createUser()

        TimeUnit.SECONDS.sleep(5)

        createVme()

        assertAllParticipantsNotified()

        vote()

        assertAllParticipantsNotifiedOfOutcome()
      }
    }

  }

  def assertAllParticipantsNotifiedOfOutcome() {
    val numberOfParticipants = 1
    twilioSimulator.assertTwilioLogMetricsEventuallyEquals(TwilioLogMetrics(calls=numberOfParticipants, messages=numberOfParticipants * 2))
  }

  def vote() {
    val restTemplate = new RestTemplate()

    val beginCallResponse = postToTwilio(restTemplate, jettyForSurveyManagementApp.makeUrl("begincall.html"), Map("From" -> Numbers.TEST_RECIPIENT_PHONE_NUMBER))

    Assert.assertEquals("handleresponse.html", beginCallResponse \\ "Gather" \ "@action" text)

    val voteResponse = postToTwilio(restTemplate, jettyForSurveyManagementApp.makeUrl("handleresponse.html"), Map("From" -> Numbers.TEST_RECIPIENT_PHONE_NUMBER, "Digits" -> "1"))

    logger.info("voteresponse=" + voteResponse)

    Assert.assertEquals(2, voteResponse \ "Say" length)
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


  def postToTwilio(restTemplate: RestTemplate, url: String, requestParams: Map[String, String]) = {
    val entity = makeEntity(requestParams)
    val response = restTemplate.postForObject(url, entity, classOf[String])
    XML.loadString(response)
  }


  def assertAllParticipantsNotified() {
    val allParticipants = Set(Numbers.TEST_RECIPIENT_PHONE_NUMBER)
    twilioSimulator.assertSmsRecipientsEventuallyEquals(allParticipants)
  }


  def createVme() {
    val createVmeMessage =
      Map( "name" -> TEST_USER, "phoneNumber" -> Numbers.TEST_RECIPIENT_PHONE_NUMBER, "longitude" -> Locations.TEST_LONGITUDE, "latitude" -> Locations.TEST_LATITUDE ).toJson

    rabbitTemplate.convertAndSend("scheduleVme", "scheduleVme", createVmeMessage)
  }

  def createUser() {
    val addUserMessage =
      Map( "name" -> TEST_USER, "phoneNumber" -> Numbers.TEST_RECIPIENT_PHONE_NUMBER, "longitude" -> Locations.TEST_LONGITUDE, "latitude" -> Locations.TEST_LATITUDE ).toJson

    rabbitTemplate.convertAndSend("crudUsers", "crudUsers", addUserMessage)
  }


}
