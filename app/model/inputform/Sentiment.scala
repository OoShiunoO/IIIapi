package model.inputform

import play.api.data._
import play.api.data.Forms._

/**
 * Created by chiashunlu on 2016/6/1.
 */
case class Sentiment(dataId: String, token: String, apiPath: String) extends FormModel

object Sentiment extends InputForm {
  val form = Form(
    mapping(
      "dataId" -> text,
      "token" -> text,
      "apiPath" -> text
    )(Sentiment.apply)(Sentiment.unapply)
  )
}