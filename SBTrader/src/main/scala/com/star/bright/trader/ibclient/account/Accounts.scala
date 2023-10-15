package com.starbright.trade.ibclient.account

import com.ib.client.Contract
import com.starbright.trade.ibclient.IBClient
import rx.lang.scala.Observable

import scala.collection.mutable

case class AccountUpdate(
  positions: mutable.ArrayBuffer[Position] = mutable.ArrayBuffer.empty[Position],
  accountInfo: mutable.Map[String, Value] = mutable.Map.empty[String, Value],
  netLiquidation: Value = Value(-1, "USD")
)

case class AccountUpdateSubscription(iBClient: IBClient, observableAccountUpdate: Observable[AccountUpdate]) {

  def close(): Unit =
    iBClient.closeAccountUpdateSubscription()

}

case class Position(
  contract: Contract,
  position: Int,
  marketPrice: Double,
  marketValue: Double,
  averageCost: Double,
  unrealizedPNL: Double,
  realizedPNL: Double,
  accountName: String
)

case class Value(value: Double, currency: String)
