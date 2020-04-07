package uk.gov.nationalarchives.tdr.error

import uk.gov.nationalarchives.tdr.GraphQLClient.{Error, Extensions, Location}

trait GraphQlError {
  def message: String
  def locations: List[Location]
  def path: List[String]
}

object GraphQlError {
  def apply(error: Error): GraphQlError = {
    error.extensions match {
      case Some(Extensions(Some("NOT_AUTHORISED"))) => NotAuthorisedError(error.message, error.locations, error.path)
      case Some(Extensions(Some(unknownCode))) =>
        UnknownGraphQlError(error.message, error.locations, error.path, Some(unknownCode))
      case _ => UnknownGraphQlError(error.message, error.locations, error.path, None)
    }
  }
}

case class NotAuthorisedError(message: String, locations: List[Location], path: List[String])
  extends GraphQlError
case class UnknownGraphQlError(message: String, locations: List[Location], path: List[String], code: Option[String])
  extends GraphQlError
