package net.chrisrichardson.survey.backend.domain.jpa
import org.springframework.stereotype.Repository
import net.chrisrichardson.survey.backend.domain.SurveyRepository
import org.joda.time.DateTime
import net.chrisrichardson.survey.backend.domain.Survey
import net.chrisrichardson.survey.backend.domain.Participant
import javax.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.PersistenceUnit
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceContext
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.dao.support.DataAccessUtils
import scala.collection.JavaConversions._

@Repository
class SurveyRepositoryJpaImpl extends SurveyRepository {

  @PersistenceContext
  var em : EntityManager = _
  
  def save(survey : Survey) {
    em.persist(survey)
  }
  
  def findOne(id : Long) = em.find(classOf[Survey], id)
  
  def findSurveyByCallerId(callerId : String) = 
      em.createNamedQuery(classOf[Survey].getName + ".findSurveyByCallerId", classOf[Survey]).
        setParameter(1, callerId).
        setMaxResults(1).
        getResultList().headOption
      
  def findExpiredSurveys(cutoffTime : DateTime) = 
    em.createNamedQuery(classOf[Survey].getName + ".findExpiredSurveys", classOf[Survey]).setParameter(1, cutoffTime).getResultList()

  def findParticipantsToNotify = em.createNamedQuery(classOf[Survey].getName + ".findParticipantsToNotify", classOf[Participant]).getResultList().toList
  
  def merge(participant : Participant) = em.merge(participant)
    
}