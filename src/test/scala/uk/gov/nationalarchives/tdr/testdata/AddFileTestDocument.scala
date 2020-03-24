package uk.gov.nationalarchives.tdr.testdata

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import sangria.macros._

object AddFileTestDocument {
  object addFile {
    val addFileDocument: sangria.ast.Document = graphql"""mutation addfile($$addFileInput: AddFileInput!) {
  addFile(addFileInput: $$addFileInput) {
    fileId
  }
}"""
    case class AddFileVariables(addFileInput: AddFileInput)
    object AddFileVariables { implicit val jsonEncoder: Encoder[AddFileVariables] = deriveEncoder[AddFileVariables] }
    case class AddFileInput(consignmentId: Long, name: Option[String])
    case object AddFileInput {
      implicit val jsonDecoder: Decoder[AddFileInput] = deriveDecoder[AddFileInput]
      implicit val jsonEncoder: Encoder[AddFileInput] = deriveEncoder[AddFileInput]
    }
    case class AddFile(fileId: String)
    object AddFile {
      implicit val jsonDecoder: Decoder[AddFile] = deriveDecoder[AddFile]
      implicit val jsonEncoder: Encoder[AddFile] = deriveEncoder[AddFile]
    }
    case class FileResponseData(addFile: AddFile)
    object FileResponseData { implicit val jsonDecoder: Decoder[FileResponseData] = deriveDecoder[FileResponseData] }
  }
}
