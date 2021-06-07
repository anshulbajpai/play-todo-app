name := """play-todo-app"""
organization := "com.github.anshulbajpai"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.14"

libraryDependencies += guice
libraryDependencies += "uk.gov.hmrc" %% "simple-reactivemongo" % "8.0.0-play-28"
libraryDependencies += "uk.gov.hmrc" %% "bootstrap-frontend-play-28" % "5.3.0"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.mockito" %% "mockito-scala" % "1.16.37" % Test

resolvers += MavenRepository(
  "HMRC-open-artefacts-maven2",
  "https://open.artefacts.tax.service.gov.uk/maven2"
)

libraryDependencies += ws
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.github.anshulbajpai.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.github.anshulbajpai.binders._"
