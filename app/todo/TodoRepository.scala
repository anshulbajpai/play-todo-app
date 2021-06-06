package todo

import com.google.inject.ImplementedBy

import scala.concurrent.Future

@ImplementedBy(classOf[MongoTodoRepository])
trait TodoRepository {
  def list(): Future[List[Todo]]
  def create(title: String, description: String): Future[String]
  def update(todo: Todo): Future[Boolean]
  def get(id: String): Future[Option[Todo]]
  def delete(id: String): Future[Boolean]
}