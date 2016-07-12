package controllers

import java.io.{Reader, InputStreamReader}
import java.net.{HttpURLConnection, URL}
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection

import model.inputform.KeywordFb
import model.response.{JSONModel, ResponseJSON, KeywordFbJSON}
import model.{ErrorInfo, TokenInfo, ApiResponse}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.ws.{WSResponse, WSRequest, WSClient}
import play.api.mvc.Results._
import play.api.mvc._

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import service.IIICaller._

import service.{HttpsNoCert, IIICaller}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.reflect.runtime._
import scala.util.{Failure, Success}

class Application @Inject() (ws: WSClient)(val messagesApi: MessagesApi) extends Controller with I18nSupport{

  def index = Action {
    Ok(views.html.index())
  }

  def console = Action {
    Ok(views.html.console())
  }

  def changeForm(apiPath: String) = Action { request =>
    val tn = request.body.asText.getOrElse("")
    apiPath match {
      case "keyword/fb" => Ok(views.html.forms.keyword.fb(lastToken = tn))
      case "keyword/forum" => Ok(views.html.forms.keyword.forum(lastToken = tn))
      case "keyword/news" => Ok(views.html.forms.keyword.news(lastToken = tn))
      case "keyword/ptt" => Ok(views.html.forms.keyword.ptt(lastToken = tn))
      case "trend/fb" => Ok(views.html.forms.trend.fb(lastToken = tn))
      case "trend/forum" => Ok(views.html.forms.trend.forum(lastToken = tn))
      case "trend/news" => Ok(views.html.forms.trend.news(lastToken = tn))
      case "trend/ptt" => Ok(views.html.forms.trend.ptt(lastToken = tn))
      case "sentiment" => Ok(views.html.forms.sentiment(lastToken = tn))
      case _ => Ok("TODO")
    }
  }

  def userPost(apiName: String) = Action { implicit request =>

    IIICaller.dynamicBind(apiName).fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        println("Form " + apiName + "binding is fail.")
        BadRequest(formWithErrors.toString)
      },
      inputData => {
        /* binding success, you get the actual value. */
        val result = IIICaller.calling(apiName, inputData, ws)
        println("calling is over, result is get!")

        result match {
          case e:ErrorInfo => Ok(views.html.error(e))
          case _ => Ok(views.html.result(result))
        }

      }
    )
  }

  def updateToken = Action {
    Ok(IIICaller.doAuthorize(ws))
  }

}