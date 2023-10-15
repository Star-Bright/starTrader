package com.starbright.trade.models

import com.ib.client.Contract

case class Position(account: String, contract: Contract, position: Int, avgCost: Double)
