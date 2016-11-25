package com.stratio.tests.template.SimulationAPI

/**
 * This is a template simulation. Should be used to iniciate new modules testing
 */

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

class TemplateSimulationAPI extends PerformanceTestAPI with CustomProperties {

  feederAssoc.records.foreach(fA => {
    scns += scenario(fA.get("TOPIC").get)
      .exec(flattenMapIntoAttributes(fA))
      .exec(Prod.produceData)
  }
  )

  logger.info("Scenarios size: {}",scns.size )
  if (scns.size < 1) {
    throw new AssertionError("No scenarios")
  }

  setUp(
    scns.toList.map(_.inject(rampUsers(users) over injectDuration)))
    .maxDuration(runDuration minutes)
    .assertions(
      /* constantUsersPerSec(5) during (2 seconds), // 4
        constantUsersPerSec(5) during (2 seconds) randomized, // 5
        rampUsersPerSec(10) to 20 during (3 seconds), // 6
        rampUsersPerSec(10) to 20 during (2 seconds) randomized, // 7
        splitUsers(10) into (rampUsers(2) over (5 seconds)) separatedBy (2 seconds), // 8
        splitUsers(10) into (rampUsers(2) over (5 seconds)) separatedBy atOnceUsers(5), // 9
        global.responseTime.max.lessThan(3000),
        heavisideUsers(10) over (500 milliseconds) // 10*/
      global.successfulRequests.percent.greaterThan(95)
    )
}

trait PerformanceTestAPI extends Simulation with Headers {

  def logger = LoggerFactory.getLogger(this.getClass)


  object Prod extends Urls{
    val produceData =
      forever {
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

  val scns = new ListBuffer[ScenarioBuilder]()
}