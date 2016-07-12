package model.inputform

import play.api.data._
import play.api.data.Forms._

/**
 * Created by chiashunlu on 2016/6/1.
 */
case class KeywordPtt(board: String, startDate: String, endDate: String, keyword: String, searchField: String,
                       pageSize: String, pageNum: String, sort: String, token: String, apiPath: String) extends FormModel

object KeywordPtt extends InputForm {
  val form = Form(
    mapping(
      "board" -> text,
      "startDate" -> text,
      "endDate" -> text,
      "keyword" -> text,
      "searchField" -> text,
      "pageSize" -> text,
      "pageNum" -> text,
      "sort" -> text,
      "token" -> text,
      "apiPath" -> text
    )(KeywordPtt.apply)(KeywordPtt.unapply)
  )
}