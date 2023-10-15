package com.starbright.trade.config

import pureconfig.ConfigSource
import pureconfig.generic.auto._

object ConfigLoader {

  def apply(from: String = "application.conf"): AppConfig =
    ConfigSource.default(ConfigSource.resources(from)).loadOrThrow[AppConfig]

}
