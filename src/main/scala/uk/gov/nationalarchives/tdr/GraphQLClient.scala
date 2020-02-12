package uk.gov.nationalarchives.tdr

import com.nimbusds.oauth2.sdk.token.BearerAccessToken
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Error, Json}
import sangria.ast.Document
import sangria.renderer.QueryRenderer
import sttp.client.asynchttpclient.WebSocketHandler
import sttp.client.asynchttpclient.future.AsyncHttpClientFutureBackend
import sttp.client.circe._
import sttp.client.{Response, ResponseError, basicRequest, _}
import sttp.model.{MediaType, Uri}

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

class GraphQLClient[Data, Variables](url: String)(implicit val ec: ExecutionContext, val dataDecoder: Decoder[Data], val variablesEncoder: Encoder[Variables]) {

  implicit val backend: SttpBackend[Future, Nothing, WebSocketHandler] = AsyncHttpClientFutureBackend()

  implicit val customConfig: Configuration = Configuration.default.withDefaults

  case class GraphqlError(message: String, path: List[String], locations: List[Locations])

  case class Locations(column: Int, line: Int)

  case class GraphqlData(data: Option[Data], errors: List[GraphqlError] = Nil)

  def getResult(token: BearerAccessToken, document: Document, variables: Option[Variables] = Option.empty): Future[GraphqlData] = {
    val queryJson: Json = Json.fromString(QueryRenderer.render(document, QueryRenderer.Compact))
    val variablesJson: Option[Json] = variables.map(_.asJson)
    val fields: immutable.Seq[(String, Json)] = List("query" -> queryJson) ++ variablesJson.map("variables" -> _)

    val body = Json.obj(fields: _*).noSpaces

    val response: Future[Response[Either[ResponseError[Error], GraphqlData]]] =
      basicRequest
        .post(uri"$url")
        .auth.bearer(token.getValue)
        .body(body)
        .contentType(MediaType.ApplicationJson)
        .response(asJson[GraphqlData]).send()

    response.map(r => {
      r.body match {
        case Right(r) => r
        case Left(e) => throw e
      }
    })
  }
}
