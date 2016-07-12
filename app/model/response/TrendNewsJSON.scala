package model.response

import model.ErrorInfo
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
 * Created by chiashunlu on 2016/6/1.
 */
case class TrendNewsJSON(message: Option[String], result: Option[Seq[TrendNewsResultJSON]]) extends JSONModel

object TrendNewsJSON extends ResponseJSON {

  implicit val resultSingleReads = TrendNewsResultJSON.reads
  val reads:Reads[TrendNewsJSON] = (
      (JsPath \ "message").readNullable[String] and
      (JsPath \ "result").readNullable[Seq[TrendNewsResultJSON]]
    )(TrendNewsJSON.apply _)

  def validate(jsonValue: JsValue): JSONModel = {
    implicit val customRead: Reads[TrendNewsJSON] = reads
    jsonValue.validate[TrendNewsJSON] match {
      case s: JsSuccess[TrendNewsJSON] => s.get
      case e: JsError => ErrorInfo("回傳的JSON綁定失敗(可能是資策會修改了規格),請洽管理人員修改程式", JsError.toJson(e).toString())
    }
  }
}

case class TrendNewsResultJSON(keyword: Option[String], weight: Option[Double]) extends JSONModel

object TrendNewsResultJSON extends ResponseJSON {
  val reads:Reads[TrendNewsResultJSON] = (
      (JsPath \ "keyword").readNullable[String] and
      (JsPath \ "weight").readNullable[Double]
    )(TrendNewsResultJSON.apply _)

  def validate(jsonValue: JsValue): String = {
    implicit val customRead: Reads[TrendNewsResultJSON] = reads
    jsonValue.validate[TrendNewsResultJSON] match {
      case s: JsSuccess[TrendNewsResultJSON] => s.get.toString
      case e: JsError => JsError.toJson(e).toString()
    }
  }
}