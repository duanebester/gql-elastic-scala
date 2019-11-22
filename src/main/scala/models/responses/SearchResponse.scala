package models.responses
import models.common._

trait SearchResponse[T <: GeoSearchable] {
  val hits: Seq[T]
  val total: Long
}

trait GeoSearchable {
  val location: Location
}
