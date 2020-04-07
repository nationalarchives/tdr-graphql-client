package uk.gov.nationalarchives.tdr.error

import java.io.IOException

import sttp.client.Response
import sttp.model.StatusCode

class HttpException(val response: Response[Either[String, String]])
  extends IOException(s"Unexpected response from GraphQL API: $response") {

  def code: StatusCode = response.code

  def body: String = response.body match {
    // An error should always be in the Left side of the Either, but check both sides so that we don't lose error
    // information if that assumption is wrong
    case Left(body) => body
    case Right(body) => body
  }
}
