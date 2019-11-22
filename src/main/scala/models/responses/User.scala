package models.responses

import spray.json.DefaultJsonProtocol
import models.common._

case class User(id: Int, name: String, location: Location) extends GeoSearchable
object User extends DefaultJsonProtocol {
  implicit val format = jsonFormat3(User.apply)
}

case class UsersResponse(hits: Seq[User], total: Long) extends SearchResponse[User]
object UsersResponse extends DefaultJsonProtocol {
  implicit def format = jsonFormat2(UsersResponse.apply)
}
