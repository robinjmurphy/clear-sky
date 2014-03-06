package uk.co.robinmurphy.clear_sky.util

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.xml.Elem
import dispatch.{Http, as, url}
import scala.util.parsing.json.JSON
import java.util.zip.GZIPInputStream
import java.io.ByteArrayInputStream
import org.apache.commons.io.IOUtils

object HttpFetcher extends Fetcher {

  override def getXml(uri: String): Future[Elem] = {
    val resource = url(uri)

    Http(resource OK as.xml.Elem)
  }

  override def getJson(uri: String): Future[Option[Any]] = {
    val request = getGzipped(uri)

    request map { body =>
      JSON.parseFull(body)
    }
  }

  private def getGzipped(uri: String): Future[String] = {
    val resource = url(uri)

    Http(
      resource.addHeader("Accept-Encoding", "gzip") OK as.Bytes) map { bytes =>
      val gis = new GZIPInputStream(new ByteArrayInputStream(bytes))

      IOUtils.toString(gis);
    }
  }

}
