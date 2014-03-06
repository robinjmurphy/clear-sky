package uk.co.robinmurphy.clear_sky.models

import org.scalatest._

class ForecastSpec extends FunSpec with Matchers with BeforeAndAfter {

  var forecast: Forecast = _

  before {
    forecast = new Forecast(12, "Clear Sky", 15, "South Easterly")
  }

  describe("Forecast") {
    it("has a temperature") {
      forecast.temperature should be (12)
    }

    it("has a weather type") {
      forecast.weatherType should be ("Clear Sky")
    }

    it("has a wind speed") {
      forecast.windSpeed should be (15)
    }

    it("has a wind direction") {
      forecast.windDirection should be ("South Easterly")
    }
  }

}
