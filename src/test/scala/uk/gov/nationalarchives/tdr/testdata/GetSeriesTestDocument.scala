package uk.gov.nationalarchives.tdr.testdata

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import sangria.macros._

object GetSeriesTestDocument {
  object getSeries {
    val getSeriesDocument: sangria.ast.Document = graphql"""query getSeries($$body: String) {
  getSeries(body: $$body) {
    seriesid
    bodyid
    name
    code
    description
  }
}"""
    case class GetSeriesVariables(body: Option[String])
    object GetSeriesVariables { implicit val jsonEncoder: Encoder[GetSeriesVariables] = deriveEncoder[GetSeriesVariables] }
    case class SeriesResponseData(getSeries: List[GetSeries])
    object SeriesResponseData { implicit val jsonDecoder: Decoder[SeriesResponseData] = deriveDecoder[SeriesResponseData] }
    case class GetSeries(seriesid: Long, bodyid: Option[Long], name: Option[String], code: Option[String], description: Option[String])
    object GetSeries {
      implicit val jsonDecoder: Decoder[GetSeries] = deriveDecoder[GetSeries]
      implicit val jsonEncoder: Encoder[GetSeries] = deriveEncoder[GetSeries]
    }
  }
}
