package uk.gov.nationalarchives.tdr

import com.github.tomakehurst.wiremock.WireMockServer
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.concurrent.ScalaFutures.{PatienceConfig, scaled}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.ExecutionContext

class WireMockTest extends AnyFlatSpec with BeforeAndAfterEach with BeforeAndAfterAll {

  // Setting the port to 0 lets Wiremock find an unused port for each test
  val wiremockServer = new WireMockServer(0)

  implicit val ec: ExecutionContext = ExecutionContext.global

  implicit val patienceConfig: PatienceConfig = PatienceConfig(timeout = scaled(Span(5, Seconds)), interval = scaled(Span(100, Millis)))

  val graphQlPath = "/graphql"
  def graphQlUrl: String = wiremockServer.url(graphQlPath)

  override def beforeAll(): Unit = {
    wiremockServer.start()
  }

  override def afterAll(): Unit = {
    wiremockServer.stop()
  }

  override def afterEach(): Unit = {
    wiremockServer.resetAll()
  }
}
