package com.stratio.tests.crossdata

import java.util.concurrent.atomic.AtomicInteger

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef.http
import org.slf4j.LoggerFactory

import scala.collection.convert._
import scala.collection.mutable.ListBuffer

trait Common {

  val fedderCrossdata = csv("crossdatafeeder.csv").circular

  val crossdataHost = System.getProperty("CROSSDATA_HOST", "127.0.0.1")
  val crossdataPort = Integer.parseInt(System.getProperty("CROSSDATA_PORT", "13422"))

  val users = Integer.parseInt(System.getProperty("users", "1"))
  val injectDuration = Integer.parseInt(System.getProperty("injectD", "1"))
  val runDuration = Integer.parseInt(System.getProperty("runD", "1"))

}
