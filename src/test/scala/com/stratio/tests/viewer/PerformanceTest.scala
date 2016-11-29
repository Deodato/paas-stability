package com.stratio.tests.viewer

import io.gatling.core.Predef._
import io.gatling.http.Predef.{http, responseTimeInMillis, jsonPath}
import scala.concurrent.duration._

trait PerformanceTest extends Simulation with Common {

  object Auth {

    val auth = http("POST /login/authenticate/userpass")
      .post("/login/authenticate/userpass")
      .body(ElFileBody("src/test/resources/data/viewer/AUTH.txt")).asJSON
  }

  object Data {

    val getData =
      forever(
        pace(5 seconds, 10 seconds).exec(
          http("POST /data")
          .post("/data")
          .body(StringBody(
          """{"pageWidgetId": ${PWID}
            ,"filters":[],"aggregations":[]
            |,"parameters": []
            |,"metadata":true
            |}""".stripMargin)).asJSON
          .check(jsonPath("$")
          .saveAs("response"))
          .check(responseTimeInMillis.lessThanOrEqual(10000))
        )
  )}
}

