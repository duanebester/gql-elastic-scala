import com.sksamuel.elastic4s.{ ElasticClient, ElasticProperties }
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.http.JavaClient
import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import models.common._
import models.responses._
import models.variables._
import com.sksamuel.elastic4s.requests.searches.SearchRequest
import com.sksamuel.elastic4s.requests.searches.queries.geo.Corners
import com.sksamuel.elastic4s.requests.searches.queries.geo.GeoBoundingBoxQuery
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.requests.searches.queries.BoolQuery
import com.sksamuel.elastic4s.sprayjson._

trait ElasticHelpers {
  final val USER_INDEX = "test-users"
  final val COFFEE_SHOPS_INDEX = "test-coffee-shops"

  val elasticProps: ElasticProperties
  val elasticClient: ElasticClient

  def searchUsers(filter: Filter): Future[SearchResponse[User]]
  def searchCoffeeShops(filter: Filter): Future[CoffeeShopsResponse]

  def geoQuery(geom: BBox): GeoBoundingBoxQuery =
    geoBoxQuery("location").corners(
      Corners(
        geom.topLeft.lat.toDouble,
        geom.topLeft.lon.toDouble,
        geom.bottomRight.lat.toDouble,
        geom.bottomRight.lon.toDouble
      )
    )

  def buildQuery(filter: Filter): BoolQuery
}

class Elastic(props: ElasticProperties) extends ElasticHelpers {

  implicit val ec: ExecutionContext = ExecutionContext.global
  override val elasticProps: ElasticProperties = props
  override val elasticClient = ElasticClient(JavaClient(elasticProps))

  def searchUsers(filter: Filter) =
    elasticClient.execute {
      search(USER_INDEX).bool(buildQuery(filter))
    }.map(resp => UsersResponse(resp.result.to[User], resp.result.totalHits))

  def searchCoffeeShops(filter: Filter) =
    elasticClient.execute {
      search(COFFEE_SHOPS_INDEX).bool(buildQuery(filter))
    }.map(resp => CoffeeShopsResponse(resp.result.to[CoffeeShop], resp.result.totalHits))

  def buildQuery(queryFilter: Filter): BoolQuery = {
    var qMusts = mutable.ListBuffer[Query]()
    var qFilters = mutable.ListBuffer[Query]()

    if (queryFilter.name.isDefined) {
      val name = queryFilter.name.get.toLowerCase()
      qMusts += prefixQuery("name", name);
    } else {
      qMusts += matchAllQuery()
    }

    if (queryFilter.bbox.isDefined) {
      val geom = queryFilter.bbox.get
      qFilters += geoQuery(geom)
    }

    return must(qMusts.toSeq).filter(qFilters)
  }
}
