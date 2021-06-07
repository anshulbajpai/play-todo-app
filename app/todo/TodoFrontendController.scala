package todo

import play.api.mvc.MessagesControllerComponents
import todo.html.{ListTodosView, TodoView}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class TodoFrontendController @Inject()(
  mcc: MessagesControllerComponents,
  listTodosView: ListTodosView,
  todoView: TodoView,
  todoApiConnector: TodoApiConnector
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) {

  val list = Action.async { implicit request =>
    todoApiConnector.getAllTodos().map { todos =>
      Ok(listTodosView(todos))
    }
  }

  def get(id: String) = Action.async { implicit request =>
    todoApiConnector.getTodo(id).map { todoOpt =>
      todoOpt match {
        case Some(todo) => Ok(todoView(todo))
        case None       => NotFound
      }
    }
  }
}
