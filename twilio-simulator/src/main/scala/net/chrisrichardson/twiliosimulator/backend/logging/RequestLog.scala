package net.chrisrichardson.twiliosimulator.backend.logging

import collection.mutable
import junit.framework.Assert

object RequestLog {


  case class SmsMessage(sid: String, from: String, to: String, body: String)
  case class Call(sid: String, from: String, to: String, url: String)

  val smsMessages = mutable.Map[String, SmsMessage]()
  val calls = mutable.Map[String, Call]()

  def logSms(sid: String, from: String, to: String, body: String) {
    smsMessages(sid) = SmsMessage(sid= sid, from= from, to= to, body= body)
  }
  def logCall(sid: String, from: String, to: String, url: String) {
    calls(sid) = Call(sid= sid, from= from, to= to, url= url)
  }

  def assertSmsExists(message: SmsMessage) {
    val m = smsMessages(message.sid)
    Assert.assertEquals(message, m)
  }
  def assertCallExists(message: Call) {
    val m = calls(message.sid)
    Assert.assertEquals(message, m)
  }

  def smsLog = smsMessages.values.toList
  def callLog = calls.values.toList

  def reset() {
    smsMessages.clear()
    calls.clear()
  }

}

