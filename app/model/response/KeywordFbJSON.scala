package model.response

import model.ErrorInfo
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
 * Created by chiashunlu on 2016/5/30.
 */
case class KeywordFbJSON(message: Option[String], total: Option[Long], pageNum: Option[Int], pageSize: Option[Int], result: Option[Seq[KeywordFbResultJSON]]) extends JSONModel

object KeywordFbJSON extends ResponseJSON {

  implicit val resultSingleReads = KeywordFbResultJSON.reads
  val reads:Reads[KeywordFbJSON] = (
      (JsPath \ "message").readNullable[String] and
      (JsPath \ "total").readNullable[Long] and
      (JsPath \ "pageNum").readNullable[Int] and
      (JsPath \ "pageSize").readNullable[Int] and
      (JsPath \ "result").readNullable[Seq[KeywordFbResultJSON]]
    )(KeywordFbJSON.apply _)

  def validate(jsonValue: JsValue): JSONModel = {
    implicit val customRead: Reads[KeywordFbJSON] = reads
    jsonValue.validate[KeywordFbJSON] match {
      case s: JsSuccess[KeywordFbJSON] => s.get
      case e: JsError => ErrorInfo("回傳的JSON綁定失敗(可能是資策會修改了規格),請洽管理人員修改程式", JsError.toJson(e).toString())
    }
  }
}

case class KeywordFbResultJSON(dataId: Option[String], postLink: Option[String], pageId: Option[String], fromId: Option[String], fromName: Option[String], message: Option[String], caption: Option[String], description: Option[String], linkName: Option[String], link: Option[String], picture: Option[String], likes: Option[Int], comments: Option[Int], shares: Option[Int], totalResponse: Option[Int], postType: Option[String], time: Option[String]) extends JSONModel

object KeywordFbResultJSON extends ResponseJSON {
  val reads:Reads[KeywordFbResultJSON] = (
      (JsPath \ "dataId").readNullable[String] and
      (JsPath \ "postLink").readNullable[String] and
      (JsPath \ "pageId").readNullable[String] and
      (JsPath \ "fromId").readNullable[String] and
      (JsPath \ "fromName").readNullable[String] and
      (JsPath \ "message").readNullable[String] and
      (JsPath \ "caption").readNullable[String] and
      (JsPath \ "description").readNullable[String] and
      (JsPath \ "linkName").readNullable[String] and
      (JsPath \ "link").readNullable[String] and
      (JsPath \ "picture").readNullable[String] and
      (JsPath \ "likes").readNullable[Int] and
      (JsPath \ "comments").readNullable[Int] and
      (JsPath \ "shares").readNullable[Int] and
      (JsPath \ "totalResponse").readNullable[Int] and
      (JsPath \ "postType").readNullable[String] and
      (JsPath \ "time").readNullable[String]
    )(KeywordFbResultJSON.apply _)

  def validate(jsonValue: JsValue): String = {
    implicit val customRead: Reads[KeywordFbResultJSON] = reads
    jsonValue.validate[KeywordFbResultJSON] match {
      case s: JsSuccess[KeywordFbResultJSON] => s.get.toString
      case e: JsError => JsError.toJson(e).toString()
    }
  }
}
