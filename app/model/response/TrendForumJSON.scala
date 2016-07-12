package model.response

import model.ErrorInfo
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
 * Created by chiashunlu on 2016/6/1.
 */
case class TrendForumJSON(message: Option[String], result: Option[Seq[TrendForumResultJSON]]) extends JSONModel

object TrendForumJSON extends ResponseJSON {

  implicit val resultSingleReads = TrendForumResultJSON.reads
  val reads:Reads[TrendForumJSON] = (
      (JsPath \ "message").readNullable[String] and
      (JsPath \ "result").readNullable[Seq[TrendForumResultJSON]]
    )(TrendForumJSON.apply _)

  def validate(jsonValue: JsValue): JSONModel = {
    implicit val customRead: Reads[TrendForumJSON] = reads
    jsonValue.validate[TrendForumJSON] match {
      case s: JsSuccess[TrendForumJSON] => s.get
      case e: JsError => ErrorInfo("回傳的JSON綁定失敗(可能是資策會修改了規格),請洽管理人員修改程式", JsError.toJson(e).toString())
    }
  }
}

case class TrendForumResultJSON(keyword: Option[String], weight: Option[Double]) extends JSONModel

object TrendForumResultJSON extends ResponseJSON {
  val reads:Reads[TrendForumResultJSON] = (
      (JsPath \ "keyword").readNullable[String] and
      (JsPath \ "weight").readNullable[Double]
    )(TrendForumResultJSON.apply _)

  def validate(jsonValue: JsValue): String = {
    implicit val customRead: Reads[TrendForumResultJSON] = reads
    jsonValue.validate[TrendForumResultJSON] match {
      case s: JsSuccess[TrendForumResultJSON] => s.get.toString
      case e: JsError => JsError.toJson(e).toString()
    }
  }
}