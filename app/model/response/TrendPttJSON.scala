package model.response

import model.ErrorInfo
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
 * Created by chiashunlu on 2016/6/1.
 */
case class TrendPttJSON(message: Option[String], result: Option[Seq[TrendPttResultJSON]]) extends JSONModel

object TrendPttJSON extends ResponseJSON {

  implicit val resultSingleReads = TrendPttResultJSON.reads
  val reads:Reads[TrendPttJSON] = (
      (JsPath \ "message").readNullable[String] and
      (JsPath \ "result").readNullable[Seq[TrendPttResultJSON]]
    )(TrendPttJSON.apply _)

  def validate(jsonValue: JsValue): JSONModel = {
    implicit val customRead: Reads[TrendPttJSON] = reads
    jsonValue.validate[TrendPttJSON] match {
      case s: JsSuccess[TrendPttJSON] => s.get
      case e: JsError => ErrorInfo("回傳的JSON綁定失敗(可能是資策會修改了規格),請洽管理人員修改程式", JsError.toJson(e).toString())
    }
  }
}

case class TrendPttResultJSON(keyword: Option[String], weight: Option[Double]) extends JSONModel

object TrendPttResultJSON extends ResponseJSON {
  val reads:Reads[TrendPttResultJSON] = (
      (JsPath \ "keyword").readNullable[String] and
      (JsPath \ "weight").readNullable[Double]
    )(TrendPttResultJSON.apply _)

  def validate(jsonValue: JsValue): String = {
    implicit val customRead: Reads[TrendPttResultJSON] = reads
    jsonValue.validate[TrendPttResultJSON] match {
      case s: JsSuccess[TrendPttResultJSON] => s.get.toString
      case e: JsError => JsError.toJson(e).toString()
    }
  }
}