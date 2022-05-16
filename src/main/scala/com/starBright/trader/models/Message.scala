package com.starBright.trader.models

import java.time.LocalDateTime

case class Message(timestamp: LocalDateTime, intervalPnl: Double, tradePnl: Double, message: String)
