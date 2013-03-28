package net.chrisrichardson.survey.backend.twilio
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.beans.factory.annotation.Autowired
import org.junit.Test

@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations = Array("classpath*:/appctx/*.xml"))
class FlushMessagesTest {

  @Autowired
  var rabbitTemplate: RabbitTemplate = _

  @Test
  def drainMessages {
    for (m <- Iterator.continually(rabbitTemplate.receiveAndConvert("createSurveyRequestQueue")).takeWhile(_ != null)) {
      println("Draining=" + m)
    }
  }
}