package controllers

import javax.inject.Inject

import model.ErrorInfo
import model.response.{KeywordFbJSON, ResponseJSON, JSONModel}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.ws.{WSResponse, WSRequest, WSClient}
import play.api.mvc.{Action, Controller}
import service._
import service.IIICaller._

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.reflect.runtime._
import scala.util.{Failure, Success}

/**
 * Created by chiashunlu on 2016/6/27.
 */

class Facebook @Inject() (ws: WSClient)(val messagesApi: MessagesApi) extends Controller with I18nSupport{

  var token: String = ""

  def show = Action {
    Ok(views.html.analyze.facebook())
  }

  def analyze = Action { request =>
    val queryString = (request.getQueryString("fansPageId"), request.getQueryString("startDate"), request.getQueryString("endDate"))
    if(queryString.productIterator.forall(x => {
      val xx = x.asInstanceOf[Option[String]]
      xx.isDefined & !xx.get.isEmpty
    })){
      val extractResult = getFbFansPages(queryString._1.get, queryString._2.get, queryString._3.get)
      if(extractResult.isContunue){
        val listBuff: scala.collection.mutable.ListBuffer[FbArticle] = new scala.collection.mutable.ListBuffer[FbArticle]()
        extractResult.datas.keySet.foreach(url => {
          val fbArticle:FbArticle = FacebookService.getArticle(url, extractResult.datas.getOrElse(url,""))
          listBuff += fbArticle
        })
        Ok(views.html.analyze.facebook(listBuff.toList))
      }else{
        Ok(views.html.analyze.facebook(null,"呼叫資策會API段,發生錯誤!!\n" + extractResult.errorMessage.toString))
      }

    }else{
      Ok(views.html.analyze.facebook(null,"輸入欄位不可有空值!"))
    }
  }

  case class FacebookResult(isContunue: Boolean, datas: mutable.HashMap[String, String], errorMessage:ErrorInfo)

  def getFbFansPages(fansPageId: String, startDate:String, endDate:String): FacebookResult = {
    var total:Long = -1
    var isContinue:Boolean = true
    val datas = new mutable.HashMap[String, String]()
    var errorMessage:ErrorInfo = null
    def loop(num:Int): Unit = {
      getFbFansPage(num, fansPageId, startDate, endDate) match {
        case k:KeywordFbJSON if(k.result.isDefined)=> {
          k.result.get.foreach(kResult => datas += (kResult.postLink.get -> kResult.message.get))
          if(total==(-1)){total = k.total.get}
        }
        case e:ErrorInfo => {
          errorMessage = e
          isContinue = false
        }
      }
      if(isContinue & num<total/10.0){
        loop(num+1)
      }
    }
    loop(1)
    FacebookResult(isContinue, datas, errorMessage)
  }

  def getFbFansPage(pageNum:Int, fansPageId: String, startDate:String, endDate:String): JSONModel = {
    if(token==""){token = IIICaller.doAuthorize(ws)}
    val request: WSRequest = ws.url("http://ser-dashboard.srm.pw/dashboard/rawapi/keyword/fb")
    var complexRequest: WSRequest = request.withHeaders("Accept" -> "application/json", "Authorization" -> ("Bearer " + token))
    complexRequest = complexRequest.withQueryString("page-id" -> fansPageId)
    complexRequest = complexRequest.withQueryString("start-date" -> startDate)
    complexRequest = complexRequest.withQueryString("end-date" -> endDate)
    complexRequest = complexRequest.withQueryString("page-num" -> pageNum.toString)
    val futureResponse: Future[WSResponse] = complexRequest.get()

    val futureResult:Future[JSONModel] = futureResponse.map(response => {
      if(response.status==200){
        KeywordFbJSON.validate(response.json)
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

}
