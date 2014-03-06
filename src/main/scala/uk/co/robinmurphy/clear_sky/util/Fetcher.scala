package uk.co.robinmurphy.clear_sky.util

import scala.concurrent.Future
import scala.xml.Elem

trait Fetcher {

  def getXml(uri: String): Future[Elem]
  def getJson(uri: String): Future[Option[Any]]

}
