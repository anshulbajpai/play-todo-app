package todo

import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.http.HeaderNames
import play.api.libs.json.Json
import play.api.test.Helpers.{status, _}
import play.api.test.{FakeRequest, Helpers}

import java.util.UUID.randomUUID
import scala.collection.mutable

class TodoControllerSpecs extends WordSpec with MustMatchers with OptionValues {

  "list" must {
    "return empty items when there are no todos" in {
      val controller = new TodoController(
        Helpers.stubControllerComponents(),
        mutable.ListBuffer.empty
      )
      val response = controller.list(FakeRequest())
      status(response) mustBe OK
      contentAsString(response) mustBe "[]"
    }

    "return items when there are todos" in {
      val todo1 =
        Todo(randomUUID().toString, "my first todo", "Do the first todo")
      val todo2 =
        Todo(randomUUID().toString, "my second todo", "Do the second todo")
      val todo3 =
        Todo(randomUUID().toString, "my third todo", "Do the third todo")

      val todos = mutable.ListBuffer(todo1, todo2, todo3)
      val controller =
        new TodoController(Helpers.stubControllerComponents(), todos)

      val response = controller.list(FakeRequest())

      status(response) mustBe OK
      contentAsJson(response) mustBe
        Json.parse(s"""
          |[
          | {"id": "${todo1.id}", "title": "my first todo", "description": "Do the first todo"},
          | {"id": "${todo2.id}", "title": "my second todo", "description": "Do the second todo"},
          | {"id": "${todo3.id}", "title": "my third todo", "description": "Do the third todo"}
          |]
          |""".stripMargin)
    }
  }

  "create" must {
    "create a new todo" in {
      val todos = mutable.ListBuffer.empty[Todo]
      val controller =
        new TodoController(Helpers.stubControllerComponents(), todos)
      val newTodoJson = Json.obj(
        "title" -> "my new todo",
        "description" -> "I am must do my new todo"
      )
      val createResponse =
        controller.create(FakeRequest().withBody(newTodoJson))
      status(createResponse) mustBe CREATED
      val location = header(HeaderNames.LOCATION, createResponse).value
      val todoIdRegex = "^/api/todos/(.*)$".r
      val todoIdRegex(todoId) = location
      val actualTodo = todos.find(_.id == todoId).value
      actualTodo.title mustBe "my new todo"
      actualTodo.description mustBe "I am must do my new todo"
    }
  }

  "get" must {
    "return a not found if a todo matching id is not found" in {
      val controller = new TodoController(
        Helpers.stubControllerComponents(),
        mutable.ListBuffer.empty
      )
      val response = controller.get(randomUUID().toString)(FakeRequest())
      status(response) mustBe NOT_FOUND
    }

    "return a todo matching id" in {
      val firstTodoId = randomUUID().toString
      val todos = mutable.ListBuffer(
        Todo(firstTodoId, "my first todo", "Do the first todo"),
        Todo(randomUUID().toString, "my second todo", "Do the second todo"),
        Todo(randomUUID().toString, "my third todo", "Do the third todo")
      )
      val controller =
        new TodoController(Helpers.stubControllerComponents(), todos)
      val response = controller.get(firstTodoId)(FakeRequest())

      status(response) mustBe OK
      contentAsJson(response) mustBe
        Json.parse(s"""
                     | {"id": "${firstTodoId}", "title": "my first todo", "description": "Do the first todo"}
                     |""".stripMargin)

    }
  }

  "update" must {
    "return 404 if id not found" in {
      val controller =
        new TodoController(
          Helpers.stubControllerComponents(),
          mutable.ListBuffer.empty
        )

      val updateJson = Json.obj(
        "title" -> "my new todo",
        "description" -> "I am must do my new todo"
      )

      val response = controller.update(randomUUID().toString)(
        FakeRequest().withBody(updateJson)
      )
      status(response) mustBe NOT_FOUND
    }

    "update an existing todo" in {
      val todoId = randomUUID().toString
      val todos =
        mutable.ListBuffer(Todo(todoId, "my second todo", "Do the second todo"))
      val controller =
        new TodoController(Helpers.stubControllerComponents(), todos)

      val updateJson = Json.obj(
        "title" -> "my new todo",
        "description" -> "I am must do my new todo"
      )

      val response =
        controller.update(todoId)(FakeRequest().withBody(updateJson))

      status(response) mustBe NO_CONTENT

      val todo = todos.find(_.id == todoId).value
      todo.title mustBe "my new todo"
      todo.description mustBe "I am must do my new todo"
    }
  }

  "delete" must {
    "return not found if id not found" in {
      val controller =
        new TodoController(
          Helpers.stubControllerComponents(),
          mutable.ListBuffer.empty
        )

      val response = controller.delete(randomUUID().toString)(FakeRequest())
      status(response) mustBe NOT_FOUND
    }

    "remove the todo if id found and return no content" in {
      val todoId = randomUUID().toString
      val todos =
        mutable.ListBuffer(Todo(todoId, "my second todo", "Do the second todo"))
      val controller =
        new TodoController(Helpers.stubControllerComponents(), todos)
      val response = controller.delete(todoId)(FakeRequest())
      status(response) mustBe NO_CONTENT
      todos mustBe empty
    }
  }
}
