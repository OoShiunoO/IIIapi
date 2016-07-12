package model

import play.api.libs.json.{Reads, JsPath}
import play.api.libs.functional.syntax._

/**
 * Created by chiashunlu on 2016/5/25.
 */
case class TokenInfo(userId: Int, accessToken: String, refreshToken: String, expiresIn: Int)

object TokenInfo {
  val reads:Reads[TokenInfo] = (
      (JsPath \ "userId").read[Int] and
      (JsPath \ "accessToken").read[String] and
      (JsPath \ "refreshToken").read[String] and
      (JsPath \ "expiresIn").read[Int]
    )(TokenInfo.apply _)
}
