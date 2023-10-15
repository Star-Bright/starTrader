package com.starbright.trade.ibclient.contract

import com.ib.client.Contract
import com.ib.client.Types.SecType

case class CashContract(
  override val symbol: String,
  override val localSymbol: String,
  override val exchange: String = "IDEALPRO",
  override val currency: String = "USD"
) extends Contract {
  symbol(symbol)
  localSymbol(localSymbol)
  secType(SecType.CASH.name())
  exchange(exchange)
  currency(currency)
}

case class FutureContract(
  override val symbol: String,
  override val expiry: String,
  override val exchange: String = "GLOBEX",
  override val currency: String = "USD"
) extends Contract {
  symbol(symbol)
  expiry(expiry)
  secType(SecType.FUT.name())
  exchange(exchange)
  currency(currency)
}

case class GenericContract(
  sectTypeEnum: String,
  override val symbol: String,
  override val exchange: String = "IDEALPRO",
  override val currency: String = "USD"
) extends Contract {
  symbol(symbol)
  secType(sectTypeEnum)
  exchange(exchange)
  currency(currency)
}

case class StockContract(
  override val symbol: String,
  override val exchange: String = "SMART",
  override val currency: String = "USD"
) extends Contract {
  symbol(symbol)
  secType(SecType.STK.name())
  exchange(exchange)
  currency(currency)
}
