package com.starbright.trade.ibclient.order

sealed trait OrderType
case class LimitOrder(limit: Double) extends OrderType
case class MarketOrder() extends OrderType
case class StopOrder(stop: Double) extends OrderType
case class StopLimitOrder(stop: Double, limit: Double) extends OrderType
case class TrailLimitIfTouchedOrder(stop: Double, limit: Double, trail: Double) extends OrderType
case class TrailMarketIfTouchedOrder(stop: Double, trail: Double) extends OrderType
case class TrailStopOrder(stop: Double = 0D) extends OrderType
case class TrailStopLimitOrder(stop: Double, trail: Double) extends OrderType

