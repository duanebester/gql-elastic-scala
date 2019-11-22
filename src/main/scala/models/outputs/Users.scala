package models.outputs

import spray.json.DefaultJsonProtocol

case class Users(users: Seq[User], totalCount: Long)
object Users extends DefaultJsonProtocol {
  implicit val format = jsonFormat2(Users.apply)
}
