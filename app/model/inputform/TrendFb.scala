package model.inputform

import play.api.data._
import play.api.data.Forms._

/**
 * Created by chiashunlu on 2016/6/1.
 */
case class TrendFb(pageId: String, keyword: String, startDate: String, endDate: String, limit: String, threshold: String,
                   token: String, apiPath: String) extends FormModel

object TrendFb extends InputForm {
  val form = Form(
    mapping(
      "pageId" -> text,
      "keyword" -> text,
      "startDate" -> text,
      "endDate" -> text,
      "limit" -> text,
      "threshold" -> text,
      "token" -> text,
      "apiPath" -> text
    )(TrendFb.apply)(TrendFb.unapply)
  )
}