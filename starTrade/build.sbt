import sbt.Keys._

lazy val trader = (project in file("."))
  .settings(
    name := "starTrade",
    version := "0.1",
    scalaVersion := "2.13.7",
    unmanagedBase := baseDirectory.value / "lib"
  )
  .settings(
    resolvers ++= Seq(
      "akka-actor".at("https://repo1.maven.org/maven2/"),
      "akka-stream".at("https://mvnrepository.com/artifact/com.typesafe.akka/akka-stream")
    )
  )
  .settings(libraryDependencies ++= Dependencies.akkaDependencies)
  .settings(libraryDependencies ++= Dependencies.coreDependencies)
