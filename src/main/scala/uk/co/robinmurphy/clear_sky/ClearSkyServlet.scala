package uk.co.robinmurphy.clear_sky

import uk.co.robinmurphy.clear_sky.services.{ForecastService, LocationService}
import scala.concurrent._
import org.scalatra.AsyncResult
import akka.actor.ActorSystem

class ClearSkyServlet(system: ActorSystem) extends ClearSkyStack {
  val locationService = new LocationService()
  val forecastService = new ForecastService()

  override protected implicit def executor: ExecutionContext = system.dispatcher

  get("/") {
    contentType = "text/html"

    mustache("index")
  }

  get("/search") {
    contentType = "text/html"

    val query = params.getOrElse("query", "")
    val locationsRequest = locationService.find(query)

    new AsyncResult { val is =
      locationsRequest map { locations =>
        mustache(
          "search",
          "query" -> query,
          "results" -> locations,
          "hasResults" -> locations.nonEmpty,
          "noResults" -> locations.isEmpty
        )
      }
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

    new AsyncResult { val is =
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
      status = 500

      mustache(
        "error",
        "error" -> e.getMessage
      )
  }
}
