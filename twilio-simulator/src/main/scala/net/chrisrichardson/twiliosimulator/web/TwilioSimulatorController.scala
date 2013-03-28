package net.chrisrichardson.twiliosimulator.web

import org.springframework.web.bind.annotation._
import org.springframework.stereotype.Controller
import net.chrisrichardson.twiliosimulator.backend.logging.{SidGenerator, RequestLog}
import org.springframework.http.{HttpStatus, ResponseEntity}

@Controller
class TwilioSimulatorController {


  @RequestMapping(value = Array("/Accounts/{accountSid}/SMS/Messages"), method = Array(RequestMethod.POST))
  @ResponseBody
  def sms(@PathVariable accountSid: String,  @RequestParam("From") from: String, @RequestParam("To") to: String, @RequestParam("Body") body: String) = {
    val sid = SidGenerator.nextSid()
    RequestLog.logSms(sid = sid, from = from, to = to, body = body)
    <Something>
      <SMSMessage>
        <Sid>
          {sid}
        </Sid>
      </SMSMessage>
    </Something>
  }

  @RequestMapping(value = Array("/Accounts/{accountSid}/SMS/Messages/{sid}"), method = Array(RequestMethod.GET))
  @ResponseBody
  def sms(@PathVariable accountSid: String, @PathVariable sid: String) = {
    <Something>
      <SMSMessage>
        <Sid>
          {sid}
        </Sid>
        <Status>JustPeachy</Status>
      </SMSMessage>
    </Something>
  }

  @RequestMapping(value = Array("/Accounts/{accountSid}/Calls"), method = Array(RequestMethod.POST))
  def call(@PathVariable accountSid: String, @RequestParam("From") from: String, @RequestParam("To") to: String, @RequestParam("Url") url: String) = {
    if (to.equals("+1510555")) {
      val body =
      <TwilioResponse>
        <RestException>
          <Status>400</Status>
          <Message>bad number</Message>
          <Code>21211</Code>
        </RestException>
      </TwilioResponse>
      new ResponseEntity(body, HttpStatus.BAD_REQUEST)
    } else {
      val sid = SidGenerator.nextSid()
      RequestLog.logCall(sid = sid, from = from, to = to, url = url)
      val body = <Something>
        <Call>
          <Sid>{sid}          </Sid>
        </Call>
      </Something>
      new ResponseEntity(body, HttpStatus.OK)
    }
  }

}