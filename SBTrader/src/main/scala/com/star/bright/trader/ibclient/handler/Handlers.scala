package com.starbright.trade.ibclient.handler

import com.ib.client.ContractDetails
import com.starbright.trade.ibclient.account.{AccountUpdate, AccountUpdateSubscription}
import com.starbright.trade.models._
import rx.lang.scala.Subject

import scala.collection.mutable

case class AccountUpdateHandler(accountUpdateSubscription: AccountUpdateSubscription, subject: Subject[AccountUpdate])
    extends ErrorHandler {
  val nextAccountUpdate = new AccountUpdate()

  override def error(throwable: Throwable): Unit =
    subject.onError(throwable)

}

case class MarketDataHandler(subscription: MarketDataSubscription, subject: Subject[Tick]) extends ErrorHandler {

  override def error(throwable: Throwable): Unit =
    subject.onError(throwable)

}

case class ContractDetailsHandler(
  details: mutable.ArrayBuffer[ContractDetails] = mutable.ArrayBuffer.empty[ContractDetails]
) extends ErrorHandler

case class HistoricalDataHandler(queue: mutable.Queue[Bar] = mutable.Queue.empty[Bar]) extends ErrorHandler

case class OpenOrdersHandler(openOrders: mutable.Map[Int, OpenOrder] = mutable.Map.empty[Int, OpenOrder])
    extends ErrorHandler

case class OrderStatusHandler(subject: Subject[OrderStatus]) extends ErrorHandler
case class PositionHandler(queue: mutable.Queue[Position] = mutable.Queue.empty[Position]) extends ErrorHandler

case class RealtimeBarsHandler(subscription: RealtimeBarsSubscription, subject: Subject[Bar]) extends ErrorHandler {

  override def error(throwable: Throwable): Unit =
    subject.onError(throwable)

}
