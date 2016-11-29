package com.stratio.tests.viewer

import java.util.concurrent.atomic.AtomicInteger

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef.http
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

trait Common {

  def logger = LoggerFactory.getLogger(this.getClass)

  val protocol = System.getProperty("PROTOCOL", "http")
  val port = System.getProperty("PORT", "9000")
  val url = System.getProperty("URL", "127.0.0.1")
  val sut = s"${protocol}://${url}:${port}/api"

  val httpConf = http
    .baseURL(sut)
    .warmUp(sut)
    .acceptHeader("text/html,application/xhtml+xml,application/xml;application/zip,q=0.9,*/*;q=0.8")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36")

  object order{
    val dataStart = new AtomicInteger(1)
  }

  val feederAssoc = csv("src/test/resources/data/viewer/associationId.csv")

  val users = Integer.parseInt(System.getProperty("users", "1"))
  val injectDuration = Integer.parseInt(System.getProperty("injectD", "1"))
  val runDuration = Integer.parseInt(System.getProperty("runD", "1"))


  val scns = new ListBuffer[ScenarioBuilder]()
}
