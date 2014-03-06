package uk.co.robinmurphy.clear_sky.services

import org.scalatest._
import org.scalatest.mock.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent._
import ExecutionContext.Implicits.global
import duration._
import scala.io._
import scala.util.parsing.json.JSON
import uk.co.robinmurphy.clear_sky.util.Fetcher
import org.mockito.Mockito._
import org.mockito.Matchers._

class ForecastServiceSpec extends FunSpec with Matchers with ScalaFutures with MockitoSugar with BeforeAndAfter {
  var fetcher: Fetcher = _
  var forecastService: ForecastService = _

  before {
    fetcher = mock[Fetcher]
    forecastService = new ForecastService(fetcher)
  }

  def mockJsonRequestWithFixture(filename: String) {
    when(fetcher.getJson(anyString())).thenReturn(future {
      val json = Source.fromURL(getClass.getResource("/fixtures/" + filename)).mkString
      JSON.parseFull(json)
    })
  }

  describe("ForecastService") {
    describe(".findById") {
      it("returns a forecast model") {
        mockJsonRequestWithFixture("forecast.json")
        val forecast = forecastService.findById("2643743")

        whenReady(forecast, timeout(5.seconds)) { _forecast =>
          _forecast.temperature should equal(7)
          _forecast.weatherType should equal("Clear Sky")
          _forecast.windSpeed should equal(4)
          _forecast.windDirection should equal("Westerly")
        }
      }
    }
  }

}
