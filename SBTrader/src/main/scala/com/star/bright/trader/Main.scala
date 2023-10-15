package com.star.bright.trader

import com.star.bright.trader.reader.FileReader
import org.apache.spark.sql.SparkSession
import org.slf4j.{Logger, LoggerFactory}

object Main {
  private val log: Logger = LoggerFactory.getLogger(this.getClass)

  val spark: SparkSession = SparkSession.builder
    .master("local[4]")
    .appName("Simple Application")
    .getOrCreate()

  def main(args: Array[String]): Unit = {
    log.info("Starting Star Trader...")
    val path = getClass.getResource("/marketdata/").getPath
    val pricesDF = FileReader(spark, path)
    pricesDF.show()
    pricesDF.createOrReplaceTempView("spy")
  }

}
