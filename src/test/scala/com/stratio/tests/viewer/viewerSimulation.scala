package com.stratio.tests.viewer

import io.gatling.core.Predef._
import scala.concurrent.duration.DurationInt

class viewerSimulation extends PerformanceTest {

  feederAssoc.records.foreach(fA => {
    scns += scenario(fA.get("DS").get)
      .exec(flattenMapIntoAttributes(fA))
      .exec(Auth.auth)
      .exec(Data.getData)
  })

  logger.error("Scenarios size: {}",scns.size )
  if (scns.size < 1) {
      throw new AssertionError("No scenarios")
    }

  setUp(
    scns.toList.map(_.inject(rampUsers(users) over injectDuration)))
    .maxDuration(runDuration minutes)
    .protocols(httpConf)
    .assertions(
        global.responseTime.max.lessThan(50),
        global.successfulRequests.percent.greaterThan(90)
        )
}