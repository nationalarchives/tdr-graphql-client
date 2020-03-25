package uk.gov.nationalarchives.tdr

import com.nimbusds.oauth2.sdk.token.BearerAccessToken
import io.circe._
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.auto._
import io.circe.parser._
import io.circe.syntax._
import sangria.ast.Document
import sangria.renderer.QueryRenderer
import sttp.client.asynchttpclient.WebSocketHandler
import sttp.client.asynchttpclient.future.AsyncHttpClientFutureBackend
import sttp.client.{Response, basicRequest, _}
import sttp.model.MediaType
import uk.gov.nationalarchives.tdr.GraphQLClient.Error
import uk.gov.nationalarchives.tdr.error.{HttpException, ResponseDecodingException, _}

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

class GraphQLClient[Data, Variables](url: String)(implicit val ec: ExecutionContext, val dataDecoder: Decoder[Data], val variablesEncoder: Encoder[Variables]) {

  implicit val backend: SttpBackend[Future, Nothing, WebSocketHandler] = AsyncHttpClientFutureBackend()

  implicit val customConfig: Configuration = Configuration.default.withDefaults

  case class GraphqlData(data: Option[Data], errors: List[Error] = Nil)

  def getResult(token: BearerAccessToken, document: Document, variables: Option[Variables] = Option.empty): Future[GraphQlResponse[Data]] = {
    val queryJson: Json = Json.fromString(QueryRenderer.render(document, QueryRenderer.Compact))
    val variablesJson: Option[Json] = variables.map(_.asJson)
    val fields: immutable.Seq[(String, Json)] = List("query" -> queryJson) ++ variablesJson.map("variables" -> _)

    val body = Json.obj(fields: _*).printWith(GraphQLClient.jsonPrinter)

    val response: Future[Response[Either[String, String]]] =
      basicRequest
        .post(uri"$url")
        .auth.bearer(token.getValue)
        .body(body)
        .contentType(MediaType.ApplicationJson)
        .response(asString)
        .send()

    response.flatMap(r => {
      r.body match {
        case Right(body) => parseBody(body)
        case Left(_) => Future.failed(new HttpException(r))
      }
    })
  }

  private def parseBody(body: String): Future[GraphQlResponse[Data]] = {
    val parsedBody = parse(body).flatMap(json => json.as[GraphqlData])
    parsedBody match {
      case Left(decodingFailure) => Future.failed(new ResponseDecodingException(body, decodingFailure))
      case Right(graphQlResponseBody) => {
        val response = GraphQlResponse(
          graphQlResponseBody.data,
          graphQlResponseBody.errors.map(e => GraphQlError(e))
        )
        Future.successful(response)
      }
    }
  }
}

object GraphQLClient {

  case class Error(message: String, path: List[String], locations: List[Location], extensions: Option[Extensions])

  case class Location(column: Int, line: Int)

  case class Extensions(code: Option[String])

  val jsonPrinter: Printer = Printer
    .noSpaces
    // If any optional values are set to None, ignore them rather than setting them to null. This makes it possible to
    // remove deprecated optional parameters from the API without breaking the clients.
    .copy(dropNullValues = true)
}

case class GraphQlResponse[Data](data: Option[Data], errors: List[GraphQlError])