package net.chrisrichardson.vme.common.messages

import org.junit.Test
import org.junit.Assert
import scala.collection.JavaConversions._
import org.codehaus.jackson.map.ObjectMapper
import org.apache.commons.logging.LogFactory

class JsonSerializationTest {

  val logger = LogFactory.getLog(getClass())

  @Test
  def serializeCreateSurveyRequest {
    val mapper = new ObjectMapper();
    val request = CreateSurveyRequest("123", "foo bar", Set(ParticipantInfo("x", "y")), List("a"), 1, 2 )
    logger.info(mapper.writeValueAsString(request))
  }
  
   @Test
   def deserializeCreateSurveyRequest {
     val mapper = new ObjectMapper();

     val json = """
     { 
       "prompt": "Foo",
       "participants" : [ 
                           { "name" : "Foo", "phoneNumber" : "5105551212"}, 
                           {"name" : "Bar", "phoneNumber" : "4155551212"} ],
       "choices" : [ "a", "b", "c"],
       "responsesNeeded" : "1",
       "durationInSeconds" : "123"
     }
       """
     val x = mapper.readValue(json, classOf[CreateSurveyRequest])
     
     Assert.assertEquals("Foo", x.prompt)
     Assert.assertEquals(List("a", "b", "c"), x.choices.toList)
     Assert.assertEquals(Set(ParticipantInfo("Foo", "5105551212"), ParticipantInfo("Bar", "4155551212") ), x.participants.toSet)
     Assert.assertEquals(1, x.responsesNeeded)
     Assert.assertEquals(123, x.durationInSeconds)
   }
}