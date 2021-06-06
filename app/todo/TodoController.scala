package todo

import play.api.http.HeaderNames
import play.api.libs.json._
import play.api.mvc.{BaseController, ControllerComponents}
import todo.TodoController.CreateTodoRequest

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class TodoController @Inject()(
  val controllerComponents: ControllerComponents,
  todoRepository: TodoRepository
)(implicit ec: ExecutionContext)
    extends BaseController {

  private implicit val todoWrites = Json.writes[Todo]

  val list = Action.async {
    todoRepository.list().map(Json.toJson(_)).map(Ok(_))
  }

  val create = Action(parse.json).async { request =>
    val createTodoRequest = request.body.as[CreateTodoRequest]
    todoRepository
      .create(createTodoRequest.title, createTodoRequest.description)
      .map(
        id =>
          Created.withHeaders(
            HeaderNames.LOCATION -> routes.TodoController
              .get(id)
              .url
          )
      )
  }

  def get(id: String) = Action.async {
    todoRepository.get(id).map {
      case Some(todo) => Ok(Json.toJson(todo))
      case None       => NotFound
    }
  }

  def update(id: String) = Action(parse.json).async { request =>
    val createTodoRequest = request.body.as[CreateTodoRequest]
    todoRepository
      .update(Todo(id, createTodoRequest.title, createTodoRequest.description))
      .map {
        case true  => NoContent
        case false => NotFound
      }
  }

  def delete(id: String) = Action.async {
    todoRepository.delete(id).map {
      case true  => NoContent
      case false => NotFound
    }
  }
}

object TodoController {
  private case class CreateTodoRequest(title: String, description: String)
  private object CreateTodoRequest {
    implicit val reads: Reads[CreateTodoRequest] = Json.reads[CreateTodoRequest]
  }
}