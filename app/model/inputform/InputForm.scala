package model.inputform

import play.api.data.Form

/**
  * Created by user on 2016/5/29.
  */

//用來讓service作多型的處理
trait InputForm{
  val form:Form[_]
}

trait FormModel{}