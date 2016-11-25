package com.stratio.tests.template.SimulationWithDriver

/**
 * This is a template simulation. Should be used to iniciate new modules testing
 */

import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.structure.ScenarioContext

import scala.collection.convert.wrapAsJava
import scala.concurrent.duration._

class PerfCustomProtocolSimulation extends Simulation {

  val mine = new ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action = {
      new MyAction(next, ctx)
    }
  }

  val userLog = csv("user_credentials.csv").circular
  val scn = scenario("My custom protocol test")
    .feed(userLog)
    .repeat(2) {
      exec(mine)
    }

  /*setUp(scn.inject(ramp(3 users) over (10 seconds)))
      //Assert the output max time is less than 50ms and 95% of requests were successful
      .assertions(global.responseTime.max.lessThan(50),global.successfulRequests.percent.greaterThan(95))*/

  setUp(
    scn.inject(
      nothingFor(4 seconds), // 1
      atOnceUsers(10), // 2
      rampUsers(3) over (2 seconds) // 3
      /* constantUsersPerSec(5) during (2 seconds), // 4
      constantUsersPerSec(5) during (2 seconds) randomized, // 5
      rampUsersPerSec(10) to 20 during (3 seconds), // 6
      rampUsersPerSec(10) to 20 during (2 seconds) randomized, // 7
      splitUsers(10) into (rampUsers(2) over (5 seconds)) separatedBy (2 seconds), // 8
      splitUsers(10) into (rampUsers(2) over (5 seconds)) separatedBy atOnceUsers(5), // 9
      heavisideUsers(10) over (500 milliseconds) // 10*/
    )
    //Assert the output max time is less than 50ms and 95% of requests were successful
  ).assertions(global.responseTime.max.lessThan(50), global.successfulRequests.percent.greaterThan(95))
}

class MyAction(val next: Action, ctx: ScenarioContext) extends ChainableAction {



  def greet(session: Session) {
    val delegate = new SayHello
    delegate.echo("Hi", wrapAsJava.mapAsJavaMap(session.attributes))
  }

  override def execute(session: Session) {
    var start: Long = 0L
    var end: Long = 0L
    var status: Status = OK
    var errorMessage: Option[String] = None
    try {
      start = System.currentTimeMillis;
      greet(session)
      //println("hola")
      Thread.sleep(100)
      end = System.currentTimeMillis;
    } catch {
      case e: Exception =>
        errorMessage = Some(e.getMessage)
        logger.error("FOO FAILED", e)
        status = KO
    } finally {
      val requestStartDate, requestEndDate = start
      val responseStartDate, responseEndDate = end
      val responseTime = new ResponseTimings(requestStartDate, responseStartDate)
      val requestName = "Test Scenario"
      val message = errorMessage
      val extraInfo = Nil
      val responseCode: Option[String] = None
      ctx.coreComponents.statsEngine.logResponse(session, requestName, responseTime, status, responseCode, message, extraInfo)
      next ! session

    }
  }

  override def name: String = "pepito"
}
