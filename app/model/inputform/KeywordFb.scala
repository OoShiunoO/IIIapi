package model.inputform

import play.api.data._
import play.api.data.Forms._

/**
 * Created by chiashunlu on 2016/5/26.
 */
case class KeywordFb(pageId: String, startDate: String, endDate: String, keyword: String, searchField: String, pageSize: String,
                      pageNum: String, postType: String, sort: String, token: String, apiPath: String) extends FormModel

object KeywordFb extends InputForm {
  val form = Form(
    mapping(
      "pageId" -> text,
      "startDate" -> text,
      "endDate" -> text,
      "keyword" -> text,
      "searchField" -> text,
      "pageSize" -> text,
      "pageNum" -> text,
      "postType" -> text,
      "sort" -> text,
      "token" -> text,
      "apiPath" -> text
    )(KeywordFb.apply)(KeywordFb.unapply)
  )
}