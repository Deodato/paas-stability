package com.stratio.tests.template.SimulationAPI

import java.util.concurrent.atomic.AtomicInteger

import io.gatling.core.Predef._
import io.gatling.core.session._
import io.gatling.http.Predef.http
import org.slf4j.LoggerFactory



trait Headers {
  val contentTypeValue: Expression[String] = "application/vnd.kafka.json.v1+json"
  val contentType = "Content-Type"
}


trait Urls {
  val REST_PROXY = System.getProperty("CUSTOM_URL", "127.0.0.1")


  val HTTPproducer = s"""http://${REST_PROXY}/topics/$${TOPIC}"""
  val HTTPcreateConsumer = s"""http://${REST_PROXY}/consumers/$${CONSUMER}"""
  val HTTPobtainMsg = s"""http://${REST_PROXY}/consumers/$${CONSUMER}/instances/$${CONSUMER}/topics/$${TOPIC}"""
}


/*
* Any other system property needed for running simulation
*/
trait CustomProperties {
  val users = Integer.parseInt(System.getProperty("users", "1"))
  val injectDuration = Integer.parseInt(System.getProperty("injectD", "1"))
  val runDuration = Integer.parseInt(System.getProperty("runD", "1"))


  /*
  * File located in the root path of the project that will feed the scenarios.
  */
  val feederAssoc = csv("topicList.csv")
}