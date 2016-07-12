package model.response

import model.ErrorInfo
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
 * Created by chiashunlu on 2016/6/1.
 */
case class TrendFbJSON(message: Option[String], result: Option[Seq[TrendFbResultJSON]]) extends JSONModel

object TrendFbJSON extends ResponseJSON {

  implicit val resultSingleReads = TrendFbResultJSON.reads
  val reads:Reads[TrendFbJSON] = (
      (JsPath \ "message").readNullable[String] and
      (JsPath \ "result").readNullable[Seq[TrendFbResultJSON]]
    )(TrendFbJSON.apply _)

  def validate(jsonValue: JsValue): JSONModel = {
    implicit val customRead: Reads[TrendFbJSON] = reads
    jsonValue.validate[TrendFbJSON] match {
      case s: JsSuccess[TrendFbJSON] => s.get
      case e: JsError => ErrorInfo("回傳的JSON綁定失敗(可能是資策會修改了規格),請洽管理人員修改程式", JsError.toJson(e).toString())
    }
  }
}

case class TrendFbResultJSON(keyword: Option[String], weight: Option[Double]) extends JSONModel

object TrendFbResultJSON extends ResponseJSON {
  val reads:Reads[TrendFbResultJSON] = (
      (JsPath \ "keyword").readNullable[String] and
      (JsPath \ "weight").readNullable[Double]
    )(TrendFbResultJSON.apply _)

  def validate(jsonValue: JsValue): String = {
    implicit val customRead: Reads[TrendFbResultJSON] = reads
    jsonValue.validate[TrendFbResultJSON] match {
      case s: JsSuccess[TrendFbResultJSON] => s.get.toString
      case e: JsError => JsError.toJson(e).toString()
    }
  }
}