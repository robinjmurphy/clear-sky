package uk.co.robinmurphy.clear_sky

import uk.co.robinmurphy.clear_sky.services.{ForecastService, LocationService}
import scala.concurrent._
import ExecutionContext.Implicits.global
import org.scalatra.AsyncResult
import duration._
import scala.util._

class ClearSkyServlet extends ClearSkyStack {
  val locationService = new LocationService()
  val forecastService = new ForecastService()

  get("/") {
    contentType = "text/html"

    mustache(
      "index",
      "query" -> "",
      "results" -> Nil,
      "hasResults" -> false,
      "noResults" -> false
    )
  }

  get("/search") {
    contentType = "text/html"

    val query = params.getOrElse("query", "")
    val locationsRequest = locationService.find(query)

    locationsRequest map { locations =>
      mustache(
        "index",
        "query" -> query,
        "results" -> locations,
        "hasResults" -> locations.nonEmpty,
        "noResults" -> locations.isEmpty
      )
    }
  }

  get("/locations/:geoId") {
    contentType = "text/html"

    val geoId = params("geoId")
    val locationRequest = locationService.findById(geoId)
    val forecastRequest = forecastService.findById(geoId)

    val requests = for {
      location <- locationRequest
      forecast <- forecastRequest
    } yield (location, forecast)

    requests map { data =>
      val location = data._1
      val forecast = data._2

      mustache(
        "location",
        "name" -> location.name,
        "container" -> location.container,
        "temperature" -> forecast.temperature,
        "weatherType" -> forecast.weatherType,
        "windSpeed" -> forecast.windSpeed,
        "windDirection" -> forecast.windDirection
      )
    }
  }

  notFound {
    serveStaticResource() getOrElse {
      contentType = "text/html"
      status = 404

      mustache(
        "error",
        "error" -> "404. Nothing to see here."
      )
    }
  }

  error {
    case e =>
      mustache(
        "error",
        "error" -> e.getMessage
      )
  }

  override protected implicit def executor: ExecutionContext = global
}
