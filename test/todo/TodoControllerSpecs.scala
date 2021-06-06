package todo

import org.mockito.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.HeaderNames
import play.api.libs.json.Json
import play.api.test.Helpers.{status, _}
import play.api.test.{FakeRequest, Helpers, Injecting}

import java.util.UUID.randomUUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TodoControllerSpecs
    extends WordSpec
    with MustMatchers
    with OptionValues
    with GuiceOneAppPerSuite
    with Injecting
    with MockitoSugar
    with ScalaFutures {

  "list" must {
    "return empty items when there are no todos" in new Setup {
      when(mockRepo.list()).thenReturn(Future.successful(List.empty))

      val response = controller.list(FakeRequest())

      status(response) mustBe OK
      contentAsString(response) mustBe "[]"
    }

    "return items when there are todos" in new Setup {
      val todo1 =
        Todo(randomUUID().toString, "my first todo", "Do the first todo")
      val todo2 =
        Todo(randomUUID().toString, "my second todo", "Do the second todo")
      val todo3 =
        Todo(randomUUID().toString, "my third todo", "Do the third todo")

      val todos = List(todo1, todo2, todo3)

      when(mockRepo.list()).thenReturn(Future.successful(todos))

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
    "create a new todo" in new Setup {
      val newTodoJson = Json.obj(
        "title" -> "my new todo",
        "description" -> "I am must do my new todo"
      )
      val todoId = randomUUID().toString
      when(mockRepo.create("my new todo", "I am must do my new todo"))
        .thenReturn(Future.successful(todoId))

      val createResponse =
        controller.create(FakeRequest().withBody(newTodoJson))

      status(createResponse) mustBe CREATED
      val location = header(HeaderNames.LOCATION, createResponse).value
      val todoIdRegex = "^/api/todos/(.*)$".r
      val todoIdRegex(actualTodoId) = location
      actualTodoId mustBe todoId
    }
  }

  "get" must {
    "return a not found if a todo matching id is not found" in new Setup {
      val id = randomUUID().toString

      when(mockRepo.get(id)).thenReturn(Future.successful(None))

      val response = controller.get(id)(FakeRequest())
      status(response) mustBe NOT_FOUND
    }

    "return a todo matching id" in new Setup {
      val todo =
        Todo(randomUUID().toString, "my first todo", "Do the first todo")
      when(mockRepo.get(todo.id)).thenReturn(Future.successful(Some(todo)))

      val response = controller.get(todo.id)(FakeRequest())

      status(response) mustBe OK
      contentAsJson(response) mustBe
        Json.parse(s"""
                     | {"id": "${todo.id}", "title": "my first todo", "description": "Do the first todo"}
                     |""".stripMargin)

    }
  }

  "update" must {
    "return 404 if id not found" in new Setup {
      val updateJson = Json.obj(
        "title" -> "my new todo",
        "description" -> "I am must do my new todo"
      )

      val id = randomUUID().toString
      when(
        mockRepo
          .update(Todo(id, "my new todo", "I am must do my new todo"))
      ).thenReturn(Future.successful(false))

      val response = controller.update(id)(FakeRequest().withBody(updateJson))

      status(response) mustBe NOT_FOUND
    }

    "update an existing todo" in new Setup {
      val todoId = randomUUID().toString
      when(
        mockRepo
          .update(Todo(todoId, "my new todo", "I am must do my new todo"))
      ).thenReturn(Future.successful(true))

      val updateJson = Json.obj(
        "title" -> "my new todo",
        "description" -> "I am must do my new todo"
      )

      val response =
        controller.update(todoId)(FakeRequest().withBody(updateJson))

      status(response) mustBe NO_CONTENT
    }
  }

  "delete" must {
    "return not found if id not found" in new Setup {
      val id: String = randomUUID().toString
      when(mockRepo.delete(id)).thenReturn(Future.successful(false))

      val response = controller.delete(id)(FakeRequest())

      status(response) mustBe NOT_FOUND
    }

    "remove the todo if id found and return no content" in new Setup {
      val todoId = randomUUID().toString
      when(mockRepo.delete(todoId)).thenReturn(Future.successful(true))

      val response = controller.delete(todoId)(FakeRequest())

      status(response) mustBe NO_CONTENT
    }
  }

  trait Setup {
    val mockRepo = mock[TodoRepository]
    val controller =
      new TodoController(Helpers.stubControllerComponents(), mockRepo)
  }
}
