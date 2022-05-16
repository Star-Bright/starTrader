package com.starBright.trader.ibEvents

import com.ib.client
import com.ib.client._

import java.{lang, util}

object Events {

  sealed trait IBEvent
  case object ConnectionOK extends IBEvent

  case class HistoricalData(reqId: Int, bar: client.Bar) extends IBEvent

  case class ScannerDataEnd(reqId: Int) extends IBEvent
  case class DeltaNeutralValidation(reqId: Int, underComp: DeltaNeutralContract) extends IBEvent

  case class TickOptionComputation(
    tickerId: Int,
    field: Int,
    impliedVol: Double,
    delta: Double,
    optPrice: Double,
    pvDividend: Double,
    gamma: Double,
    vega: Double,
    theta: Double,
    undPrice: Double
  ) extends IBEvent

  case class SoftDollarTiers(reqId: Int, tiers: Array[SoftDollarTier]) extends IBEvent
  case class DisplayGroupUpdated(reqId: Int, contractInfo: String) extends IBEvent
  case class PositionMultiEnd(reqId: Int) extends IBEvent
  case class FundamentalData(reqId: Int, data: String) extends IBEvent
  case class BondContractDetails(reqId: Int, contractDetails: client.ContractDetails) extends IBEvent
  case class NextValidId(orderId: Int) extends IBEvent

  case class PositionMulti(
    reqId: Int,
    account: String,
    modelCode: String,
    contract: Contract,
    pos: Double,
    avgCost: Double
  ) extends IBEvent

  case class UpdateAccountTime(timeStamp: String) extends IBEvent
  case class MarketDataType(reqId: Int, marketDataType: Int) extends IBEvent
  case class AccountDownloadEnd(accountName: String) extends IBEvent
  case class ExecDetailsEnd(reqId: Int) extends IBEvent
  case class AccountSummary(reqId: Int, account: String, tag: String, value: String, currency: String) extends IBEvent
  case class CurrentTime(time: Long) extends IBEvent
  case class UpdateAccountValue(key: String, value: String, currency: String, accountName: String) extends IBEvent
  case class TickPrice(i: Int, i1: Int, v: Double, i2: Int) extends IBEvent
  case class TickSnapshotEnd(reqId: Int) extends IBEvent

  case class TickEFP(
    tickerId: Int,
    tickType: Int,
    basisPoints: Double,
    formattedBasisPoints: String,
    impliedFuture: Double,
    holdDays: Int,
    futureLastTradeDate: String,
    dividendImpact: Double,
    dividendsToLastTradeDate: Double
  ) extends IBEvent

  case class Position(account: String, contract: Contract, pos: Double, avgCost: Double) extends IBEvent
  case class ExecDetails(reqId: Int, contract: Contract, execution: Execution) extends IBEvent
  case object PositionEnd extends IBEvent
  case class Error(id: Int, errorCode: Int, errorMsg: String) extends IBEvent
  case class TickGeneric(tickerId: Int, tickType: Int, value: Double) extends IBEvent
  case class UpdateNewsBulletin(msgId: Int, msgType: Int, message: String, origExchange: String) extends IBEvent

  case class RealtimeBar(
    reqId: Int,
    time: Long,
    open: Double,
    high: Double,
    low: Double,
    close: Double,
    volume: Long,
    wap: Double,
    count: Int
  ) extends IBEvent

  case class AccountSummaryEnd(reqId: Int) extends IBEvent

  case class ScannerData(
    reqId: Int,
    rank: Int,
    contractDetails: client.ContractDetails,
    distance: String,
    benchmark: String,
    projection: String,
    legsStr: String
  ) extends IBEvent

  case class UpdatePortfolio(
    contract: Contract,
    position: Double,
    marketPrice: Double,
    marketValue: Double,
    averageCost: Double,
    unrealizedPNL: Double,
    realizedPNL: Double,
    accountName: String
  ) extends IBEvent

  case class SecurityDefinitionOptionalParameterEnd(reqId: Int) extends IBEvent

