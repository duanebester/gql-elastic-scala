package models.outputs

import spray.json.DefaultJsonProtocol
import models.common.Location

case class User(id: Int, name: String, location: Location)
object User extends DefaultJsonProtocol {
  implicit val format = jsonFormat3(User.apply)
}
