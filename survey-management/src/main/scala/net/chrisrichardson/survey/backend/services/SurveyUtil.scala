package net.chrisrichardson.survey.backend.services

import org.apache.commons.logging.LogFactory

object SurveyUtil {

  val logger = LogFactory.getLog(getClass())

  def ignoringErrors(fn: => Unit) = try {
    fn
  } catch {
    case e: Exception => {
      logger.error("ignoring exception", e)
    }
  }

}