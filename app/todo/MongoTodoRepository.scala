package todo

import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
@Singleton
class MongoTodoRepository @Inject()(mongoComponent: ReactiveMongoComponent)(
  implicit ec: ExecutionContext
) extends ReactiveRepository[Todo, String](
      collectionName = "todos",
      mongo = mongoComponent.mongoConnector.db,
      domainFormat = MongoTodoRepository.format,
      idFormat = Format(Reads.StringReads, Writes.StringWrites)
    )
    with TodoRepository {
  override def list(): Future[List[Todo]] = findAll()

  override def create(title: String, description: String): Future[String] = {
    val id = BSONObjectID.generate().stringify
    insert(Todo(id, title, description))
      .map(_ => id)
  }

  override def update(todo: Todo): Future[Boolean] = {

    val updateJson = Json.obj(
      "$set" -> Json
        .obj("title" -> todo.title, "description" -> todo.description)
    )

    findAndUpdate(query = _id(todo.id), update = updateJson)
      .map(_.result[Todo].fold(false)(_ => true))
  }

  override def get(id: String): Future[Option[Todo]] = findById(id)
  override def delete(id: String): Future[Boolean] =
    removeById(id).map(_.n == 1)
}

object MongoTodoRepository {
  val format: Format[Todo] = mongoEntity {
    Json.format[Todo]
  }
}
