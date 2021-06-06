package todo

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpec}

import java.util.UUID.randomUUID
import scala.collection.mutable.ListBuffer

class InMemoryTodoRepositorySpecs
    extends WordSpec
    with MustMatchers
    with ScalaFutures {

  "list" must {
    "return no todos if repo is empty" in {
      val repo = new InMemoryTodoRepository()
      repo.list().futureValue mustBe List.empty
    }

    "return todos" in {
      val todo1 =
        Todo(randomUUID().toString, "one", "oneD")
      val todo2 = Todo(randomUUID().toString, "two", "twoD")
      val todo3 =
        Todo(randomUUID().toString, "three", "threeD")
      val repo = new InMemoryTodoRepository(ListBuffer(todo1, todo2, todo3))

      repo.list().futureValue mustBe List(todo1, todo2, todo3)
    }
  }

  "create" must {
    "add new todos to the list" in {
      val list = ListBuffer.empty[Todo]
      val repo = new InMemoryTodoRepository(list)
      val id = repo.create("one", "oneD").futureValue
      list mustBe ListBuffer(Todo(id, "one", "oneD"))
    }
  }

  "update" must {
    "return false if id doesn't exist" in {
      val todo =
        Todo(randomUUID().toString, "title", "descr")
      val todos = ListBuffer(todo)
      val repo = new InMemoryTodoRepository(todos)

      repo
        .update(todo.copy(id = randomUUID().toString))
        .futureValue mustBe false
      todos mustBe ListBuffer(todo)
    }

    "update the todo if the todo exist" in {
      val todo =
        Todo(randomUUID().toString, "title", "descr")
      val todos =
        ListBuffer(todo)
      val repo = new InMemoryTodoRepository(todos)

      repo
        .update(Todo(todo.id, "new title", "new descr"))
        .futureValue mustBe true

      todos mustBe ListBuffer(Todo(todo.id, "new title", "new descr"))
    }
  }

  "get" must {
    "return the todo if the id exist" in {
      val todo =
        Todo(randomUUID().toString, "title", "descr")
      val todos =
        ListBuffer(todo)
      val repo = new InMemoryTodoRepository(todos)

      repo.get(todo.id).futureValue mustBe Some(todo)
    }

    "return None if the todo with id doesn't exist" in {
      val todo =
        Todo(randomUUID().toString, "title", "descr")
      val todos =
        ListBuffer(todo)
      val repo = new InMemoryTodoRepository(todos)

      repo.get(randomUUID().toString).futureValue mustBe None
    }
  }

  "delete" must {
    "return false if id doesn't exist" in {
      val todo =
        Todo(randomUUID().toString, "title", "descr")
      val todos = ListBuffer(todo)
      val repo = new InMemoryTodoRepository(todos)

      repo
        .delete(randomUUID().toString)
        .futureValue mustBe false
      todos mustBe ListBuffer(todo)
    }

    "return true if id exist and remove the todo from the list" in {
      val todo =
        Todo(randomUUID().toString, "title", "descr")
      val todos =
        ListBuffer(todo)
      val repo = new InMemoryTodoRepository(todos)

      repo.delete(todo.id).futureValue mustBe true

      todos mustBe empty
    }

  }

}
