package com.starbright.trade.models

import com.ib.client.{Contract, OrderState}
import com.ib.client.{Order ⇒ IBOrder, _}

case class OpenOrder(orderId: Int, contract: Contract, order: IBOrder, orderState: OrderState)
