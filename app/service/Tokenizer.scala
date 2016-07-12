package service

import java.nio.file.Paths

import com.huaban.analysis.jieba.JiebaSegmenter.SegMode
import com.huaban.analysis.jieba.{WordDictionary, JiebaSegmenter}

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

/**
 * Created by chiashunlu on 2016/6/28.
 */
object Tokenizer {

  val wordDict = WordDictionary.getInstance()
  wordDict.loadUserDict(Paths.get("D:\\BigDataTeam\\workspace\\JiebaTest\\src\\main\\resources\\user_dict.txt"))
  val segmenter:JiebaSegmenter = new JiebaSegmenter()

  /*
  將文章分詞後,儲存成 (詞,詞頻) 的已排序(或不排序)列表,並回傳
   */
  def doSegment(text:String, needToSort:Boolean=false):List[(String,Int)] = {
    val tokenCounts = new collection.mutable.HashMap[String,Int]()
    segmenter.process(text, SegMode.INDEX).asScala.foreach(segToken => {
      val count = tokenCounts.getOrElse(segToken.word, 0)
      tokenCounts.update(segToken.word, count+1)
    })

    if(needToSort){
      tokenCounts.toList.sortBy(a => a._2)(Ordering.Int.reverse)
    }else{
      tokenCounts.toList
    }
  }

  /*
  根據詞在文章中出現的頻率,以及詞在corpus中出現的反頻率,這兩種因子來給予每個詞新的權重
  回傳排序後的 (詞,權重詞頻) 列表,預設取前八大的
   */
  def rawSeg_to_weughtedSeg(rawSeg:List[(String,Int)]):List[(String,Double)] = {
    val totalCount = rawSeg.foldLeft(0)((b,a) => a._2 + b)
    val listBuff: scala.collection.mutable.ListBuffer[(String,Double)] = new scala.collection.mutable.ListBuffer[(String,Double)]()
    rawSeg.foreach(wc => {
      listBuff += wc._1 -> (wc._2/totalCount.toDouble) * Math.log(1/(Math.exp(wordDict.getFreq(wc._1).toDouble)))
    })

    def isAllChinese(word:String):Boolean = {
      word.toCharArray.forall(ch => (ch >= 0x4E00 & ch <= 0x9FA5))
    }

    def isAllEnglish(word:String):Boolean = {
      word.toCharArray.forall(ch => ((ch >= 0x0041 & ch <= 0x005A) || (ch >= 0x0061 & ch <= 0x007A)))
    }

    listBuff.toList.filter(x => (isAllChinese(x._1) | isAllEnglish(x._1) ) & x._1.length!=1).sortBy(a => a._2)(Ordering.Double.reverse).take(8)
  }

}

abstract class Article{
  val id:String
  val seg:List[(String,Int)]
  val weightedSeg:List[(String, Double)]
}

case class FbArticle(id:String, textSeg:List[(String, Double)])

case class PttArticle(id:String, textSeg:List[(String, Double)], pushetSeg:List[(String, Double)])