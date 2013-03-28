package net.chrisrichardson.vme.twilio

import junit.framework.Assert
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.{Value, Autowired}

import org.junit.Test
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
;

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations = Array("classpath*:/appctx/**/*.xml"))
class TwilioServiceEndToEndTest {
	
	@Autowired
	var twilioService : TwilioService = _

  @Value("${twilio.real.test.number}")
  var realPhoneNumber : String = _

	@Test
	def testSendSms {
		val sid = twilioService.sendSms(realPhoneNumber, "Please call me asap")
		assertNotBlank(sid)
	}


  def assertNotBlank(sid: String) {
    Assert.assertTrue(StringUtils.isNotBlank(sid))
  }

  @Test
	def testCall  {
	  val sid = twilioService.call(realPhoneNumber, "announceresults.html")
    assertNotBlank(sid)
	}

  @Test
  def testCallToBogusNumber  {
    try {
      twilioService.call("+15005550001", "announceresults.html")
      Assert.fail("Exception expected")
    } catch {
      case TwilioRestException(_, 21217)  =>
    }
  }
}