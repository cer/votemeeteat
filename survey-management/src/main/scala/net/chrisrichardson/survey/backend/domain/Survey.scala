package net.chrisrichardson.survey.backend.domain

import org.joda.time.DateTime
import scala.collection.JavaConversions._
import org.apache.commons.logging.LogFactory
import net.chrisrichardson.vme.common.domain.EntityWithIdImpl

class Survey(var prompt : String, 
             var participants : java.util.Set[Participant],  
             var choices : java.util.List[String], 
             var closeTime: DateTime) extends EntityWithIdImpl {


  def this() = this("", new java.util.HashSet[Participant](), List(), new DateTime())
  
  participants.foreach(_.survey = this)
	val OPEN = 1
	val COMPLETED = 2
  val READY_TO_COMPLETE = 3

	var status = OPEN

  def isReadyToComplete = status == READY_TO_COMPLETE

	def recordVote(callerId : String, vote : Int) = {
		val participant = participants.find(_.phoneNumber == callerId)
		participant match {
			case Some(p) =>
        p.recordVote(vote)
        if (participantsWhoResponded.size == participants.size)
          status = READY_TO_COMPLETE
			case None => Survey.logger.info("oops - we don't know about: " + callerId)
		}
		participant
	}
	
	def participantsWhoResponded = participants.filter(_.responded)
	
	def mostPopularChoice =
		participantsWhoResponded.groupBy(_.response).mapValues(_.size).reduceLeftOption( (p, q) =>  if (p._2 > q._2) p else q) map { pair => choices.get(pair._1) }
	
	def noteCompleted = status = COMPLETED
	
}

object Survey {
  val logger = LogFactory.getLog(getClass())

  def apply(prompt : String, participants : Set[Participant], choices : List[String], closeTime : DateTime) = {
		new Survey(prompt, participants, choices, closeTime)
	}
	
	object Participants {
		def apply(participants : Participant*) : Set[Participant] = participants.toSet
	}

	object Choices {
		def apply(choice1 : String, choice2 : String) = List(choice1, choice2)
	}
	
	object DurationInMinutes {
		def apply(minutes : Int) = new DateTime().plusMinutes(minutes)
	}
}