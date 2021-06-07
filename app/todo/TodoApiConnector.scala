package todo

import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TodoApiConnector @Inject()(
  httpClient: HttpClient,
  servicesConfig: ServicesConfig
)(implicit ec: ExecutionContext) {

  private val todosApiBaseUrl = s"${servicesConfig.baseUrl("todo-api")}/api"

  private implicit val todoReads = Json.reads[Todo]

  def getAllTodos()(implicit hc: HeaderCarrier): Future[List[Todo]] =
    httpClient.GET[List[Todo]](s"$todosApiBaseUrl/todos")

  def getTodo(id: String)(implicit hc: HeaderCarrier): Future[Option[Todo]] =
    httpClient.GET[Option[Todo]](s"$todosApiBaseUrl/todos/$id")

}
