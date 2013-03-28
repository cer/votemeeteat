package net.chrisrichardson.twiliosimulator.backend

import logging.{TwilioLogMetrics, RequestLog}
import net.chrisrichardson.polyglotpersistence.webtestutil.JettyLauncher
import org.springframework.stereotype.Component
import junit.framework.Assert

import net.chrisrichardson.testutils.streams.DelayedStream._

@Component
class TwilioSimulator {

  def baseUrl = jetty.getBaseUrl()

  var jetty : JettyLauncher = null

  def ensureInitialized() {
    if (jetty == null) {
      jetty = new JettyLauncher();
      jetty.setContextPath("/webapp");
      jetty.setPort(-1);
      jetty.setSrcWebApp("../twilio-simulator/src/main/webapp");
      jetty.start();
    }
  }

  def makeUrl(path: String) = jetty.makeUrl(path)

  def smsLog = RequestLog.smsLog
  def callLog = RequestLog.callLog
  def smsRecipients = smsLog.map(_.to).toSet

  def resetLogs() = RequestLog.reset()

  def logSizes = TwilioLogMetrics(calls=callLog.size, messages=smsLog.size)

  def assertTwilioLogMetricsEventuallyEquals(expectedMetrics: TwilioLogMetrics) {

    val actionsAfter20 = Stream.continually(logSizes).withDelay().take(20).dropWhile {
      !_.equals(expectedMetrics)
    }

    if (actionsAfter20.isEmpty)
      Assert.assertEquals(expectedMetrics, logSizes)
  }

  def assertSmsRecipientsEventuallyEquals(expectedRecipients: Set[String]) {
    val notifiedAfter20Seconds = Stream.continually(smsRecipients).withDelay().take(20).dropWhile(!_.equals(expectedRecipients))

    if (notifiedAfter20Seconds.isEmpty)
      Assert.assertEquals(expectedRecipients, smsRecipients)
  }

}
