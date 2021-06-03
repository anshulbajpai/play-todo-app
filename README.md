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

