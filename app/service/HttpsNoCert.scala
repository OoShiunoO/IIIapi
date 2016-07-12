package service

import javax.net.ssl._
import java.security.cert.X509Certificate

import scala.io.Source


/**
 * Created by chiashunlu on 2016/6/17.
 */
object HttpsNoCert {

  object IngenuousTrustManager extends X509TrustManager {
    val getAcceptedIssuers = null

    override def checkClientTrusted(cert: Array[X509Certificate], authType: String) = {
      //don't check
    }
    override def checkServerTrusted(cert: Array[X509Certificate], authType: String) = {
      //don't check
    }
  }

  // Verifies all host names by simply returning true.
  object VerifiesAllHostNames extends HostnameVerifier {
    def verify(s: String, sslSession: SSLSession) = true
  }

  def execute(url: String):String = {
    // SSL Context initialization and configuration
    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, Array(IngenuousTrustManager), new java.security.SecureRandom())
    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory)
    HttpsURLConnection.setDefaultHostnameVerifier(VerifiesAllHostNames)

    // Actual call
    Source.fromURL(url, "UTF-8").mkString
  }
}
