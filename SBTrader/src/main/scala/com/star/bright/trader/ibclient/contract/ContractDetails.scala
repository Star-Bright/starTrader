package com.starbright.trade.ibclient.contract

import com.ib.client.{Contract, ContractDetails}
import com.starbright.trade.ibclient.IBClient

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object ContractDetails {

  def apply(ibClient: IBClient, contractOptions: ContractOptions): Seq[ContractDetails] = {
    val contract: Contract = com.starbright.trade.ibclient.contract.Contract(contractOptions).get
    val futureContractDetails = ibClient.contractDetails(contract)
    Await.result(futureContractDetails, Duration.Inf)
  }

}
