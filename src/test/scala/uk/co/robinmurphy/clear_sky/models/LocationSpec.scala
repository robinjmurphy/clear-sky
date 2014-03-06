package uk.co.robinmurphy.clear_sky.models

import org.scalatest._

class LocationSpec extends FunSpec with Matchers with BeforeAndAfter {
  var location: Location = _

  before {
    location = new Location("123456", "London", "Greater London")
  }

  describe("Location") {
    it("has an id") {
      location.id should be ("123456")
    }

    it("has a name") {
      location.name should be ("London")
    }

    it("has a container") {
      location.container should be ("Greater London")
    }
  }

}
