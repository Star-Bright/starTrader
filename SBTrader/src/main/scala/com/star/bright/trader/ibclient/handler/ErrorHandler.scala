package com.starbright.trade.ibclient.handler

trait ErrorHandler {
  def error(throwable: Throwable): Unit = {}
}

