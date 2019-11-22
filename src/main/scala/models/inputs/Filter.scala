package models.inputs

import spray.json.DefaultJsonProtocol

case class Filter(name: Option[String], bbox: Option[BBox])
object Filter extends DefaultJsonProtocol {
  implicit val format = jsonFormat2(Filter.apply)
}
