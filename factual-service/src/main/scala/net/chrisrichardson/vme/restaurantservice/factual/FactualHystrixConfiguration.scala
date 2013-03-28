package net.chrisrichardson.vme.restaurantservice.factual

import org.springframework.context.annotation.{Bean, Configuration}
import com.netflix.hystrix.{HystrixCommandProperties, HystrixThreadPoolProperties, HystrixCommandGroupKey, HystrixCommand}
import org.springframework.beans.factory.annotation.Value

@Configuration
class FactualHystrixConfiguration {

  @Value("${factual.execution.isolation.thread.timeoutInMilliseconds}")
  var executionIsolationTimeoutInMilliseconds: Int = _

  @Value("${factual.threadpool.coreSize}")
  var threadPoolSize: Int = _

  @Bean
  def factualHystrixConfig = {
    HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
      .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
      .withCoreSize(threadPoolSize))
      .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
      .withMetricsRollingPercentileEnabled(true).withExecutionIsolationThreadTimeoutInMilliseconds(executionIsolationTimeoutInMilliseconds))
  }

}
