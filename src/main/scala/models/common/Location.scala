package models.common

import spray.json.DefaultJsonProtocol

case class Location(lat: String, lon: String)
object Location extends DefaultJsonProtocol {
  implicit val format = jsonFormat2(Location.apply)
}
