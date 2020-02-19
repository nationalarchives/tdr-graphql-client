package uk.gov.nationalarchives.tdr

import java.util.concurrent.TimeUnit

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.{okJson, post, urlEqualTo, unauthorized}
import com.nimbusds.oauth2.sdk.token.BearerAccessToken
import io.circe.Printer
import io.circe.generic.auto._
import io.circe.syntax._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import uk.gov.nationalarchives.tdr.GraphQLTestDocument.getSeries.{Data, GetSeries, Variables, document}
import uk.gov.nationalarchives.tdr.GraphQLClient.GraphqlError

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

class GraphQLClientTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach {
  val wiremockServer = new WireMockServer(9006)

  implicit val ec: ExecutionContext = ExecutionContext.global

  override def beforeEach(): Unit = {
    wiremockServer.start()
  }

  override def afterEach(): Unit = {
    wiremockServer.stop()
  }

  case class GraphqlData(data: Option[Data], errors: List[GraphqlError] = Nil)

  "The getResult method " should "return the correct result" in {
    val data= GraphqlData(Some(Data(List(GetSeries(1L, Option.empty,Option.empty, Some("code"), Option.empty)))))

    val dataString: String = data.asJson.printWith(Printer(dropNullValues = false, ""))

    wiremockServer.stubFor(post(urlEqualTo("/graphql"))
      .willReturn(okJson(dataString)))

    val result = new GraphQLClient[Data, Variables]("http://localhost:9006/graphql").getResult(new BearerAccessToken("token"), document, Option.empty)
    val resultData: GraphQLClient[Data, Variables]#GraphqlData = Await.result(result, Duration(1, TimeUnit.HOURS))
    assert(resultData.data.isDefined)
    assert(resultData.data.get.getSeries.nonEmpty)
    resultData.data.get.getSeries.head.seriesid should equal(1L)
  }

  "The getResult method " should "return an error from the api" in {
    val data= GraphqlData(Option.empty, List(GraphqlError("error", List(), List())))

    val dataString: String = data.asJson.printWith(Printer(dropNullValues = false, ""))

    wiremockServer.stubFor(post(urlEqualTo("/graphql"))
      .willReturn(okJson(dataString)))

    val result = new GraphQLClient[Data, Variables]("http://localhost:9006/graphql").getResult(new BearerAccessToken("token"), document, Option.empty)
    val resultData: GraphQLClient[Data, Variables]#GraphqlData = Await.result(result, Duration(1, TimeUnit.HOURS))
    assert(resultData.data.isEmpty)
    assert(resultData.errors.nonEmpty)
    resultData.errors.head.message should equal("error")
  }

  "The getResult method " should "return an appropriate error if the api returns an error" in {
    wiremockServer.stubFor(post(urlEqualTo("/graphql"))
      .willReturn(unauthorized().withBody("Unauthorised")))
    val result = new GraphQLClient[Data, Variables]("http://localhost:9006/graphql").getResult(new BearerAccessToken("token"), document, Option.empty)
    val resultData: GraphQLClient[Data, Variables]#GraphqlData = Await.result(result, Duration(1, TimeUnit.HOURS))
    assert(resultData.data.isEmpty)
    assert(resultData.errors.nonEmpty)
    resultData.errors.head.message should equal("Unauthorised")
  }

  "The getResult method " should "return no data if the api response is invalid" in {
    wiremockServer.stubFor(post(urlEqualTo("/graphql"))
      .willReturn(okJson("{\"status\": \"ok\"}")))
    val result = new GraphQLClient[Data, Variables]("http://localhost:9006/graphql").getResult(new BearerAccessToken("token"), document, Option.empty)
    val resultData: GraphQLClient[Data, Variables]#GraphqlData = Await.result(result, Duration(1, TimeUnit.HOURS))
    assert(resultData.data.isEmpty)
    assert(resultData.errors.isEmpty)
  }

}
