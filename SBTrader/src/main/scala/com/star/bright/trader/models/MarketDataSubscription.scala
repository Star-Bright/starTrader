package com.starbright.trade.models

import com.ib.client.Contract
import com.starbright.trade.ibclient.IBClient
import rx.lang.scala.Observable

/**
 * Market data line returned by call to [[IBClient]] method marketData. This class is not intended to be instantiated by
 * the user of [[IBClient]]
 */
case class MarketDataSubscription(ibClient: IBClient, id: Int, contract: Contract, observableTick: Observable[Tick]) {

  /**
   * Close this market data line and free all associated resources. There's no need to call any [[IBClient]] additional
   * methods
   */
  def close(): Unit =
    ibClient.closeMarketData(id)

}
