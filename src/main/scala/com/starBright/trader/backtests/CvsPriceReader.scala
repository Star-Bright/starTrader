package com.starBright.trader.backtests

import akka.actor.Actor
import com.starBright.trader.backtests.CvsPriceReader.{PriceTicks, ReadTicksFromCsv}
import com.starBright.trader.models.Candle

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.io.Source

object CvsPriceReader {

  case class ReadTicksFromCsv(fileName: String)

  case class PriceTicks(priceTicks: List[Candle])

}

class CvsPriceReader extends Actor {

  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm:ss")

  def receive: Receive = { case ReadTicksFromCsv(fileName: String) =>
    val source = Source
      .fromFile(fileName)
      .getLines()
    val ticks = source.map { line =>
      val tokens = line.split(";")
      if (tokens.length == 6)
        Candle(
          LocalDateTime.parse(tokens(0) + " " + tokens(1), dateTimeFormatter),
          tokens(2).toDouble,
          tokens(3).toDouble,
          tokens(4).toDouble,
          tokens(5).toDouble
        )
      else
        Candle(
          LocalDateTime.parse(tokens(0) + " 00:00:00", dateTimeFormatter),
          tokens(1).toDouble,
          tokens(2).toDouble,
          tokens(3).toDouble,
          tokens(4).toDouble
        )
    }.toList
    sender() ! PriceTicks(ticks)
  }

}
