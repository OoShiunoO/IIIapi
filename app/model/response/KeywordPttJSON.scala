package model.response

import model.ErrorInfo
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
 * Created by chiashunlu on 2016/6/1.
 */
case class KeywordPttJSON(message: Option[String], total: Option[Long], pageNum: Option[Int], pageSize: Option[Int], result: Option[Seq[KeywordPttResultJSON]]) extends JSONModel

object KeywordPttJSON extends ResponseJSON {

  implicit val resultSingleReads = KeywordPttResultJSON.reads
  val reads:Reads[KeywordPttJSON] = (
      (JsPath \ "message").readNullable[String] and
      (JsPath \ "total").readNullable[Long] and
      (JsPath \ "pageNum").readNullable[Int] and
      (JsPath \ "pageSize").readNullable[Int] and
      (JsPath \ "result").readNullable[Seq[KeywordPttResultJSON]]
    )(KeywordPttJSON.apply _)

  def validate(jsonValue: JsValue): JSONModel = {
    implicit val customRead: Reads[KeywordPttJSON] = reads
    jsonValue.validate[KeywordPttJSON] match {
      case s: JsSuccess[KeywordPttJSON] => s.get
      case e: JsError => ErrorInfo("回傳的JSON綁定失敗(可能是資策會修改了規格),請洽管理人員修改程式", JsError.toJson(e).toString())
    }
  }
}

case class KeywordPttResultJSON(dataId: Option[String], board: Option[String], url: Option[String], title: Option[String], content: Option[String], replyCount: Option[Int], time: Option[String], isReply: Option[Boolean]) extends JSONModel

object KeywordPttResultJSON extends ResponseJSON {
  val reads:Reads[KeywordPttResultJSON] = (
      (JsPath \ "dataId").readNullable[String] and
      (JsPath \ "board").readNullable[String] and
      (JsPath \ "url").readNullable[String] and
      (JsPath \ "title").readNullable[String] and
      (JsPath \ "content").readNullable[String] and
      (JsPath \ "replyCount").readNullable[Int] and
      (JsPath \ "time").readNullable[String] and
      (JsPath \ "isReply").readNullable[Boolean]
    )(KeywordPttResultJSON.apply _)

  def validate(jsonValue: JsValue): String = {
    implicit val customRead: Reads[KeywordPttResultJSON] = reads
    jsonValue.validate[KeywordPttResultJSON] match {
      case s: JsSuccess[KeywordPttResultJSON] => s.get.toString
      case e: JsError => JsError.toJson(e).toString()
    }
  }
}