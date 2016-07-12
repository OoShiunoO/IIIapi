package model.response

import play.api.libs.json.{JsValue, Reads}

/**
 * Created by chiashunlu on 2016/5/31.
 */
trait ResponseJSON {
  val reads:Reads[_]
  def validate(jsonValue: JsValue):Any
}

trait JSONModel{

}