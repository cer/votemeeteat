package net.chrisrichardson.survey.backend.domain

import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.{TransactionCallback, TransactionTemplate}
import org.springframework.transaction.TransactionStatus
import javax.persistence.{EntityManager, PersistenceContext}
import scala.collection.JavaConversions._
import org.apache.commons.logging.LogFactory

@Component
class SurveyTransactionHelper {
  val logger = LogFactory.getLog(getClass())

  @Autowired
  var transactionTemplate : TransactionTemplate = _

  @PersistenceContext
  var em : EntityManager = _

  def withTransaction(fn : => Unit) {
    class MyTxnCallback extends TransactionCallback[Unit] {
      def doInTransaction(status : TransactionStatus) {
        fn
      }
    }
    transactionTemplate.execute(new MyTxnCallback)
  }

  def deleteAllSurveys() {
    transactionTemplate.execute(new TransactionCallback[Unit] {
      def doInTransaction(status: TransactionStatus) = {
        val surveysToDelete = em.createQuery("select s from Survey s", classOf[Survey]).getResultList()
        logger.info("Deleting this many surveys=" + surveysToDelete)
        surveysToDelete.foreach(em.remove(_))

      }
    })
  }

}
