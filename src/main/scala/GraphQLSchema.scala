import sangria.schema._
import sangria.macros.derive._
import sangria.marshalling.sprayJson._
import sangria.execution.deferred._
import models.common._
import models.outputs._
import models.inputs._

case class MyContext(elastic: Elastic)

object GraphQLSchema {

  // Outputs
  implicit val LocationType = deriveObjectType[Unit, Location]()
  implicit val UserType = deriveObjectType[Unit, User]()
  val UsersType = deriveObjectType[Unit, Users]()

  // Inputs
  implicit val FilterLocationType = deriveInputObjectType[Location](
    InputObjectTypeName("FilterLocation"),
    InputObjectTypeDescription("A location filter")
  )
  implicit val FilterBBoxType = deriveInputObjectType[BBox](
    InputObjectTypeName("FilterBBox"),
    InputObjectTypeDescription("A bbox filter containing a topLeft, and bottomRight location filters")
  )
  val FilterType = deriveInputObjectType[Filter](
    InputObjectTypeName("Filter"),
    InputObjectTypeDescription("A filter object containing an optional BBox filter and an optional name filter")
  )

  // Input Arguments
  val Id = Argument("id", IntType)
  val Ids = Argument("ids", ListInputType(IntType))
  val filter = Argument("filter", FilterType)

  val usersFetcher = Fetcher(
    (ctx: MyContext, ids: Seq[Int]) => ctx.elastic.getUsers(ids)
  )(HasId(_.id))

  val QueryType = ObjectType(
    "Query",
    fields[MyContext, Unit](
      Field(
        "searchUsers",
        UsersType,
        arguments = filter :: Nil,
        resolve = c => c.ctx.elastic.searchUsers(c.arg(filter))
      )
    )
  )

  val Resolver = DeferredResolver.fetchers(usersFetcher)
  val SchemaDefinition = Schema(QueryType)
}
