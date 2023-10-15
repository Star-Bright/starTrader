package com.starbright.trade.ibclient.contract

import com.ib.client.Types.{BarSize, SecType}
import org.joda.time.DateTimeZone

import java.time.LocalDate

case class ContractOptions(
  contract: String = "",
  symbol: Option[String] = None,
  securityType: String = SecType.valueOf("STK").getApiString,
  exchange: String = "SMART",
  currency: String = "USD",
  expiry: String = "",
  barSize: String = BarSize._1_min.toString,
  startDate: LocalDate,
  endDate: LocalDate
)
