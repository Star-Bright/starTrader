package com.starbright.trade.ibclient.order

import com.ib.client.{Order => IBOrder}

sealed trait Order {
  def orderType: OrderType = MarketOrder()
  def quantity: Int = 1

  def toIBOrder: IBOrder = {
    val iBOrder = new IBOrder()
    this match {
      case Buy(_, qty) ⇒
        iBOrder.action("BUY")
        iBOrder.totalQuantity(qty)
      case Sell(_, qty) ⇒
        iBOrder.action("SELL")
        iBOrder.totalQuantity(qty)
    }
    this.orderType match {
      case LimitOrder(limit) ⇒
        iBOrder.orderType("LMT")
        iBOrder.lmtPrice(limit)
        iBOrder.auxPrice(0)

      case MarketOrder() ⇒
        iBOrder.orderType("MKT")
      case StopOrder(stop) ⇒
        iBOrder.orderType("STP")
        iBOrder.lmtPrice(0)
        iBOrder.auxPrice(stop)
      case StopLimitOrder(stop, limit) ⇒
        iBOrder.orderType("STPLMT")
        iBOrder.lmtPrice(limit)
        iBOrder.auxPrice(stop)
      case TrailStopOrder(stop) ⇒
        iBOrder.orderType("TRAIL")
        iBOrder.lmtPrice(stop)
        iBOrder.auxPrice(stop)
      case TrailStopLimitOrder(stop, trail) ⇒
        iBOrder.orderType("TRAIL LIMIT")
        iBOrder.lmtPrice(0)
        iBOrder.auxPrice(stop)
        iBOrder.trailStopPrice(trail)
      case TrailLimitIfTouchedOrder(stop, limit, trail) ⇒
        iBOrder.orderType("TRAIL LIT")
        iBOrder.trailStopPrice(trail)
        iBOrder.lmtPrice(limit)
        iBOrder.auxPrice(stop)
      case TrailMarketIfTouchedOrder(stop, trail) ⇒
        iBOrder.orderType("TRAIL MIT")
        iBOrder.trailStopPrice(trail)
        iBOrder.auxPrice(stop)
        iBOrder.lmtPrice(0)
    }
    iBOrder
  }
}

case class Buy(override val orderType: OrderType, override val quantity: Int) extends Order
case class Sell(override val orderType: OrderType, override val quantity: Int) extends Order

