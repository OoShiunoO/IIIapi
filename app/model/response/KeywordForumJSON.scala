package model.response

import model.ErrorInfo
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
 * Created by chiashunlu on 2016/6/1.
 */
case class KeywordForumJSON(message: Option[String], total: Option[Long], pageNum: Option[Int], pageSize: Option[Int], result: Option[Seq[KeywordForumResultJSON]]) extends JSONModel

object KeywordForumJSON extends ResponseJSON {

  implicit val resultSingleReads = KeywordForumResultJSON.reads
  val reads:Reads[KeywordForumJSON] = (
      (JsPath \ "message").readNullable[String] and
      (JsPath \ "total").readNullable[Long] and
      (JsPath \ "pageNum").readNullable[Int] and
      (JsPath \ "pageSize").readNullable[Int] and
      (JsPath \ "result").readNullable[Seq[KeywordForumResultJSON]]
    )(KeywordForumJSON.apply _)

  def validate(jsonValue: JsValue): JSONModel = {
    implicit val customRead: Reads[KeywordForumJSON] = reads
    jsonValue.validate[KeywordForumJSON] match {
      case s: JsSuccess[KeywordForumJSON] => s.get
      case e: JsError => ErrorInfo("回傳的JSON綁定失敗(可能是資策會修改了規格),請洽管理人員修改程式", JsError.toJson(e).toString())
    }
  }
}

case class KeywordForumResultJSON(dataId: Option[String], forum: Option[String], channel: Option[String], url: Option[String], title: Option[String], content: Option[String], replyCount: Option[String], time: Option[String], isReply: Option[Boolean]) extends JSONModel

object KeywordForumResultJSON extends ResponseJSON {
  val reads:Reads[KeywordForumResultJSON] = (
      (JsPath \ "dataId").readNullable[String] and
      (JsPath \ "forum").readNullable[String] and
      (JsPath \ "channel").readNullable[String] and
      (JsPath \ "url").readNullable[String] and
      (JsPath \ "title").readNullable[String] and
      (JsPath \ "content").readNullable[String] and
      (JsPath \ "replyCount").readNullable[String] and
      (JsPath \ "time").readNullable[String] and
      (JsPath \ "isReply").readNullable[Boolean]
    )(KeywordForumResultJSON.apply _)

  def validate(jsonValue: JsValue): String = {
    implicit val customRead: Reads[KeywordForumResultJSON] = reads
    jsonValue.validate[KeywordForumResultJSON] match {
      case s: JsSuccess[KeywordForumResultJSON] => s.get.toString
      case e: JsError => JsError.toJson(e).toString()
    }
  }
}
