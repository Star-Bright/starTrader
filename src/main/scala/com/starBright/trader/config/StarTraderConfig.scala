package com.starBright.trader.config

import pureconfig._
import pureconfig.generic.auto._

case class IBConfig(port: Int, localhost: String, clientId: String, serverLogLevel: Int = 0)

case class StarTraderConfig(ib: IBConfig)

object StarTraderConfigLoader {

  def loadStarTraderConfig(from: String = "application.conf"): StarTraderConfig =
    ConfigSource.default(ConfigSource.resources(from)).loadOrThrow[StarTraderConfig]

}
