package com.starbright.trade.ibclient.order

object ExecutionStatus extends Enumeration {
  type ExecutionStatus = Value
  val
    PENDING_SUBMIT,
    PENDING_CANCEL,
    PRE_SUBMITTED,
    SUBMITTED,
    CANCELLED,
    FILLED,
    INACTIVE = Value
}
