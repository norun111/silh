package models

import java.util.UUID

import play.api.libs.json._
import org.joda.time.DateTime
import reactivemongo.bson.{ BSONDateTime, BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID }
/**
 * A token to authenticate a user against an endpoint for a short time period.
 *
 * @param id The unique token ID.
 * @param userID The unique ID of the user the token is associated with.
 * @param expiry The date-time the token expires.
 */
case class AuthToken(
  id: String = UUID.randomUUID.toString,
  userID: String,
  expiry: Option[DateTime]
)

object AuthToken {
  implicit val jsonFormat: OFormat[AuthToken] = Json.format[AuthToken]

  implicit object AuthTokenBSONReader extends BSONDocumentReader[AuthToken] {
    def read(doc: BSONDocument): AuthToken =
      AuthToken(
        doc.getAs[String]("id").get,
        doc.getAs[String]("userID").get,
        doc.getAs[BSONDateTime]("expiry").map(dt => new DateTime(dt.value))
      )
  }

  implicit object AuthTokenBSONWriter extends BSONDocumentWriter[AuthToken] {
    def write(authToken: AuthToken): BSONDocument =
      BSONDocument(
        "id" -> authToken.id,
        "userID" -> authToken.userID,
        "expiry" -> authToken.expiry.map(date => BSONDateTime(date.getMillis))
      )
  }

}
