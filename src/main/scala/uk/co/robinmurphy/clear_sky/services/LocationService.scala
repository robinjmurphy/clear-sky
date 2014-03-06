package uk.co.robinmurphy.clear_sky.services

import uk.co.robinmurphy.clear_sky.models.Location
import scala.xml._
import scala.concurrent._
import ExecutionContext.Implicits.global
import uk.co.robinmurphy.clear_sky.util.{HttpFetcher, Fetcher}
import java.net.URLEncoder

class LocationService(fetcher: Fetcher) {
  val searchUrl = "https://open.live.bbc.co.uk/locator/locations?s={query}&a=true&pt=settlement"
  val locationUrl = "http://open.live.bbc.co.uk/locator/locations/{id}"

  def this() {
    this(HttpFetcher)
  }

  def findById(id: String): Future[Location] = {
    val url = locationUrl.replace("{id}", id)
    val xml = fetcher.getXml(url)

    xml map { elem =>
      locationFromXml(elem)
    }
  }

  def find(query: String): Future[List[Location]] = {
    val url = searchUrl.replace("{query}", URLEncoder.encode(query, "UTF-8"))
    val xml = fetcher.getXml(url)

    xml map { elem =>
      (elem \ "results" \ "location").map { location => locationFromXml(location) }.toList
    }
  }

  private def locationFromXml(xml: Node): Location = {
    val id = xml \ "id"
    val name = xml \ "name"
    val container = xml \ "container"

    new Location(id.text, name.text, container.text)
  }

}
