package uk.gov.nationalarchives.tdr

import com.github.tomakehurst.wiremock.client.WireMock._
import com.nimbusds.oauth2.sdk.token.BearerAccessToken
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.matchers.should.Matchers
import sttp.model.StatusCode
import uk.gov.nationalarchives.tdr.error.{HttpException, ResponseDecodingException}
import uk.gov.nationalarchives.tdr.testdata.AddFileTestDocument.addFile.addFileDocument
import uk.gov.nationalarchives.tdr.testdata.GetSeriesTestDocument.getSeries.{GetSeriesVariables, SeriesResponseData}

class ErrorHandlingTest extends WireMockTest with Matchers {

  def getSeriesClient = new GraphQLClient[SeriesResponseData, GetSeriesVariables](graphQlUrl)

  "a 401 Unauthorized response" should "cause an HTTP error" in {
    wiremockServer.stubFor(post(urlEqualTo(graphQlPath))
      .willReturn(unauthorized().withBody("some response body")))

    val result = getSeriesClient.getResult(new BearerAccessToken("token"), addFileDocument, None)

    val errorResponse = result.failed.futureValue
    errorResponse shouldBe a[HttpException]

    val responseException = errorResponse.asInstanceOf[HttpException]
    responseException.code should equal(StatusCode.Unauthorized)
    responseException.body should equal("some response body")
  }

  "a 500 server error response" should "cause an HTTP error" in {
    wiremockServer.stubFor(post(urlEqualTo(graphQlPath))
      .willReturn(serverError.withBody("some server error body")))

    val result = getSeriesClient.getResult(new BearerAccessToken("token"), addFileDocument, None)

    val errorResponse = result.failed.futureValue
    errorResponse shouldBe a[HttpException]

    val responseException = errorResponse.asInstanceOf[HttpException]
    responseException.code should equal(StatusCode.InternalServerError)
    responseException.body should equal("some server error body")
  }

  "an arbitrary non-200 response" should "cause an HTTP error" in {
    wiremockServer.stubFor(post(urlEqualTo(graphQlPath))
      .willReturn(status(429).withBody("some arbitrary response body")))

    val result = getSeriesClient.getResult(new BearerAccessToken("token"), addFileDocument, None)

    val errorResponse = result.failed.futureValue
    errorResponse shouldBe a[HttpException]

    val responseException = errorResponse.asInstanceOf[HttpException]
    responseException.code should equal(StatusCode.TooManyRequests)
    responseException.body should equal("some arbitrary response body")
  }

  "a 200 response with invalid JSON" should "cause a deserialisation error" in {
    wiremockServer.stubFor(post(urlEqualTo(graphQlPath))
      .willReturn(ok.withBody("this is not valid JSON")))

    val result = getSeriesClient.getResult(new BearerAccessToken("token"), addFileDocument, None)

    val errorResponse = result.failed.futureValue
    errorResponse shouldBe a[ResponseDecodingException]

    val responseException = errorResponse.asInstanceOf[ResponseDecodingException]
    responseException.getMessage should include("this is not valid JSON")
  }
}
