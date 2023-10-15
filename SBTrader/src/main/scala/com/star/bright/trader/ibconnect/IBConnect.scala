package com.starbright.trade.ibconnect

import com.starbright.trade.ibclient.IBClient

case class IBConnect(host: String = "localhost", port: Int = 7496, clientId: Int = 1)

object IBConnect {

  def connect(ibConnect: IBConnect): IBClient =
    new IBClient(ibConnect.host, ibConnect.port, ibConnect.clientId)

}
