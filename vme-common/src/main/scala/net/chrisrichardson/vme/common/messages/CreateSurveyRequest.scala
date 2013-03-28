package net.chrisrichardson.vme.common.messages

import scala.reflect.BeanInfo
import scala.reflect.BeanProperty

@BeanInfo
case class CreateSurveyRequest(
             @BeanProperty var correlationId : String, 
             @BeanProperty var prompt : String, 
             @BeanProperty var participants : java.util.Set[ParticipantInfo],  
             @BeanProperty var choices : java.util.List[String], 
             @BeanProperty var responsesNeeded : Int,
             @BeanProperty var durationInSeconds : Int) {
  def this() = this(null, null, null, null, 0, 0)

}

@BeanInfo
case class ParticipantInfo(@BeanProperty var name : String, @BeanProperty var phoneNumber : String) {
  def this() = this(null, null)
}