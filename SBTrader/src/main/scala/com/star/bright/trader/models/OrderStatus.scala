package com.starbright.trade.models

import com.starbright.trade.ibclient.order.ExecutionStatus.ExecutionStatus

case class OrderStatus(orderId: Int, status: ExecutionStatus, filled: Int, remaining: Int, avgFillPrice: Double,
                       permId: Int, parentId: Int, lastFilledPrice: Double, clientId: Int, whyHeld: String)
