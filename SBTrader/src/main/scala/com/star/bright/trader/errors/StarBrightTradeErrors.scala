package com.starbright.trade.errors

case class IBApiError(code: Int, msg: String, reqId: Int) extends Exception(msg) {
  def this(msg: String) = this(-1, msg, -1)
}
case class IBClientError(msg: String) extends Exception(msg)
