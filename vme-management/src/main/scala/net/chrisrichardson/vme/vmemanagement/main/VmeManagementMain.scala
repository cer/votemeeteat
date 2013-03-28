package net.chrisrichardson.vme.vmemanagement.backend.main;
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.cloudfoundry.runtime.env.CloudEnvironment

object VmeManagementMain extends App {
  if (new CloudEnvironment().isCloudFoundry()) {
    // activate cloud profile
    System.setProperty("spring.profiles.active", "cloud");
  }
  val ctx = new ClassPathXmlApplicationContext("classpath*:/appctx/*.xml")

}
