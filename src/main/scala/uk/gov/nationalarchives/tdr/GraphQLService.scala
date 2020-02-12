package uk.gov.nationalarchives.tdr

import io.circe.{Decoder, Encoder}

import scala.concurrent.ExecutionContext

abstract class GraphQLService(url: String) {
  def getClient[Data, Variables]()(implicit ec: ExecutionContext, dataDecoder: Decoder[Data], variablesEncoder: Encoder[Variables]) = {
    new GraphQLClient[Data, Variables](url)
  }
}
