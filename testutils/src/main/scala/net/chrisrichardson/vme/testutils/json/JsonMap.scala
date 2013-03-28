package net.chrisrichardson.vme.testutils.json

import org.codehaus.jackson.map.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import java.io.StringWriter


class JsonMap(map: Map[String, Any]) {

  def toJson = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    val sw = new StringWriter()
    mapper.writeValue(sw, map)
    val json = sw.toString
    json
  }
}

object JsonMap {
  implicit def toJsonMap(map: Map[String, Any]) = new JsonMap(map)

}
