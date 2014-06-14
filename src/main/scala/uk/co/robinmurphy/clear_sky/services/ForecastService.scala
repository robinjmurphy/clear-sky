package uk.co.robinmurphy.clear_sky.services

import uk.co.robinmurphy.clear_sky.models.Forecast
import scala.concurrent._
import ExecutionContext.Implicits.global
import uk.co.robinmurphy.clear_sky.util.{HttpFetcher, Fetcher}

class ForecastService(fetcher: Fetcher) {
  val forecastUrl = "http://open.live.bbc.co.uk/weather/feeds/en/{id}/3hourlyforecast.json"

  def this() {
    this(HttpFetcher)
  }

  def findById(id: String): Future[Forecast] = {
    val url = forecastUrl.replace("{id}", id)
    val request = fetcher.getJson(url)

    request.map { json =>
      forecastFromJson(json)
    }
  }

  private def forecastFromJson(json: Option[Any]): Forecast = {
    val forecastContent = json.get.asInstanceOf[Map[String, Any]]("forecastContent")
    val forecast = forecastContent.asInstanceOf[Map[String, List[Map[String, Any]]]]("forecasts")(0)
    val temperature = forecast("temperature").asInstanceOf[Map[String, Double]]
    val wind = forecast("wind").asInstanceOf[Map[String, Any]]
    val windSpeed = wind("windspeed").asInstanceOf[Map[String, Double]]
    val windSpeedMph = windSpeed("mph")
    val temperatureC = temperature("centigrade")
    val windDirection = wind("directionDesc").asInstanceOf[String]
    val weatherType = forecast("weatherType").asInstanceOf[String]

    new Forecast(temperatureC.toInt, weatherType, windSpeedMph.toInt, windDirection)
  }
}
