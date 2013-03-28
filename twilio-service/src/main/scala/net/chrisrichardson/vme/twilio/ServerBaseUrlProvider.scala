package net.chrisrichardson.vme.twilio

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Value
import org.cloudfoundry.runtime.env.CloudEnvironment
import org.apache.commons.lang.StringUtils
import javax.annotation.PostConstruct
import org.apache.commons.logging.LogFactory

@Component
class ServerBaseUrlProvider {

  val logger = LogFactory.getLog(getClass)

  @Value("${server.base.url}")
  var serverBaseUrl : String = _

  @PostConstruct
  def determineBaseUrl() {
    if (StringUtils.isBlank(serverBaseUrl))
      serverBaseUrl = fetchFromEnvironment
    logger.info("serverBaseUrl=" + serverBaseUrl)
  }

  def baseUrl = serverBaseUrl

  lazy val ce = new CloudEnvironment

  def fetchFromEnvironment = {
    if (ce.isCloudFoundry())
      "http://" + ce.getInstanceInfo().getUris().get(0) + "/"
    else
      null
  }
}