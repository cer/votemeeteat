package net.chrisrichardson.vme.vmemanagement.services

import org.junit.{Assert, Test}
import org.codehaus.jackson.map.{SerializationConfig, ObjectMapper}
import java.io.{StringWriter, PrintWriter}
import net.chrisrichardson.vme.vmemanagement.messages.CreateVmeRequest

class CreateVmeRequestTest {


  @Test
  def serializationShouldWork {
    val objectMapper = new ObjectMapper();
    val sw = new StringWriter()
    val request = CreateVmeRequestMother.makeCreateVmeRequest()
    objectMapper.writeValue(sw, request)
    Assert.assertEquals("""{"name":"foo","phoneNumber":"+15105551212","longitude":-122.17525949713045,"latitude":37.80070344455335}""", sw.toString())

  }

  @Test
  def deserializationShouldWork {
    val json = """{"name":"foo","phoneNumber":"+15105551212","longitude":-122.17525949713045,"latitude":37.80070344455335}"""
    val objectMapper = new ObjectMapper();
    val cvr = objectMapper.readValue(json, classOf[CreateVmeRequest])
    val expected = CreateVmeRequestMother.makeCreateVmeRequest()

    Assert.assertEquals(expected, cvr);
  }
}
