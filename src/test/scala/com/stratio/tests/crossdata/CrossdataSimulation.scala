package com.stratio.tests.crossdata

import com.stratio.crossdata.driver.config.DriverConf
import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.structure.ScenarioContext
import com.stratio.crossdata.driver.Driver
import scala.concurrent.duration._
import scala.collection.convert.wrapAsJava

class CrossdataSimulation extends Simulation with Common {

  val mine = new ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action = {
      new CrossdataAction(next, ctx)
    }
  }

  val scn = scenario("Crossdata perfomance protocol test")
    .feed(fedderCrossdata)
    .exec(mine)

  setUp(
    scn.inject(rampUsers(users) over injectDuration))
    .maxDuration(runDuration minutes)
    .assertions(global.successfulRequests.percent.greaterThan(98))
}

class CrossdataAction(val next: Action, ctx: ScenarioContext) extends ChainableAction with Common {


  override def execute(session: Session) {
    val feeder = wrapAsJava.mapAsJavaMap(session.attributes)
    val driverConf = new DriverConf()
    driverConf.setHttpHostAndPort(crossdataHost, crossdataPort)
    val driver = Driver.http.newSession(feeder.get("user").toString,feeder.get("password").toString, driverConf)
    var start: Long = 0L
    var end: Long = 0L
    var status: Status = OK
    var errorMessage: Option[String] = None
    try {
      start = System.currentTimeMillis
      val resp = driver.sql(feeder.get("query").toString)
      val result = resp.waitForResult()
      if(result.hasError){
        logger.error("CROSSDATA QUERY EXCEPTION", result.hasError)
        status = KO
      }
      end = System.currentTimeMillis
    } catch {
      case e: Exception =>
        errorMessage = Some(e.getMessage)
        logger.error("CROSSDATA EXCEPTION", e)
        status = KO
    } finally {
      val requestStartDate, requestEndDate = start
      val responseStartDate, responseEndDate = end
      val responseTime = new ResponseTimings(requestStartDate, responseStartDate)
      val requestName = "Crossdata Scenario"
      val message = errorMessage
      val extraInfo = Nil
      val responseCode: Option[String] = None
      ctx.coreComponents.statsEngine.logResponse(session, requestName, responseTime, status, responseCode, message, extraInfo)
      next ! session

    }
  }

  override def name: String = "Crossdata Simulation"
}
