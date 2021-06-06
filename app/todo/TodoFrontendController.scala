package todo

import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{BaseController, ControllerComponents}
import todo.html.{ListTodosView, TodoView}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class TodoFrontendController @Inject()(
  val controllerComponents: ControllerComponents,
  listTodosView: ListTodosView,
  todoView: TodoView,
  configuration: Configuration,
  wsClient: WSClient
)(implicit ec: ExecutionContext)
    extends BaseController {

  private val todosApiBaseUrl = s"${configuration.get[String]("todos-api-baseurl")}/api"

  private implicit val todoReads = Json.reads[Todo]

  val list = Action.async {
    val responseFuture = wsClient.url(s"$todosApiBaseUrl/todos").get()
    responseFuture.map { response =>
      val todos = response.json.as[List[Todo]]
      Ok(listTodosView(todos))
    }
  }

  def get(id: String) = Action.async {
    val responseFuture = wsClient.url(s"$todosApiBaseUrl/todos/$id").get()
    responseFuture.map { response =>
      if (response.status == NOT_FOUND) NotFound
      else {
        val todo = response.json.as[Todo]
        Ok(todoView(todo))
      }
    }
  }
}
