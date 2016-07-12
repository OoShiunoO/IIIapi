package service

/**
 * Created by chiashunlu on 2016/7/5.
 */
object FacebookService {

  def getArticle(url:String, message:String):FbArticle = {

    val textSeg = Tokenizer.rawSeg_to_weughtedSeg(Tokenizer.doSegment(message))

    FbArticle(url, textSeg)
  }

}
