package com.starBright.trader.backtests

import java.text.DecimalFormat
import java.time.format.DateTimeFormatter
import akka.actor.{Actor, ActorLogging, Props, ReceiveTimeout}
import com.starBright.trader.backtests.CvsPriceReader.{PriceTicks, ReadTicksFromCsv}
import com.starBright.trader.models.Message
import com.starBright.trader.strategies.StrategyFSM

import scala.concurrent.duration._
import scala.language.postfixOps

class BackTest extends Actor with ActorLogging {

  import context._

  val formatter = new DecimalFormat("#.######")

  override def preStart() {
    val csvReader = actorOf(Props[CvsPriceReader], "csvReader")
    csvReader ! ReadTicksFromCsv("src/main/resources/eurusd_daily_2004_2014.csv")
  }

  context.setReceiveTimeout(1 second)

  def receive: Receive = {
    case PriceTicks(priceTicks) =>
      val strategyFsm = actorOf(Props[StrategyFSM], "strategyFsm")
      for (tick <- priceTicks)
        strategyFsm ! tick
    case Message(d, intervalPnl, tradePnl, logMsg) =>
      println(s"${DateTimeFormatter.ofPattern("dd.MM.uuuu\tHH:mm:ss").format(d)}\t${formatter
          .format(intervalPnl)}\t${formatter.format(tradePnl)}\t$logMsg")
    case ReceiveTimeout =>
      stop(self)
  }

}
