package com.star.bright.trader.strategies

import org.apache.spark.sql.functions.{avg, desc, month, year}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

class Statistical(spark: SparkSession) {
  import spark.implicits._

  def findAverage(df: DataFrame): Dataset[Row] =
    df.select(
      year($"date")
        .alias("yr"),
      $"adjClose"
    ).groupBy("yr")
      .avg("adjClose")
      .orderBy(desc("yr"))

  def df(df: DataFrame): Dataset[Row] =
    df.select(year($"date").alias("yr"), month($"date").alias("mo"), $"adjClose")
      .groupBy("yr", "mo")
      .agg(avg("adjClose"))
      .orderBy(desc("yr"), desc("mo"))

  val sql =
    "SELECT spy.date, spy.open, spy.close, abs(spy.close - spy.open) as spydif FROM spy WHERE abs(spy.close - spy.open) > 4 "

  def res(sql: String): DataFrame = spark.sqlContext.sql(sql)

}
