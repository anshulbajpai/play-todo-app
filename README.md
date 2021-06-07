# play-todo-app

This is an example todo app built using Scala Play framework

Here we have built CRUD API for `todo` domain.

A series of commits helped to create this app. We will go through each commit below

## Pre-requisites
- JDK 8+
- SBT (1.4.7+)
- Mongo (4.0.22) (docker run -d -p 27017:27017 --name mongo mongo:4.0.22)

## Clone the project

### Checkout nth commit

Use the command below to checkout the nth commit as instructed in the following sections

```bash
 git checkout $(git rev-list master | tail -n <INSERT COMMIT INDEX> | head -n 1)
```

## Change the `HEAD` to point to the first commit - `Seed play project`

This commit was created by running `play-scala-seed` Giter8 template as shown below

```bash
sbt new playframework/play-scala-seed.g8
```

This template creates a Play app which has [`/` route](conf/routes) and a corresponding [`HomeController.index`](app/controllers/HomeController.scala) action method to handle that endpoint.
There is also a [`HomeControllerSpec`](test/controllers/HomeControllerSpec.scala) which shows 3 different way to test the `HomeController.index` method.

Make changes so that we are using
- Version 2.8.8 of `sbt-plugin` in `projects/plugins.sbt`
- Version `2.12.14` of `scala` in `build.sbt`
- Version `5.0.0` of `scalatestplus-play` in `build.sbt`
- Version `1.5.2` of `sbt` in `project/build.properties`

Execute `sbt test` to ensure all tests are passing
Execute `sbt run` to run the app. Browse `http://localhost:9000`

We will also add a `HomeIntegrationSpec` to demonstrate how to test the `/` route using a http client.

## Change the `HEAD` to point to the second commit - `Todo app with in memory persistence`

As part of this commit, we added a `TODO` API using in memory-persistence. This API will let us manage our `TODOs`

We have additional routes in the routes file to do CRUD operations on the `TODO` domain -

GET         /api/todos    
POST        /api/todos    
GET         /api/todos/:id
PUT         /api/todos/:id
DELETE      /api/todos/:id

We also have a new [`TodoController`](app/todo/TodoController.scala) and a corresponding [`Todo`](app/todo/Todo.scala) model.
There are [`TodoControllerSpecs`](test/todo/TodoControllerSpecs.scala) which test the CRUD behaviour.

## Change the `HEAD` to point to the third commit - `Todo with mongo persistence`

As part of this commit, we refactored the `TODO` API to use mongo persistence. For this we are using [hmrc/simple-reactivemongo](https://github.com/hmrc/simple-reactivemongo) library to interact with mongodb.

To use mongo persistence, we refactored the controller code to use `TodoRepository` abstraction which has two concrete implementations, i.e. `InMemoryTodoRepository` and `MongoTodoRepository`.
We also created new tests and updated the existing controller tests to use mocks.

## Change the `HEAD` to point to the fourth commit - `Todo frontend`

As part of this commit, we have created a HTML view for TODOs using Play's twirl templates. We created additional two endpoints to show TODOs which in turn consume the API endpoints we created in previous commits.
We have used Play's `WSClient` as http client.

```sbt
libraryDependencies += ws
```

## Change the `HEAD` to point to the fifth commit - `Use hmrc bootstrap`

As part of this commit, we made use of [HMRC bootstrap-play](https://github.com/hmrc/bootstrap-play) library which is an opinionated library and brings in features like session management, auditing, metrics reporting and an opinionated http client.
We enabled lots of module in application.conf to enable the above-mentioned features. We added an api connector which encapsulates interaction with the Todo API.

## Change the `HEAD` to point to the sixth commit - `Use hmrc frontend`

As part of this commit, we added a dependency on  [play-frontend-hmrc](https://github.com/hmrc/play-frontend-hmrc) which adds the capability of creating HTMLs based on gov.uk guidelines (GDS)
We created our [Layout](app/views/Layout.scala.html) based on GDS and then wrapped other pages with this layout. We also made use of i18n feature supported by Play. 