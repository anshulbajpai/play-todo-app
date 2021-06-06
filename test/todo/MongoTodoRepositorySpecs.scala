package todo

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterEach, MustMatchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Injecting

import java.util.UUID.randomUUID
import scala.concurrent.ExecutionContext.Implicits.global

class MongoTodoRepositorySpecs
    extends WordSpec
    with ScalaFutures
    with GuiceOneAppPerSuite
    with BeforeAndAfterEach
    with MustMatchers
    with Injecting {

  val repo = inject[MongoTodoRepository]

  override protected def beforeEach(): Unit = {
    repo.removeAll().futureValue
  }

  "list" must {
    "return no todos if repo is empty" in {
      repo.list().futureValue mustBe List.empty
    }

    "return todos sorted by created date" in {
      val todo1 =
        Todo(randomUUID().toString, "one", "oneD")
      val todo2 = Todo(randomUUID().toString, "two", "twoD")
      val todo3 =
        Todo(randomUUID().toString, "three", "threeD")

      repo.bulkInsert(List(todo1, todo2, todo3)).futureValue

      repo.list().futureValue mustBe List(todo1, todo2, todo3)
    }
  }

  "create" must {
    "add new todos to the list" in {
      val id = repo.create("one", "oneD").futureValue
      repo.findById(id).futureValue mustBe Some(Todo(id, "one", "oneD"))
    }
  }

  "update" must {
    "return false if id doesn't exist" in {
      repo
        .update(Todo(randomUUID().toString, "title", "descr"))
        .futureValue mustBe false
    }

    "update the todo if todo exist" in {
      val todo =
        Todo(randomUUID().toString, "title", "descr")

      repo.insert(todo).futureValue

      repo
        .update(Todo(todo.id, "new title", "new descr"))
        .futureValue mustBe true

      repo.findById(todo.id).futureValue mustBe Some(
        Todo(todo.id, "new title", "new descr")
      )
    }
  }

  "get" must {
    "return the todo if the id exist" in {
      val todo =
        Todo(randomUUID().toString, "title", "descr")
      repo.insert(todo).futureValue
      repo.get(todo.id).futureValue mustBe Some(todo)
    }

    "return None if the todo with id doesn't exist" in {
      repo.get(randomUUID().toString).futureValue mustBe None
    }
  }

  "delete" must {
    "return false if id doesn't exist" in {
      repo
        .delete(randomUUID().toString)
        .futureValue mustBe false
    }

    "return true if id exist and remove the todo" in {
      val todo =
        Todo(randomUUID().toString, "title", "descr")
      repo.insert(todo).futureValue
      repo.delete(todo.id).futureValue mustBe true
      repo.findById(todo.id).futureValue mustBe None
    }

  }

}
