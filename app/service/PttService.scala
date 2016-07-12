package service

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

/**
 * Created by chiashunlu on 2016/7/4.
 */
object PttService {

  def getArticle(url:String, message:String):PttArticle = {
    //解開https限制,並爬取ptt web版的推文,之後依照id來重組推文順序,最後回傳字串
    val pushetString:String = parsePushet(getPushet(url))

    val textSeg = Tokenizer.rawSeg_to_weughtedSeg(Tokenizer.doSegment(message))
    val pushetSeg = Tokenizer.rawSeg_to_weughtedSeg(Tokenizer.doSegment(pushetString))

    PttArticle(url, textSeg, pushetSeg)
  }

  def getPushet(url:String):String = Try {
    HttpsNoCert.execute(url)
  } match {
    case Success(s) => s
    case Failure(e) => ""
  }

  def parsePushet(html: String): String = {
    val regex = """<div class="push"><span class="f?1? ?hl push-tag">(.+)</span><span class="f3 hl push-userid">(.+)</span><span class="f3 push-content">:(.+)</span><span class="push-ipdatetime">(.+)\n</span></div>""".r
    //val s = "<div class=\"push\"><span class=\"hl push-tag\">推 </span><span class=\"f3 hl push-userid\">andyeva     </span><span class=\"f3 push-content\">: arc100還等的到嗎？  囧</span><span class=\"push-ipdatetime\"> 03/18 04:36\n</span></div><div class=\"push\"><span class=\"hl push-tag\">推 </span><span class=\"f3 hl push-userid\">ukyo1024    </span><span class=\"f3 push-content\">: 請問這幾顆比起來，比較推薦哪一顆呢? 謝謝</span><span class=\"push-ipdatetime\"> 03/18 12:35\n</span></div><div class=\"push\"><span class=\"hl push-tag\">推 </span><span class=\"f3 hl push-userid\">pita30      </span><span class=\"f3 push-content\">: SSD PLUS是鄉民堪用標準裡最便宜的那顆 價差還不小</span><span class=\"push-ipdatetime\"> 03/18 15:44\n</span></div><div class=\"push\"><span class=\"f1 hl push-tag\">→ </span><span class=\"f3 hl push-userid\">pita30      </span><span class=\"f3 push-content\">: 現在1888 跟其他顆比起來都快價差500了</span><span class=\"push-ipdatetime\"> 03/18 15:45\n</span></div><div class=\"push\"><span class=\"f1 hl push-tag\">→ </span><span class=\"f3 hl push-userid\">youngerwu   </span><span class=\"f3 push-content\">: 這顆簡單說就是便宜穩定mlc,大廠</span><span class=\"push-ipdatetime\"> 03/18 18:49\n</span></div><div class=\"push\"><span class=\"hl push-tag\">推 </span><span class=\"f3 hl push-userid\">hojapaul    </span><span class=\"f3 push-content\">: 比上不足比下有餘，鄉民最低標準款</span><span class=\"push-ipdatetime\"> 03/18 21:13\n</span></div></div>"
    //regex.findAllIn(html).matchData.map(m => m.group(1) + " " + m.group(2) + ": " + m.group(3) + m.group(4)).mkString("\n")
    val userId_talking = new scala.collection.mutable.HashMap[String,mutable.StringBuilder]()
    regex.findAllIn(html).matchData.foreach(m => {
      if(!userId_talking.contains(m.group(2))){
        userId_talking += m.group(2) -> new StringBuilder(m.group(3))
      } else{
        userId_talking.get(m.group(2)).get.append(m.group(3))
      }
    })

    //將回文依照每個id來重組順序,使同一個id的回文能連在一起
    userId_talking.mapValues(sb => sb.toString).toList.map(x => x._2).mkString(".")
  }

}
