import sbt._

object Versions {

  // Core
  lazy val pureConfig = "0.17.1"
  lazy val akka = "2.6.18"

  // Test
  lazy val scalaTest = "3.2.11"
  lazy val scalaCheck = "1.16.0"

}

object Dependencies {

  val akkaDependencies: Seq[ModuleID] =
    Seq(
      "com.typesafe.akka" %% "akka-actor" % Versions.akka,
      "com.typesafe.akka" %% "akka-stream" % Versions.akka
    )

  val coreDependencies: Seq[ModuleID] = Seq("com.github.pureconfig" %% "pureconfig" % Versions.pureConfig)

  val testDependencies: Seq[ModuleID] =
    Seq(
      "org.scalatest" %% "scalatest" % Versions.scalaTest,
      "org.scalacheck" %% "scalacheck" % Versions.scalaCheck,
      "com.typesafe.akka" %% "akka-testkit" % Versions.akka
    )
      .map(_ % Test)

}
