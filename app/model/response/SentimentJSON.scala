package model.response

import model.ErrorInfo
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
 * Created by chiashunlu on 2016/6/1.
 */
case class SentimentJSON(message: Option[String], result: Option[SentimentResultJSON]) extends JSONModel

object SentimentJSON extends ResponseJSON {

  implicit val resultSingleReads = SentimentResultJSON.reads
  val reads:Reads[SentimentJSON] = (
      (JsPath \ "message").readNullable[String] and
      (JsPath \ "result").readNullable[SentimentResultJSON]
    )(SentimentJSON.apply _)

  def validate(jsonValue: JsValue): JSONModel = {
    implicit val customRead: Reads[SentimentJSON] = reads
    jsonValue.validate[SentimentJSON] match {
      case s: JsSuccess[SentimentJSON] => s.get
      case e: JsError => ErrorInfo("回傳的JSON綁定失敗(可能是資策會修改了規格),請洽管理人員修改程式", JsError.toJson(e).toString())
    }
  }
}

case class SentimentResultJSON(dataId: Option[String], positiveCount: Option[Int], negativeCount: Option[Int], postiveWords: Option[Seq[String]], negativeWords: Option[Seq[String]], sentiment: Option[String]) extends JSONModel

object SentimentResultJSON extends ResponseJSON {
  val reads:Reads[SentimentResultJSON] = (
      (JsPath \ "dataId").readNullable[String] and
      (JsPath \ "positiveCount").readNullable[Int] and
      (JsPath \ "negativeCount").readNullable[Int] and
      (JsPath \ "postiveWords").readNullable[Seq[String]] and
      (JsPath \ "negativeWords").readNullable[Seq[String]] and
      (JsPath \ "sentiment").readNullable[String]
    )(SentimentResultJSON.apply _)

  def validate(jsonValue: JsValue): String = {
    implicit val customRead: Reads[SentimentResultJSON] = reads
    jsonValue.validate[SentimentResultJSON] match {
      case s: JsSuccess[SentimentResultJSON] => s.get.toString
      case e: JsError => JsError.toJson(e).toString()
    }
  }
}