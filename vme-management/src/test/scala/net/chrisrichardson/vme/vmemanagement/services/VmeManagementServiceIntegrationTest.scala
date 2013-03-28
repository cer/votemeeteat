package net.chrisrichardson.vme.vmemanagement.services

import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.ContextConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.junit.{Assert, Test}
import net.chrisrichardson.vme.vmemanagement.stubs.{SurveyServiceStub, FriendServiceStub}
import net.chrisrichardson.testutils.streams.DelayedStream._


@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(locations = Array("classpath*:/appctx/**/*.xml", "/appctx.test/**/*.xml"))
class VmeManagementServiceIntegrationTest {

  @Autowired
  var vmeManagementService: VmeManagementService = _

  @Autowired
  var friendService: FriendServiceStub = _

  @Autowired
  var surveyService: SurveyServiceStub = _

  @Test
  def vmeShouldBeCreated {
    friendService.clear()
    surveyService.clear()

    val request = CreateVmeRequestMother.makeCreateVmeRequest()
    vmeManagementService.createVme(request)

    assertEventuallyEquals(1, friendService.requests.size)
    assertEventuallyEquals(1, surveyService.requests.size)
  }

  def assertEventuallyEquals[T](expectedValue: T, fn: => T) {

    val s = Stream.continually(expectedValue.equals(fn)).withDelay().take(5).dropWhile(!_)
    if (s.isEmpty)
      Assert.assertEquals(expectedValue, fn)

  }

}
