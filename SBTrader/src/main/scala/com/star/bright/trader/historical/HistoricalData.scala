package com.starbright.trade.historical

import com.ib.client.Contract
import com.ib.client.Types.WhatToShow
import com.starbright.trade.ibclient.IBClient
import com.starbright.trade.ibclient.contract.{Contract, ContractOptions}
import com.starbright.trade.models.Bar
import org.apache.spark.sql._
import org.slf4j.{Logger, LoggerFactory}

import java.io.File
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object HistoricalData {

  private[this] val log: Logger = LoggerFactory.getLogger(this.getClass)

  def apply(contractOptions: ContractOptions, ibClient: IBClient, sparkSession: SparkSession): Unit = {

    val contract: Contract = com.starbright.trade.ibclient.contract.Contract(contractOptions).get

    val futureBar: Future[IndexedSeq[Bar]] = ibClient.easyHistoricalData(
      contract,
      contractOptions.startDate,
      contractOptions.endDate,
      contractOptions.barSize.asInstanceOf[com.ib.client.Types.BarSize],
      WhatToShow.TRADES
    )

    val outFile = new File(fileName(contract, contractOptions))

    require(!outFile.exists(), s"Output file ${outFile.getAbsolutePath} already exists")

    val dataFrame = createDataset(sparkSession, futureBar)
    log.info(s"wrote ${dataFrame.count()} rows to $outFile")

    writeDataAsCSV(dataFrame, outFile.toString)

    ibClient.disconnect()
  }

  private[this] def fileName(contract: Contract, contractOptions: ContractOptions): String =
    if (contract.expiry() != null)
      s"${contract.symbol}${contract.expiry}-${contract.exchange}-${contractOptions.barSize}.csv"
    else
      s"${contract.symbol}-${contract.exchange}-${contractOptions.barSize}.csv"

  private[this] def createDataset(sparkSession: SparkSession, data: Future[IndexedSeq[Bar]]): DataFrame = {
    implicit def solutionEncoder: Encoder[Bar] =
      Encoders.kryo[Bar]
    sparkSession.createDataset(Await.result(data, Duration.Inf)).toDF()
  }

  private[this] def writeDataAsCSV(dataFrame: DataFrame, outFile: String): Unit = dataFrame.write
    .mode(SaveMode.Append)
    .partitionBy(List("time", "high", "low", "open", "close", "volume", "trades", "gaps"): _*)
    .csv(outFile)

}
