package models.inputs

import spray.json.DefaultJsonProtocol
import models.common.Location

case class BBox(topLeft: Location, bottomRight: Location)
object BBox extends DefaultJsonProtocol {
  implicit val format = jsonFormat2(BBox.apply)
}
