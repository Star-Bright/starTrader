package com.starBright.trader.models

case class LimitOrder(
  positionType: PositionType,
  level: Double,
  amount: Double,
  stopLossOrder: Option[LimitOrder] = None
)
