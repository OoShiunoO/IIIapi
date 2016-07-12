package model.inputform

import play.api.data._
import play.api.data.Forms._

/**
 * Created by chiashunlu on 2016/6/1.
 */
case class TrendForum(forum: String, channel: String, keyword: String, startDate: String, endDate: String, limit: String, threshold: String,
                   token: String, apiPath: String) extends FormModel

object TrendForum extends InputForm {
  val form = Form(
    mapping(
      "forum" -> text,
      "channel" -> text,
      "keyword" -> text,
      "startDate" -> text,
      "endDate" -> text,
      "limit" -> text,
      "threshold" -> text,
      "token" -> text,
      "apiPath" -> text
    )(TrendForum.apply)(TrendForum.unapply)
  )
}