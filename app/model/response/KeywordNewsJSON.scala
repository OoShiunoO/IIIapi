package model.response

import model.ErrorInfo
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
 * Created by chiashunlu on 2016/6/1.
 */
case class KeywordNewsJSON(message: Option[String], total: Option[Long], pageNum: Option[Int], pageSize: Option[Int], result: Option[Seq[KeywordNewsResultJSON]]) extends JSONModel

object KeywordNewsJSON extends ResponseJSON {

  implicit val resultSingleReads = KeywordNewsResultJSON.reads
  val reads:Reads[KeywordNewsJSON] = (
      (JsPath \ "message").readNullable[String] and
      (JsPath \ "total").readNullable[Long] and
      (JsPath \ "pageNum").readNullable[Int] and
      (JsPath \ "pageSize").readNullable[Int] and
      (JsPath \ "result").readNullable[Seq[KeywordNewsResultJSON]]
    )(KeywordNewsJSON.apply _)

  def validate(jsonValue: JsValue): JSONModel = {
    implicit val customRead: Reads[KeywordNewsJSON] = reads
    jsonValue.validate[KeywordNewsJSON] match {
      case s: JsSuccess[KeywordNewsJSON] => s.get
      case e: JsError => ErrorInfo("回傳的JSON綁定失敗(可能是資策會修改了規格),請洽管理人員修改程式", JsError.toJson(e).toString())
    }
  }
}

case class KeywordNewsResultJSON(dataId: Option[String], sitename: Option[String], Type: Option[String], url: Option[String], title: Option[String], content: Option[String],time: Option[String]) extends JSONModel

object KeywordNewsResultJSON extends ResponseJSON {
  val reads:Reads[KeywordNewsResultJSON] = (
      (JsPath \ "dataId").readNullable[String] and
      (JsPath \ "sitename").readNullable[String] and
      (JsPath \ "type").readNullable[String] and
      (JsPath \ "url").readNullable[String] and
      (JsPath \ "title").readNullable[String] and
      (JsPath \ "content").readNullable[String] and
      (JsPath \ "time").readNullable[String]
    )(KeywordNewsResultJSON.apply _)

  def validate(jsonValue: JsValue): String = {
    implicit val customRead: Reads[KeywordNewsResultJSON] = reads
    jsonValue.validate[KeywordNewsResultJSON] match {
      case s: JsSuccess[KeywordNewsResultJSON] => s.get.toString
      case e: JsError => JsError.toJson(e).toString()
    }
  }
}