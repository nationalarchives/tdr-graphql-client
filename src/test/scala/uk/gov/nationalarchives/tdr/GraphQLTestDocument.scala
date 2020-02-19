package uk.gov.nationalarchives.tdr

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import sangria.macros._

object GraphQLTestDocument {
  object getSeries {
    val document: sangria.ast.Document = graphql"""query getSeries($$body: String) {
  getSeries(body: $$body) {
    seriesid
    bodyid
    name
    code
    description
  }
}"""
    case class Variables(body: Option[String])
    object Variables { implicit val jsonEncoder: Encoder[Variables] = deriveEncoder[Variables] }
    case class Data(getSeries: List[GetSeries])
    object Data { implicit val jsonDecoder: Decoder[Data] = deriveDecoder[Data] }
    case class GetSeries(seriesid: Long, bodyid: Option[Long], name: Option[String], code: Option[String], description: Option[String])
    object GetSeries {
      implicit val jsonDecoder: Decoder[GetSeries] = deriveDecoder[GetSeries]
      implicit val jsonEncoder: Encoder[GetSeries] = deriveEncoder[GetSeries]
    }
  }
}
