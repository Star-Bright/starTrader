package com.starbright.trade.cli

import com.starbright.trade.ibclient.contract.ContractOptions

sealed trait CliCommand
case class RequestHistoricalData(contractOptions: ContractOptions) extends CliCommand
case class RequestMarketData() extends CliCommand
case class RequestContractDetails() extends CliCommand
case class RequestPortfolioBalance() extends CliCommand
case class PlaceOrder() extends CliCommand
