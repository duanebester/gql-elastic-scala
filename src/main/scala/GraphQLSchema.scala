import sangria.schema._
import sangria.macros.derive._
import sangria.marshalling.sprayJson._
import models.responses._
import models.variables._
import models.common._

case class MyContext(elastic: Elastic, bbox: Option[BBox] = None)

object GraphQLSchema {

  // Responses
  implicit val LocationOutputType = deriveObjectType[Unit, Location]()
  implicit val UserType = deriveObjectType[Unit, User]()
  implicit val CoffeeShopType = deriveObjectType[Unit, CoffeeShop]()
  val UsersResponseType = deriveObjectType[Unit, UsersResponse]()
  val CoffeeShopsResponseType = deriveObjectType[Unit, CoffeeShopsResponse]()

  // Variables
  implicit val LocationInputType = deriveInputObjectType[Location](
    InputObjectTypeName("LocationVariable") // Give name to resolve name conflict
  )
  implicit val BBoxType = deriveInputObjectType[BBox]()

  // Arguments
  val bboxArg = Argument("bbox", OptionInputType(BBoxType))
  val nameArg = Argument("name", OptionInputType(StringType))

  val SearchType = ObjectType(
    "Search",
    fields[MyContext, Unit](
      Field(
        "users",
        UsersResponseType,
        arguments = nameArg :: Nil,
        resolve = (c) => {
          val name = c arg nameArg
          val filter = Filter(name, c.ctx.bbox)
          c.ctx.elastic.searchUsers(filter)
        }
      ),
      Field(
        "coffeeShops",
        CoffeeShopsResponseType,
        arguments = nameArg :: Nil,
        resolve = (c) => {
          val name = c arg nameArg
          val filter = Filter(name, c.ctx.bbox)
          c.ctx.elastic.searchCoffeeShops(filter)
        }
      )
    )
  )

  val QueryType = ObjectType(
    "Query",
    fields[MyContext, Unit](
      Field(
        "geoSearch",
        SearchType,
        arguments = bboxArg :: Nil,
        resolve = c => {
          val bbox = c arg bboxArg
          UpdateCtx(())(_ => c.ctx.copy(bbox = bbox))
        }
      )
    )
  )

  val SchemaDefinition = Schema(QueryType)
}
