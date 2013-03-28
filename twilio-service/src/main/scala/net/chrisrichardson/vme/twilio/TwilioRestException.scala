package net.chrisrichardson.vme.twilio

case class TwilioRestException(message : String, code : Int) extends RuntimeException(message)