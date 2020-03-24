package uk.gov.nationalarchives.tdr

import java.util.concurrent.TimeUnit

import com.github.tomakehurst.wiremock.client.WireMock._
import com.nimbusds.oauth2.sdk.token.BearerAccessToken
import io.circe.Printer
import io.circe.generic.auto._
import io.circe.syntax._
import org.scalatest.matchers.should.Matchers
import uk.gov.nationalarchives.tdr.GraphQLClient.GraphqlError
import uk.gov.nationalarchives.tdr.testdata.AddFileTestDocument.addFile.{AddFileInput, AddFileVariables, FileResponseData, addFileDocument}
import uk.gov.nationalarchives.tdr.testdata.GetSeriesTestDocument.getSeries.{GetSeries, GetSeriesVariables, SeriesResponseData, getSeriesDocument}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Awaitable}

class GraphQLClientTest extends WireMockTest with Matchers {
  def getSeriesClient = new GraphQLClient[SeriesResponseData, GetSeriesVariables](graphQlUrl)
  def addFileClient = new GraphQLClient[FileResponseData, AddFileVariables](graphQlUrl)

  def await[T](result: Awaitable[T]): T = Await.result(result, Duration(5, TimeUnit.SECONDS))

  case class GraphqlData(data: Option[SeriesResponseData], errors: List[GraphqlError] = Nil)

  "The getResult method " should "return the correct result" in {
    val data= GraphqlData(Some(SeriesResponseData(List(GetSeries(1L, Some(2L),Some("foo"), Some("code"), Some("bar"))))))

    val dataString: String = data.asJson.printWith(Printer(dropNullValues = false, ""))

    wiremockServer.stubFor(post(urlEqualTo("/graphql"))
      .willReturn(okJson(dataString)))

    val result = getSeriesClient.getResult(new BearerAccessToken("token"), getSeriesDocument, Option.empty)
    val resultData: GraphQLClient[SeriesResponseData, GetSeriesVariables]#GraphqlData = await(result)
    assert(resultData.data.isDefined)
    assert(resultData.data.get.getSeries.nonEmpty)
    resultData.data.get.getSeries.head.seriesid should equal(1L)
  }

  "The getResult method " should "return an error from the api" in {
    val data= GraphqlData(Option.empty, List(GraphqlError("error", List(), List())))

    val dataString: String = data.asJson.printWith(Printer(dropNullValues = false, ""))

    wiremockServer.stubFor(post(urlEqualTo("/graphql"))
      .willReturn(okJson(dataString)))

    val result = getSeriesClient.getResult(new BearerAccessToken("token"), getSeriesDocument, Option.empty)
    val resultData: GraphQLClient[SeriesResponseData, GetSeriesVariables]#GraphqlData = await(result)
    assert(resultData.data.isEmpty)
    assert(resultData.errors.nonEmpty)
    resultData.errors.head.message should equal("error")
  }

  "The getResult method " should "return no data if the api response is invalid" in {
    wiremockServer.stubFor(post(urlEqualTo("/graphql"))
      .willReturn(okJson("{\"status\": \"ok\"}")))
    val result = getSeriesClient.getResult(new BearerAccessToken("token"), getSeriesDocument, Option.empty)
    val resultData: GraphQLClient[SeriesResponseData, GetSeriesVariables]#GraphqlData = await(result)
    assert(resultData.data.isEmpty)
    assert(resultData.errors.isEmpty)
  }

  "The client" should "include optional values which are provided in the variables" in {
    wiremockServer.stubFor(post(urlEqualTo("/graphql"))
      .willReturn(okJson("{}")))

    val variables = AddFileVariables(AddFileInput(123, Some("some file name")))

    val result = addFileClient
      .getResult(new BearerAccessToken("token"), addFileDocument, Some(variables))
    await(result)

    wiremockServer.verify(postRequestedFor(urlMatching("/graphql"))
      .withRequestBody(matchingJsonPath("$.variables.addFileInput.name", matching("some file name"))))
  }

  "The client" should "skip optional values which are set to None" in {
    wiremockServer.stubFor(post(urlEqualTo("/graphql"))
      .willReturn(okJson("{}")))

    val variables = AddFileVariables(AddFileInput(123, None))

    val result = addFileClient
      .getResult(new BearerAccessToken("token"), addFileDocument, Some(variables))
    await(result)

    wiremockServer.verify(postRequestedFor(urlMatching("/graphql"))
      .withRequestBody(notMatching(".*\"name\":null.*")))
  }
}
