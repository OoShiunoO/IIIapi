package service

import model.inputform.{FormModel, InputForm}
import model.response.{JSONModel, KeywordFbJSON, ResponseJSON}
import model.{ErrorInfo, TokenInfo, ApiResponse}
import play.api.data.Form
import play.api.libs.ws.{WSClient, WSResponse, WSRequest}
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.mvc.{Results, Result, Request}
import play.api.mvc.Results._

import scala.concurrent.duration.Duration
import scala.util.{Success, Failure}
import scala.concurrent.{Await, Future}

import scala.reflect.runtime.universe

/**
 * Created by chiashunlu on 2016/5/24.
 */
object IIICaller {

  val accunt:String = "cloud@feec.com.tw"
  val password:String = "d59bed4613b88f8dac783dfda567d6e8"
  val token_address = "http://ser-dashboard.srm.pw/dashboard/token/authorize"
  var token:String = ""

  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  def dynamicBind(apiName: String)(implicit request:Request[_]): Form[_] = {

    println("apiName: " + apiName + " is being called.")

    //request.body.toString
    val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)
    val module = runtimeMirror.staticModule("model.inputform." + apiName)
    val obj = runtimeMirror.reflectModule(module)
    val inputForm:InputForm = obj.instance.asInstanceOf[InputForm]

    inputForm.form.bindFromRequest
  }

  def calling(apiName: String, inputData: Any, ws: WSClient): JSONModel = {

    println("calling is start!")

    val fieldApiPath = inputData.getClass.getDeclaredField("apiPath")
    fieldApiPath.setAccessible(true)
    println("=================" + fieldApiPath.get(inputData).toString + "=====================")
    val request: WSRequest = ws.url("http://ser-dashboard.srm.pw/dashboard/rawapi" + fieldApiPath.get(inputData).toString)
    val fieldToken = inputData.getClass.getDeclaredField("token")
    fieldToken.setAccessible(true)
    val tokenString = fieldToken.get(inputData).toString
    var complexRequest: WSRequest = request.withHeaders("Accept" -> "application/json", "Authorization" -> ("Bearer " + tokenString))
    println("=====================" + tokenString + "==============================")
    inputData.getClass.getDeclaredFields().foreach{ field =>
      field.setAccessible(true)
      if((field.getName != "apiPath") && (field.getName != "token") && (field.get(inputData).toString!="")){
        println(field.getName + " => " + field.get(inputData).toString)
        complexRequest = complexRequest.withQueryString(capitalToDashLowerOrLower(field.getName) -> field.get(inputData).toString)
      }
    }
    println(complexRequest.queryString.toString)

    val futureResponse: Future[WSResponse] = complexRequest.get()
    //val futureResult = futureResponse.map(response=> response.json.toString)

    val futureResult:Future[JSONModel] = futureResponse.map(response => {
      if(response.status==200 || response.status==500){
        val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)
        val module = runtimeMirror.staticModule("model.response." + apiName + "JSON")
        val obj = runtimeMirror.reflectModule(module)
        val responseJson: ResponseJSON = obj.instance.asInstanceOf[ResponseJSON]
        responseJson.validate(response.json).asInstanceOf[JSONModel]
      } else {
        ErrorInfo("資策會伺服器異常", response.body)
      }
    })

    futureResult onComplete {
      case Success(s) => s
      case Failure(f) => f.getMessage
    }

    Await.result(futureResult, Duration.Inf)
  }

  def capitalToDashLowerOrLower(ori: String): String = {
    val newStr = ori.map{char =>
      if(char.isUpper){
        "-" + char.toLower
      }else{
        char.toString
      }
    }.mkString("")
    if (newStr.startsWith("-")){newStr.replace("-","")}else{newStr}
  }

  //更新token
  def doAuthorize(ws: WSClient): String = {
    val request: WSRequest = ws.url(token_address)
    val complexRequest: WSRequest =
      request.withHeaders("Accept" -> "application/json")

    val data = Json.obj(
      "email" -> accunt,
      "hashedPassword" -> password
    )

    val futureResponse: Future[WSResponse] = complexRequest.post(data)

    implicit val tokenInfoReads: Reads[TokenInfo] = TokenInfo.reads

    val futureResult:Future[String] = futureResponse.map(response =>
      if(response.status!=200){
        response.body
      } else {
        response.json.validate[TokenInfo] match {
          case s: JsSuccess[TokenInfo] => s.get.accessToken
          case e: JsError => JsError.toJson(e).toString()
        }
      }
    )
    futureResult onComplete {
      case Success(s) => s
      case Failure(f) => f.getMessage
    }

    Await.result(futureResult, Duration.Inf)
  }
}