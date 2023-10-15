package com.star.bright.trader.historical

import com.star.bright.trader.strategies.ml.OBV
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object Tranformer {

  def transform(dataFrame: DataFrame, spark: SparkSession) = {
    import spark.sqlContext.implicits._

    val initialWindowedSpec = Window.partitionBy("Date").orderBy("Date")
    val data1 = dataFrame.withColumn("YC", lag(dataFrame("Close"), 1).over(initialWindowedSpec))
    data1.show()
    val data2 = data1.withColumn("TDAC", lag(data1("Close"), 10).over(initialWindowedSpec))

    val wSpec2 = Window.orderBy("Date").rowsBetween(-4, 0)
    val wSpec3 = Window.orderBy("Date").rowsBetween(-9, 0)
    val data3 = data2.withColumn("Lowest Low", min(data2("Low")).over(wSpec2))
    val data4 = data3.withColumn("Highest High", max(data3("High")).over(wSpec2))
    val data5 = data4.withColumn("MA", avg(data4("Close")).over(wSpec3))

    val filteredRdd1 = data5.rdd.zipWithIndex().collect { case (r, i) if i > 9 => r }

    val newDf1 = spark.sqlContext.createDataFrame(filteredRdd1, data5.schema)
    val filteredRdd = newDf1.rdd.zipWithIndex().collect { case (r, i) if i < 400 => r }
    val newDf = spark.sqlContext.createDataFrame(filteredRdd, data5.schema)

    val data6 = newDf
      .withColumn("Volume1", newDf("Volume").cast(org.apache.spark.sql.types.LongType))
      .drop("Volume")
      .withColumnRenamed("Volume1", "Volume")
    val data7 = data6.withColumn("Close-YC", newDf("Close") - newDf("YC"))

    val data8 = data7
      .withColumn("TC-YC bool", when($"Close-YC" > 0, 1).otherwise(0))
      .drop("Symbol")
      .drop("Open")
      .drop("Adj_Close")

    val data9 = data8.withColumn("upcl", when($"TC-YC bool" < 1, 0).otherwise($"Close"))
    val data10 = data9.withColumn("dwcl", when($"TC-YC bool" > 0, 0).otherwise($"Close"))
    val wSpec4 = Window.orderBy("Date").rowsBetween(-8, 0)
    val data11 = data10.withColumn("upclose", sum(data10("upcl")).over(wSpec4))
    val data12 = data11.withColumn("downclose", sum(data10("dwcl")).over(wSpec4))
    val data13 = data12.drop("Lowest Low").drop("Highest High")
    val filteredRdd2 = data13.rdd.zipWithIndex().collect { case (r, i) if i > 0 => r }

    val data14 = spark.sqlContext.createDataFrame(filteredRdd2, data13.schema)
    val data15 = data14.withColumn("RSIration", data14("upclose") / data14("downclose"))

    val data16 = data15.withColumn("1+", data15("YC") - data15("YC") + 1)
    val data17 = data16.drop("High").drop("Low").drop("Close").drop("TDAC").drop("MA")
    val data18 = data17.withColumn("10+", data17("1+") / (data17("RSIration") + data17("1+")))
    val data19 = data18.withColumn("100", data15("YC") - data15("YC") + 100)
    val data20 = data19.withColumn("RSI", data19("100") - (data19("100") * data19("10+")))
    val data21 = data20.drop("RSIration").drop("1+").drop("10+").drop("100")
    val data22 = data21
      .withColumn("RSI_pred", when($"RSI" > 50, 1).otherwise(0))
      .drop("YC")
      .drop("Volume")
      .drop("Close-YC")
      .drop("TC-YC bool")
      .drop("upcl")
      .drop("dwcl")
      .drop("upclose")
      .drop("downclose")
      .drop("50")

    val data_PMO =
      data2.drop("Adj_Close").drop("High").drop("Low").drop("Open").drop("Symbol").drop("Volume").drop("YC")

    val filteredRdd4 = data_PMO.rdd.zipWithIndex().collect { case (r, i) if i > 10 => r }

    val data_PMO1 = spark.sqlContext.createDataFrame(filteredRdd4, data_PMO.schema)

    val filteredRdd5 = data_PMO1.rdd.zipWithIndex().collect { case (r, i) if i < 400 => r }
    val data_PMO2 = spark.sqlContext.createDataFrame(filteredRdd5, data_PMO1.schema)
    val data_PMO3 = data_PMO2.withColumn("TC- TDAC", data_PMO2("Close") - data_PMO2("TDAC"))
    val data_PMO4 = data_PMO3.withColumn("PMO_pred", when($"TC- TDAC" > 0, 1).otherwise(0))

    val data_k = newDf.withColumn("HN-LN", newDf("Highest High") - newDf("Lowest Low"))
    val data_k1 = data_k
      .withColumn("TC- LN", data_k("Close") - data_k("Lowest Low"))
      .drop("Adj_Close")
      .drop("High")
      .drop("Low")
      .drop("Open")
      .drop("Symbol")
      .drop("Volume")
      .drop("YC")
      .drop("TDAC")
      .drop("MA")
    val data_k2 = data_k1.withColumn("ratio", data_k1("TC- LN") / data_k1("HN-LN"))
    val data_k3 = data_k2.withColumn("100", data_k2("Close") - data_k2("Close") + 100)
    val data_k4 = data_k3.withColumn("K", data_k3("ratio") * data_k3("100"))
    val data_k5 = data_k4
      .withColumn("K_pred", when($"K" > 80, 1).otherwise(0))
      .drop("Close")
      .drop("Lowest Low")
      .drop("Highest High")
      .drop("HN-LN")
      .drop("TC- LN")
      .drop("ratio")
      .drop("100")

    val OBV_ = new OBV()
    val winspec5 = Window.orderBy("Date")
    val data_OBV =
      data7.withColumn("OBV", OBV_(data7.col("Volume"), data7.col("Close-YC")).over(winspec5))
    val data_OBV1 = data_OBV
      .withColumn("lagOBV", lag(data_OBV("OBV").cast(org.apache.spark.sql.types.LongType), 1).over(initialWindowedSpec))
      .drop("Adj_Close")
      .drop("High")
      .drop("Low")
      .drop("Open")
      .drop("Symbol")

    val filteredRdd6 = data_OBV1.rdd.zipWithIndex().collect { case (r, i) if i > 0 => r }

    val data_OBV2 = spark.sqlContext.createDataFrame(filteredRdd6, data_OBV1.schema)
    val data_OBV3 = data_OBV2.withColumn("delOBV", data_OBV2("OBV") - data_OBV2("lagOBV"))
    val data_OBV4 = data_OBV3.withColumn("OBV_pred", when($"delOBV" > 0, 1).otherwise(0))

    val data_MA = data_OBV.withColumn(
      "lagOBV",
      lag(data_OBV("OBV").cast(org.apache.spark.sql.types.LongType), 1).over(initialWindowedSpec)
    )
    val data_MA1 = data_MA
      .drop("Adj_Close")
      .drop("Close")
      .drop("High")
      .drop("Open")
      .drop("Low")
      .drop("Symbol")
      .drop("YC")
      .drop("TDAC")
      .drop("Lowest Low")
      .drop("Highest High")
      .drop("Volume")
      .drop("Close-YC")
      .drop("OBV")
      .drop("lagOBV")
    val data_MA2 = data_MA1.withColumn("lagMA", lag(data_MA1("MA"), 1).over(initialWindowedSpec))

    val filteredRdd7: RDD[Row] = data_MA2.rdd.zipWithIndex().collect { case (r, i) if i > 0 => r }

    val data_MA3 = spark.sqlContext.createDataFrame(filteredRdd7, data_MA2.schema)
    val data_MA4 = data_MA3.withColumn("delMA", data_MA3("MA") - data_MA3("lagMA"))
    val data_MA5 =
      data_MA4.withColumn("MA_pred", when($"delMA" > 0, 1).otherwise(0)).drop("lagMA").drop("delMA")

    val training_table1 = data_OBV3.withColumn("OBV_pred", when($"delOBV" > 0, 1).otherwise(0))
    val training_table2 = training_table1
      .drop("TDAC")
      .drop("Lowest Low")
      .drop("Highest High")
      .drop("MA")
      .drop("lagOBV")
      .drop("delOBV")
      .drop("Close")

    val df = training_table2.join(data_PMO4, Seq("Date")).orderBy("Date")
    val df1 = df.join(data22, Seq("Date")).orderBy("Date")
    val df2 = df1.join(data_k5, Seq("Date")).orderBy("Date")
    val df3 = df2.join(data_MA5, Seq("Date")).orderBy("Date").drop("TC- TDAC").drop("YC")

    val output1: DataFrame =
      df3.withColumn("p", df3("OBV_pred") + df3("PMO_pred") + df3("RSI_pred") + df3("K_pred") + df3("MA_pred"))
    val output2: DataFrame = output1.withColumn("p_", when($"p" > 4, 1).otherwise(when($"p" < 2, -1).otherwise(0)))
    val output3: DataFrame = output2.withColumn("label", when($"Close-YC" > 0, 1).otherwise(-1))
    val output4: DataFrame = output3.withColumn("spike", output3("p_") * output3("label"))
    val output5: DataFrame = output4.drop("Close-YC").drop("Volume").drop("RSI").drop("K").drop("MA")
    val output6: DataFrame = output5.withColumn("correct", when($"spike" > 0, 1).otherwise(0))
    val output7: DataFrame = output6.withColumn("error", when($"spike" < 0, -1).otherwise(0))

    val trainData: DataFrame = data_MA.drop("High").drop("Low").drop("Symbol").drop("Close-YC").drop("lagOBV")
    trainData.show()

    val output8: DataFrame = output4
      .drop("Volume")
      .drop("Close-YC")
      .drop("OBV")
      .drop("MA")
      .drop("Close")
      .drop("TDAC")
      .drop("PMO_pred")
      .drop("K_pred")
      .drop("MA_pred")
      .drop("p")
      .drop("p_")
      .drop("label")
      .drop("spike")
      .drop("RSI_pred")
      .drop("OBV_pred")

    val train_data1 = trainData.join(output8, Seq("Date")).orderBy("Date")
    // val filteredRdd7 = train_data1.rdd.zipWithIndex().collect { case (r, i) if i > 5 => r }

    val trainData2 = spark.sqlContext.createDataFrame(filteredRdd7, train_data1.schema)

    output7
      .coalesce(1)
      .write
      .format("csv")
      // TODO define output path
      .save(System.getProperty("pred.dir") + "/" + "filename(0)" + "_priceIndex.csv")

    trainData2
      .coalesce(1)
      .write
      .format("csv")
      // TODO define train data path
      .save(System.getProperty("pred.dir") + "/" + "filename(0)" + "_trainingData.csv")
  }

}
