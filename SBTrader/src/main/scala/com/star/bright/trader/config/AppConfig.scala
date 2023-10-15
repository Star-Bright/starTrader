package com.starbright.trade.config

case class TWS(host: String, port: Int, clientId: Int, timeout: Int)
case class Stock(contract: String, exchange: String, currency: String)
case class Future(contract: String, exchange: String, currency: String)
case class Asset(stock: Stock, future: Future)
case class HistoryRequestPacingViolationRetry(length: Int, unit: String, count: Int)

case class BarSize(
  SECOND_5_secs: Int,
  SECOND_10_secs: Int,
  SECOND_15_secs: Int,
  SECOND_30_secs: Int,
  SECOND_1_minute: Int,
  SECOND_2_minutes: Int,
  SECOND_3_minutes: Int,
  SECOND_5_minutes: Int,
  SECOND_10_minutes: Int,
  SECOND_15_minutes: Int,
  SECOND_30_minutes: Int,
  SECOND_1_hour: Int,
  DAY_30_secs: Int,
  DAY_1_min: Int,
  DAY_5_minutes: Int,
  DAY_15_minutes: Int,
  DAY_1_hour: Int,
  DAY_4_hour: Int,
  DAY_1_day: Int,
  WEEK_15_minutes: Int,
  WEEK_1_hour: Int,
  WEEK_4_hours: Int,
  WEEK_1_day: Int,
  WEEK_1_week: Int,
  MONTH_1_hour: Int,
  MONTH_4_hours: Int,
  MONTH_1_day: Int,
  MONTH_1_week: Int,
  YEAR_1_day: Int,
  YEAR_1_week: Int
)

case class AppConfig(
  tws: TWS,
  asset: Asset,
  historyRequestTimeout: String,
  historyRequestPacingViolationRetry: HistoryRequestPacingViolationRetry,
  barSize: BarSize
)
