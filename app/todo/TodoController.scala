package todo

import play.api.http.HeaderNames
import play.api.libs.json._
import play.api.mvc.{BaseController, ControllerComponents}
import todo.TodoController.CreateTodoRequest

import java.util.UUID.randomUUID
import javax.inject.{Inject, Singleton}
import scala.collection.mutable

@Singleton
class TodoController(val controllerComponents: ControllerComponents,
                     todos: mutable.ListBuffer[Todo])
    extends BaseController {

  @Inject() def this(controllerComponents: ControllerComponents) =
    this(controllerComponents, mutable.ListBuffer.empty)

  private implicit val todoWrites = Json.writes[Todo]

  val list = Action {
    Ok(Json.toJson(todos))
  }

  val create = Action(parse.json) { request =>
    val createTodoRequest = request.body.as[CreateTodoRequest]
    val todoId = randomUUID().toString
    todos += Todo(
      todoId,
      createTodoRequest.title,
      createTodoRequest.description
    )
    Created.withHeaders(
      HeaderNames.LOCATION -> routes.TodoController.get(todoId).url
    )
  }

  def get(id: String) = Action {
    todos.find(_.id == id) match {
      case Some(todo) => Ok(Json.toJson(todo))
      case None       => NotFound
    }
  }

  def update(id: String) = Action(parse.json) { request =>
    todos.find(_.id == id) match {
      case Some(originalTodo) =>
        val createTodoRequest = request.body.as[CreateTodoRequest]
        todos -= originalTodo
        todos += Todo(
          id,
          createTodoRequest.title,
          createTodoRequest.description
        )
        NoContent
      case None => NotFound
    }
  }

  def delete(id: String) = Action {
    todos.find(_.id == id) match {
      case Some(todo) =>
        todos -= todo
        NoContent
      case None => NotFound
    }
  }
}

object TodoController {
  private case class CreateTodoRequest(title: String, description: String)
  private object CreateTodoRequest {
    implicit val reads: Reads[CreateTodoRequest] = Json.reads[CreateTodoRequest]
  }
}
