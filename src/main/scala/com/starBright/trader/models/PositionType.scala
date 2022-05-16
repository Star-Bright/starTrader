package com.starBright.trader.models

sealed trait PositionType
case object Long extends PositionType
case object Short extends PositionType
case object Hold extends PositionType
