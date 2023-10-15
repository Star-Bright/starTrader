package com.starbright.trade.ibclient.contract

import com.ib.client.Contract

object Contract {

  def apply(options: ContractOptions): Option[Contract] = options.securityType match {
    case opt =>
      if (opt.contains("STK"))
        Option(StockContract(options.contract, options.exchange, options.currency))
      else None
    case opt =>
      if (opt.contains("FUT"))
        Option(FutureContract(options.contract, options.expiry, options.exchange, options.currency))
      else None
    case opt =>
      if (opt.contains("CASH"))
        Option(CashContract(options.contract, options.symbol.get, options.exchange, options.currency))
      else None
    case other @ _ =>
      Option(GenericContract(other, options.contract, options.exchange, options.currency))
  }

}
