package com.stratio.tests.kafka

import io.gatling.core.Predef._
import io.gatling.core.session.{Expression, SessionAttribute}
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

class KafkaSimulationData extends PerformanceTestData {
    feederAssoc.records.foreach(fA => {
    producerScns += scenario(fA.get("TOPIC").get)
      .exec(flattenMapIntoAttributes(fA))
      .exec(Prod.produceData)
    }
    )

  feederAssoc.records.foreach(fA => {
    consumerScns += scenario(fA.get("CONSUMER").get)
      .exec(flattenMapIntoAttributes(fA))
      .exec(Cons.consumeMessage)
  }
  )

  logger.info("Scenarios size: {}",producerScns.size )
  if (producerScns.size < 1) {
    throw new AssertionError("No scenarios")
  }

  setUp(
    producerScns.toList.map(_.inject(rampUsers(users) over injectDuration)) ::: consumerScns.toList.map(_.inject(rampUsers(1) over injectDuration)))
    .maxDuration(runDuration minutes)
    .assertions(
//        global.responseTime.max.lessThan(3000),
        global.successfulRequests.percent.greaterThan(95)
  )
}

trait HeadersData {
  val contentTypeValue: Expression[String] = "application/vnd.kafka.json.v1+json"
  val contentType = "Content-Type"
}

trait PerformanceTestData extends Simulation with HeadersData {

  def logger = LoggerFactory.getLogger(this.getClass)


  object Prod {
    val HTTPproducer = s"""http://${REST_PROXY}/topics/$${TOPIC}"""

    val produceData =
      during(produceDuration minutes) {
        pace(1 seconds, 5 seconds)
          .exec(
          http("POST /data")
            .post(HTTPproducer)
            .body(ElFileBody("src/test/resources/data/producerBody.txt")).asJSON
            .header(contentType, contentTypeValue)
            .check(jsonPath("$.offsets..offset"))
        )
      }
  }

  object Cons {
    val HTTPcreateConsumer = s"""http://${REST_PROXY}/consumers/$${CONSUMER}"""
    val HTTPobtainMsg = s"""http://${REST_PROXY}/consumers/$${CONSUMER}/instances/$${CONSUMER}/topics/$${TOPIC}"""


    val consumeMessage =
        pause(produceDuration minutes)
        .exec(
            http("POST /consumer")
              .post(HTTPcreateConsumer)
              .body(ElFileBody("src/test/resources/data/createConsumer.txt")).asJSON
              .header(contentType, contentTypeValue))
            .pause(5)
        .exec(http("GET /data")
              .get(HTTPobtainMsg)
              .header("Accept", contentTypeValue)
              .check(regex("offset").findAll.exists))
  }


  val feederAssoc = csv("topicList.csv")

  val users = Integer.parseInt(System.getProperty("users", "1"))
  val injectDuration = Integer.parseInt(System.getProperty("injectD", "1"))
  val runDuration = Integer.parseInt(System.getProperty("runD", "2"))
  val produceDuration = (runDuration - 1)
  val REST_PROXY = System.getProperty("REST_PROXY", "127.0.0.1")

  val producerScns = new ListBuffer[ScenarioBuilder]()
  val consumerScns = new ListBuffer[ScenarioBuilder]()
}

