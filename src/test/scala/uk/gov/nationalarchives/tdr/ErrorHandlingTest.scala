package uk.gov.nationalarchives.tdr

import com.github.tomakehurst.wiremock.client.WireMock._
import com.nimbusds.oauth2.sdk.token.BearerAccessToken
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.matchers.should.Matchers
import sttp.client.SttpBackend
import sttp.client.asynchttpclient.WebSocketHandler
import sttp.client.asynchttpclient.future.AsyncHttpClientFutureBackend
import sttp.model.StatusCode
import uk.gov.nationalarchives.tdr.GraphQLClient.Location
import uk.gov.nationalarchives.tdr.error.{HttpException, NotAuthorisedError, ResponseDecodingException, UnknownGraphQlError}
import uk.gov.nationalarchives.tdr.testdata.AddFileTestDocument.addFile.addFileDocument
import uk.gov.nationalarchives.tdr.testdata.GetSeriesTestDocument.getSeries.{GetSeriesVariables, SeriesResponseData}

import scala.concurrent.Future
import scala.io.Source.fromResource

class ErrorHandlingTest extends WireMockTest with Matchers {

  implicit val backend: SttpBackend[Future, Nothing, WebSocketHandler] = AsyncHttpClientFutureBackend()

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

  "an arbitrary GraphQL error" should "appear in the error response" in {
    val responseJson = fromResource("testdata/responses/unknown-error.json").mkString
    wiremockServer.stubFor(post(urlEqualTo(graphQlPath))
      .willReturn(ok.withBody(responseJson)))

    val result = getSeriesClient.getResult(new BearerAccessToken("token"), addFileDocument, None)

    val response = result.futureValue
    response.errors.head shouldBe a[UnknownGraphQlError]

    val error = response.errors.head.asInstanceOf[UnknownGraphQlError]

    error.message should equal("Some unknown error")
    error.code shouldBe None
  }

  "an arbitrary GraphQL error with a code" should "include the code in the error response" in {
    val responseJson = fromResource("testdata/responses/unknown-error-code.json").mkString
    wiremockServer.stubFor(post(urlEqualTo(graphQlPath))
      .willReturn(ok.withBody(responseJson)))

    val result = getSeriesClient.getResult(new BearerAccessToken("token"), addFileDocument, None)

    val response = result.futureValue
    response.errors.head shouldBe a[UnknownGraphQlError]

    val error = response.errors.head.asInstanceOf[UnknownGraphQlError]

    error.message should equal("Some unknown error")
    error.code shouldBe Some("OTHER_CODE")
  }

  "an authorisation error" should "appear in the error response as a NotAuthorised error" in {
    val responseJson = fromResource("testdata/responses/not-authorised-error.json").mkString
    wiremockServer.stubFor(post(urlEqualTo(graphQlPath))
      .willReturn(ok.withBody(responseJson)))

    val result = getSeriesClient.getResult(new BearerAccessToken("token"), addFileDocument, None)

    val response = result.futureValue
    response.errors.head shouldBe a[NotAuthorisedError]

    val error = response.errors.head.asInstanceOf[NotAuthorisedError]

    error.message should equal("Some description of the authorisation error")
  }

  "a GraphQL error" should "include the built-in error details" in {
    val responseJson = fromResource("testdata/responses/unknown-error.json").mkString
    wiremockServer.stubFor(post(urlEqualTo(graphQlPath))
      .willReturn(ok.withBody(responseJson)))

    val result = getSeriesClient.getResult(new BearerAccessToken("token"), addFileDocument, None)

    val response = result.futureValue
    response.errors.head shouldBe a[UnknownGraphQlError]

    val error = response.errors.head.asInstanceOf[UnknownGraphQlError]

    error.locations should equal(List(Location(5, 2)))
    error.path should equal(List("somePath"))
  }
}
