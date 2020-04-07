package uk.gov.nationalarchives.tdr.error

import java.io.IOException

class ResponseDecodingException(responseBody: String, cause: Throwable)
  extends IOException(s"Failed to decode API response body '$responseBody' as graphQL data", cause)
