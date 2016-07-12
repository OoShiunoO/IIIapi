package controllers

import javax.inject.Inject

import model.ErrorInfo
import model.response.{KeywordPttJSON, ResponseJSON, JSONModel}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.ws.{WSResponse, WSRequest, WSClient}
import play.api.mvc.{Action, Controller}
import service._
import service.IIICaller._

import scala.StringBuilder
import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.reflect.runtime._
import scala.util.{Failure, Success}

/**
 * Created by chiashunlu on 2016/7/4.
 */

class Ptt @Inject() (ws: WSClient)(val messagesApi: MessagesApi) extends Controller with I18nSupport{

  var token: String = ""

  def show = Action {
    Ok(views.html.analyze.ptt())
  }

  def analyze = Action { request =>
    val queryString = (request.getQueryString("board"), request.getQueryString("startDate"), request.getQueryString("endDate"))
    if(queryString.productIterator.forall(x => {
      val xx = x.asInstanceOf[Option[String]]
      xx.isDefined & !xx.get.isEmpty
    })){
      val extractResult = getPttPosts(queryString._1.get, queryString._2.get, queryString._3.get)
      if(extractResult.isContunue){
        val listBuff: scala.collection.mutable.ListBuffer[PttArticle] = new scala.collection.mutable.ListBuffer[PttArticle]()
        extractResult.datas.keySet.foreach(url => {
          val pttArticle:PttArticle = PttService.getArticle(url, extractResult.datas.getOrElse(url,""))
          listBuff += pttArticle
        })
        Ok(views.html.analyze.ptt(listBuff.toList))
      }else{
        Ok(views.html.analyze.ptt(null,"呼叫資策會API段,發生錯誤!!\n" + extractResult.errorMessage.toString))
      }

    }else{
      Ok(views.html.analyze.ptt(null,"輸入欄位不可有空值!"))
    }
  }

  case class PttResult(isContunue: Boolean, datas: mutable.HashMap[String, String], errorMessage:ErrorInfo)

  def getPttPosts(board: String, startDate:String, endDate:String): PttResult = {
    var total:Long = -1
    var isContinue:Boolean = true
    val datas = new mutable.HashMap[String, String]()
    var errorMessage:ErrorInfo = null
    def loop(num:Int): Unit = {
      getPttPost(num, board, startDate, endDate) match {
        case k:KeywordPttJSON if(k.result.isDefined)=> {
          k.result.get.foreach(kResult => datas += (kResult.url.get -> kResult.content.get))
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
    PttResult(isContinue, datas, errorMessage)
  }

  def getPttPost(pageNum:Int, board: String, startDate:String, endDate:String): JSONModel = {
    if(token==""){token = IIICaller.doAuthorize(ws)}
    val request: WSRequest = ws.url("http://ser-dashboard.srm.pw/dashboard/rawapi/keyword/ptt")
    var complexRequest: WSRequest = request.withHeaders("Accept" -> "application/json", "Authorization" -> ("Bearer " + token))
    complexRequest = complexRequest.withQueryString("board" -> board)
    complexRequest = complexRequest.withQueryString("start-date" -> startDate)
    complexRequest = complexRequest.withQueryString("end-date" -> endDate)
    complexRequest = complexRequest.withQueryString("page-num" -> pageNum.toString)
    val futureResponse: Future[WSResponse] = complexRequest.get()

    val futureResult:Future[JSONModel] = futureResponse.map(response => {
      if(response.status==200){
        KeywordPttJSON.validate(response.json)
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
