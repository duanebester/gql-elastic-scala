import com.sksamuel.elastic4s.{ ElasticClient, ElasticProperties }
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.http.JavaClient
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import models.outputs._
import models.inputs._
import com.sksamuel.elastic4s.requests.searches.queries.geo.Corners
import com.sksamuel.elastic4s.requests.searches.queries.geo.GeoBoundingBoxQuery
import com.sksamuel.elastic4s.requests.searches.SearchRequest
import com.sksamuel.elastic4s.requests.searches.queries.Query
import scala.collection.mutable
import com.sksamuel.elastic4s.requests.searches.queries.BoolQuery
import com.sksamuel.elastic4s.sprayjson._

trait ElasticHelpers {
  final val USER_INDEX = "test-users"

  val elasticProps: ElasticProperties
  val elasticClient: ElasticClient

  def searchUsers(filter: Filter): Future[Users]

  def getUser(id: Int): Future[User]

  def getUsers(ids: Seq[Int]): Future[Seq[User]]

  def geoQuery(geom: BBox): GeoBoundingBoxQuery =
    geoBoxQuery("location").corners(
      Corners(
        geom.topLeft.lat.toDouble,
        geom.topLeft.lon.toDouble,
        geom.bottomRight.lat.toDouble,
        geom.bottomRight.lon.toDouble
      )
    )

  def buildUserQuery(filter: Filter): BoolQuery
}

class Elastic(props: ElasticProperties) extends ElasticHelpers {

  implicit val ec: ExecutionContext = ExecutionContext.global
  override val elasticProps: ElasticProperties = props
  override val elasticClient = ElasticClient(JavaClient(elasticProps))

  def searchUsers(filter: Filter) =
    elasticClient.execute {
      search(USER_INDEX).bool(buildUserQuery(filter))
    }.map(resp => Users(resp.result.to[User], resp.result.totalHits))

  def getUser(id: Int) =
    elasticClient.execute {
      search(USER_INDEX).query(idsQuery(id))
    }.map(resp => resp.result.to[User].head)

  def getUsers(ids: Seq[Int]) =
    elasticClient.execute {
      search(USER_INDEX).query(idsQuery(ids))
    }.map(resp => resp.result.to[User])

  def buildUserQuery(queryFilter: Filter): BoolQuery = {
    var userMusts = mutable.ListBuffer[Query]()
    var userFilters = mutable.ListBuffer[Query]()

    if (queryFilter.name.isDefined) {
      val name = queryFilter.name.get.toLowerCase()
      userMusts += prefixQuery("name", name);
    } else {
      userMusts += matchAllQuery()
    }

    if (queryFilter.bbox.isDefined) {
      val geom = queryFilter.bbox.get
      userFilters += geoQuery(geom)
    }

    return must(userMusts.toSeq).filter(userFilters)
  }
}
