package todo

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

@Singleton
class InMemoryTodoRepository(todos: ListBuffer[Todo]) extends TodoRepository {

  @Inject() def this() = this(ListBuffer.empty)

  override def list(): Future[List[Todo]] =
    Future.successful(todos.toList)

  override def create(title: String, description: String): Future[String] = {
    val id = UUID.randomUUID().toString
    todos += Todo(id, title, description)
    Future.successful(id)
  }

  override def update(todo: Todo): Future[Boolean] =
    todos.find(_.id == todo.id) match {
      case Some(originalTodo) =>
        todos -= originalTodo
        todos += todo
        Future.successful(true)
      case None => Future.successful(false)
    }

  override def get(id: String): Future[Option[Todo]] =
    Future.successful(todos.find(_.id == id))

  override def delete(id: String): Future[Boolean] =
    todos.find(_.id == id) match {
      case Some(todo) =>
        todos -= todo
        Future.successful(true)
      case None => Future.successful(false)
    }
}
