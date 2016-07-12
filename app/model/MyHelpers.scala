package model

import views.html

/**
 * Created by chiashunlu on 2016/5/27.
 */
object MyHelpers {
  import views.html.helper.FieldConstructor
  implicit val myFields = FieldConstructor(html.myFieldConstructorTemplate.f)
}
