package com.starBright.trader.models

import java.time.LocalDateTime

case class Candle(timestamp: LocalDateTime, open: Double, high: Double, low: Double, last: Double) {
  lazy val isBullish: Boolean = last > low + (high - low) / 2
}
