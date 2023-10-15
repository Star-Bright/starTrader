import Versions._
import sbt._

object Versions {
  lazy val pureConfig = "0.17.1"
  lazy val decline = "2.2.0"
  lazy val sparkSql = "3.2.1"
  lazy val rx = "0.27.0"
  lazy val scalaTest = "3.2.10"
  lazy val scalacheck = "1.16.0"
  lazy val jodaTime = "2.9.1"
  lazy val jodaConvert = "1.2"
  lazy val catsCore = "2.8.0"
  lazy val scalarx = "0.4.3"
}

object Dependencies {
  val sparkDependencies: Seq[ModuleID] = Seq("org.apache.spark" %% "spark-sql" % sparkSql)

  val miscDependencies: Seq[ModuleID] = Seq(
    "joda-time" % "joda-time" % jodaTime,
    "org.joda" % "joda-convert" % jodaConvert,
    "com.github.pureconfig" %% "pureconfig" % pureConfig,
    "com.monovore" %% "decline" % decline,
    "org.typelevel" %% "cats-core" % catsCore,
    "com.lihaoyi" %% "scalarx" % scalarx,
    "io.reactivex" %% "rxscala" % rx
  )

  val testDependencies: Seq[ModuleID] =
    Seq("org.scalatest" %% "scalatest" % scalaTest, "org.scalacheck" %% "scalacheck" % scalacheck)
      .map(_ % "it,test")

  val starBrightTrader: Seq[ModuleID] = sparkDependencies ++ miscDependencies ++ testDependencies
}
