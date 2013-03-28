package net.chrisrichardson.survey.backend.domain

import net.chrisrichardson.vme.common.domain.EntityWithIdImpl

class Participant(var name : String, var phoneNumber : String) extends EntityWithIdImpl {
  
  // FIXME - verify that phone number is +1...
  
  def this() = this(null, null)
  
	val WAITING = 0
	val NOTIFYING = 1
	val NOTIFIED = 2
	val RESPONDED = 3
	val FAILED = 4
	var survey : Survey = _
	
	var state : Int = WAITING
	var response : Int = _

	def recordVote(vote : Int) { 
		response = vote
		state = RESPONDED
	}
	
	def responded = state == RESPONDED
	
	def noteNotifying() {
	  // FIXME we should track when so we can retry later.
	  state = NOTIFYING
	}
	
	def noteNotified() {
	  state = NOTIFIED
	}
}

object Participant {
	def apply(name : String, phoneNumber : String) = new Participant(name, phoneNumber)
	
}