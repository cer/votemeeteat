package net.chrisrichardson.vme.usermanagement.main;
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.cloudfoundry.runtime.env.CloudEnvironment

object UserManagementMainProgram extends App {
  if (new CloudEnvironment().isCloudFoundry()) {
    // activate cloud profile
    System.setProperty("spring.profiles.active", "cloud");
  }
  val ctx = new ClassPathXmlApplicationContext("classpath*:/appctx/*.xml")

}
