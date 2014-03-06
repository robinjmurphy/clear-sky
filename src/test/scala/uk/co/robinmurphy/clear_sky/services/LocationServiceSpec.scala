package uk.co.robinmurphy.clear_sky.services

import org.scalatest._
import scala.concurrent.duration._
import org.scalatest.concurrent._
import uk.co.robinmurphy.clear_sky.util.Fetcher
import org.scalatest.mock.MockitoSugar
import org.mockito.Matchers._
import org.mockito.Mockito._
import scala.concurrent._
import scala.xml.XML
import ExecutionContext.Implicits.global

class LocationServiceSpec extends FunSpec with Matchers with ScalaFutures with BeforeAndAfter with MockitoSugar {
  var fetcher: Fetcher = _
  var locationService: LocationService = _

  before {
    fetcher = mock[Fetcher]
    locationService = new LocationService(fetcher)
  }

  def mockXmlRequestWithFixture(filename: String) {
    when(fetcher.getXml(anyString)).thenReturn(future {
      XML.load(getClass.getResource("/fixtures/" +  filename))
    })
  }

  describe("LocationService") {
    describe(".findById") {
      it("returns a location") {
        mockXmlRequestWithFixture("location.xml")
        val locationRequest = locationService.findById("2643743")

        whenReady(locationRequest, timeout(5.seconds)) { location =>
          location.name should be ("London")
          location.container should be ("Greater London")
          location.id should be ("2643743")
        }
      }
    }

    describe(".find") {
      it("returns a list of location models") {
        mockXmlRequestWithFixture("locations.xml")
        val locationsRequest = locationService.find("London")

        whenReady(locationsRequest, timeout(5.seconds)) { locations =>
          locations.size should be (5)
          locations(0).name should be ("London")
          locations(0).container should be ("Canada")
          locations(0).id should be ("6058560")
        }
      }
    }
  }

}
