package models.responses

import spray.json.DefaultJsonProtocol
import models.common._

case class CoffeeShop(id: Int, name: String, location: Location) extends GeoSearchable
object CoffeeShop extends DefaultJsonProtocol {
  implicit val format = jsonFormat3(CoffeeShop.apply)
}

case class CoffeeShopsResponse(hits: Seq[CoffeeShop], total: Long) extends SearchResponse[CoffeeShop]
object CoffeeShopsResponse extends DefaultJsonProtocol {
  implicit def format = jsonFormat2(CoffeeShopsResponse.apply)
}