  case class OrderStatus(
    orderId: Int,
    status: String,
    filled: Double,
    remaining: Double,
    avgFillPrice: Double,
    permId: Int,
    parentId: Int,
    lastFillPrice: Double,
    clientId: Int,
    whyHeld: String
  ) extends IBEvent

  case class ExceptionError(e: Exception) extends IBEvent
  case class ErrorMessage(str: String) extends IBEvent
  case class TickSize(tickerId: Int, field: Int, size: Int) extends IBEvent
  case class AccountUpdateMultiEnd(reqId: Int) extends IBEvent
  case class VerifyCompleted(isSuccessful: Boolean, errorText: String) extends IBEvent
  case class CommissionReport(commissionReport: client.CommissionReport) extends IBEvent
  case class VerifyMessageAPI(apiData: String) extends IBEvent

  case class UpdateMktDepthL2(
    tickerId: Int,
    position: Int,
    marketMaker: String,
    operation: Int,
    side: Int,
    price: Double,
    size: Int,
    depth: Boolean
  ) extends IBEvent

  case class ContractDetails(reqId: Int, contractDetails: client.ContractDetails) extends IBEvent
  case class DisplayGroupList(reqId: Int, groups: String) extends IBEvent
  case class ManagedAccounts(accountsList: String) extends IBEvent
  case class ContractDetailsEnd(reqId: Int) extends IBEvent

  case class AccountUpdateMulti(
    reqId: Int,
    account: String,
    modelCode: String,
    key: String,
    value: String,
    currency: String
  ) extends IBEvent

  case class TickString(tickerId: Int, tickType: Int, value: String) extends IBEvent
  case class OpenOrder(orderId: Int, contract: Contract, order: Order, orderState: OrderState) extends IBEvent

  case class UpdateMktDepth(tickerId: Int, position: Int, operation: Int, side: Int, price: Double, size: Int)
      extends IBEvent

  case class ReceiveFA(faDataType: Int, xml: String) extends IBEvent
  case object OpenOrderEnd extends IBEvent
  case object ConnectionClosed extends IBEvent
  case class ScannerParameters(xml: String) extends IBEvent
  case class VerifyAndAuthMessageAPI(apiData: String, xyzChallange: String) extends IBEvent
  case class VerifyAndAuthCompleted(isSuccessful: Boolean, errorText: String) extends IBEvent

  case class SecurityDefinitionOptionalParameter(
    reqId: Int,
    exchange: String,
    underlyingConId: Int,
    tradingClass: String,
    multiplier: String,
    expirations: util.Set[String],
    strikes: util.Set[lang.Double]
  ) extends IBEvent

  case class SymbolSamples(i: Int, contractDescriptions: Array[ContractDescription]) extends IBEvent
  case class HistoricalNews(i: Int, s: String, s1: String, s2: String, s3: String) extends IBEvent
  case class TickPriceWithAttr(i: Int, i1: Int, v: Double, tickAttrib: TickAttrib) extends IBEvent
  case class SmartComponents(i: Int, map: util.Map[Integer, util.Map.Entry[String, Character]]) extends IBEvent
  case class NewsArticle(i: Int, i1: Int, s: String) extends IBEvent
  case class NewsProviders(newsProviders: Array[NewsProvider]) extends IBEvent
  case class HeadTimestamp(i: Int, s: String) extends IBEvent
  case class TickReqParams(i: Int, v: Double, s: String, i1: Int) extends IBEvent
  case class TickNews(i: Int, l: Long, s: String, s1: String, s2: String, s3: String) extends IBEvent
  case class HistoricalDataEnd(i: Int, s: String, s1: String) extends IBEvent
  case class HistogramData(i: Int, list: util.List[HistogramEntry]) extends IBEvent
  case class FamilyCodes(familyCodes: Array[FamilyCode]) extends IBEvent
  case class HistoricalNewsEnd(i: Int, b: Boolean) extends IBEvent
  case class MktDepthExchanges(depthMktDataDescriptions: Array[DepthMktDataDescription]) extends IBEvent

}
