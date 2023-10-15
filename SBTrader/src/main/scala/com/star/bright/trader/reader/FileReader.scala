package com.star.bright.trader.reader

import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

case class MarketData(
  date: String,
  open: Double,
  high: Double,
  low: Double,
  close: Double,
  volume: Double,
  adjClose: Double
)

object FileReader {

  def apply(spark: SparkSession, filePath: String): DataFrame = {
    import spark.implicits._

    def parseDataset(rdd: Dataset[String]): Dataset[MarketData] = {
      val header = rdd.first
      rdd.filter(_(0) != header(0)).map(parseStock).cache()
    }
    parseDataset(spark.read.textFile(filePath)).toDF()
  }

  private[this] def parseStock(str: String): MarketData = {
    val line = str.split(",")
    MarketData(
      line(0),
      line(1).toDouble,
      line(2).toDouble,
      line(3).toDouble,
      line(4).toDouble,
      line(5).toDouble,
      line(6).toDouble
    )
  }

}
