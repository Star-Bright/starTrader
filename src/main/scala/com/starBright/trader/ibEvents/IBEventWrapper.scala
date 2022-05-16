package com.starBright.trader.ibEvents

import com.ib.client.{Bar, _}
import com.starBright.trader.ibEvents.Events._
import com.sun.org.slf4j.internal.{Logger, LoggerFactory}

import java.{lang, util}

class IBEventWrapper(event: Events.IBEvent => Unit) extends EWrapper {

  val log: Logger = LoggerFactory.getLogger(getClass)

  override def historicalData(reqId: Int, bar: Bar): Unit =
    event(HistoricalData(reqId, bar))

  override def scannerDataEnd(reqId: Int): Unit =
    event(ScannerDataEnd(reqId))

  override def deltaNeutralValidation(reqId: Int, underComp: DeltaNeutralContract): Unit =
    event(DeltaNeutralValidation(reqId, underComp))

  override def scannerParameters(xml: String): Unit = event(ScannerParameters(xml))

  override def verifyAndAuthMessageAPI(apiData: String, xyzChallange: String): Unit =
    event(VerifyAndAuthMessageAPI(apiData, xyzChallange))

  override def verifyAndAuthCompleted(isSuccessful: Boolean, errorText: String): Unit = {}

  override def tickOptionComputation(
    tickerId: Int,
    field: Int,
    field1: Int,
    impliedVol: Double,
    delta: Double,
    optPrice: Double,
    pvDividend: Double,
    gamma: Double,
    vega: Double,
    theta: Double,
    undPrice: Double
  ): Unit =
    event(
      TickOptionComputation(tickerId, field, impliedVol, delta, optPrice, pvDividend, gamma, vega, theta, undPrice)
    )

  override def softDollarTiers(reqId: Int, tiers: Array[SoftDollarTier]): Unit =
    event(SoftDollarTiers(reqId, tiers))

  override def displayGroupUpdated(reqId: Int, contractInfo: String): Unit =
    event(DisplayGroupUpdated(reqId, contractInfo))

  override def positionMultiEnd(reqId: Int): Unit =
    event(PositionMultiEnd(reqId))

  override def fundamentalData(reqId: Int, data: String): Unit = event(FundamentalData(reqId, data))

  override def nextValidId(orderId: Int): Unit = event(NextValidId(orderId))

  override def positionMulti(
    reqId: Int,
    account: String,
    modelCode: String,
    contract: Contract,
    pos: Double,
    avgCost: Double
  ): Unit =
    event(PositionMulti(reqId, account, modelCode, contract, pos, avgCost))

  override def updateAccountTime(timeStamp: String): Unit = event(UpdateAccountTime(timeStamp))

  override def marketDataType(reqId: Int, marketDataType: Int): Unit = event(
    Events.MarketDataType(reqId, marketDataType)
  )

  override def accountDownloadEnd(accountName: String): Unit = event(AccountDownloadEnd(accountName))

  override def execDetailsEnd(reqId: Int): Unit = event(ExecDetailsEnd(reqId))

  override def accountSummary(reqId: Int, account: String, tag: String, value: String, currency: String): Unit =
    event(AccountSummary(reqId, account, tag, value, currency))

  override def currentTime(time: Long): Unit = event(CurrentTime(time))

  override def updateAccountValue(key: String, value: String, currency: String, accountName: String): Unit =
    event(UpdateAccountValue(key, value, currency, accountName))

  override def tickSnapshotEnd(reqId: Int): Unit =
    event(TickSnapshotEnd(reqId))

  override def tickEFP(
    tickerId: Int,
    tickType: Int,
    basisPoints: Double,
    formattedBasisPoints: String,
    impliedFuture: Double,
    holdDays: Int,
    futureLastTradeDate: String,
    dividendImpact: Double,
    dividendsToLastTradeDate: Double
  ): Unit =
    event(
      TickEFP(
        tickerId,
        tickType,
        basisPoints,
        formattedBasisPoints,
        impliedFuture,
        holdDays,
        futureLastTradeDate,
        dividendImpact,
        dividendsToLastTradeDate
      )
    )

  override def position(account: String, contract: Contract, pos: Double, avgCost: Double): Unit =
    event(Position(account, contract, pos, avgCost))

  override def execDetails(reqId: Int, contract: Contract, execution: Execution): Unit =
    event(ExecDetails(reqId, contract, execution))

  override def positionEnd(): Unit =
    event(PositionEnd)

  override def tickGeneric(tickerId: Int, tickType: Int, value: Double): Unit =
    event(TickGeneric(tickerId, tickType, value))

  override def updateNewsBulletin(msgId: Int, msgType: Int, message: String, origExchange: String): Unit =
    event(UpdateNewsBulletin(msgId, msgType, message, origExchange))

  override def realtimeBar(
    reqId: Int,
    time: Long,
    open: Double,
    high: Double,
    low: Double,
    close: Double,
    volume: Long,
    wap: Double,
    count: Int
  ): Unit =
    event(RealtimeBar(reqId, time, open, high, low, close, volume, wap, count))

  override def accountSummaryEnd(reqId: Int): Unit =
    event(AccountSummaryEnd(reqId))

  override def updatePortfolio(
    contract: Contract,
    position: Double,
    marketPrice: Double,
    marketValue: Double,
    averageCost: Double,
    unrealizedPNL: Double,
    realizedPNL: Double,
    accountName: String
  ): Unit =
    event(
      UpdatePortfolio(
        contract,
        position,
        marketPrice,
        marketValue,
        averageCost,
        unrealizedPNL,
        realizedPNL,
        accountName
      )
    )

  override def securityDefinitionOptionalParameterEnd(reqId: Int): Unit =
    event(SecurityDefinitionOptionalParameterEnd(reqId))

  override def orderStatus(
    orderId: Int,
    status: String,
    filled: Double,
    remaining: Double,
    avgFillPrice: Double,
    permId: Int,
    parentId: Int,
    lastFillPrice: Double,
    clientId: Int,
    whyHeld: String,
    extra: Double
  ): Unit =
    event(
      Events.OrderStatus(
        orderId,
        status,
        filled,
        remaining,
        avgFillPrice,
        permId,
        parentId,
        lastFillPrice,
        clientId,
        whyHeld
      )
    )

  override def error(e: Exception): Unit =
    event(ExceptionError(e))

  override def error(str: String): Unit =
    event(ErrorMessage(str))

  override def error(id: Int, errorCode: Int, errorMsg: String): Unit =
    event(Error(id, errorCode, errorMsg))

  override def tickSize(tickerId: Int, field: Int, size: Int): Unit =
    event(TickSize(tickerId, field, size))

  override def accountUpdateMultiEnd(reqId: Int): Unit =
    event(AccountUpdateMultiEnd(reqId))

  override def verifyCompleted(isSuccessful: Boolean, errorText: String): Unit =
    event(VerifyCompleted(isSuccessful, errorText))

  override def verifyMessageAPI(apiData: String): Unit =
    event(VerifyMessageAPI(apiData))

  override def updateMktDepthL2(
    tickerId: Int,
    position: Int,
    marketMaker: String,
    operation: Int,
    side: Int,
    price: Double,
    size: Int,
    depth: Boolean
  ): Unit =
    event(UpdateMktDepthL2(tickerId, position, marketMaker, operation, side, price, size, depth))

  override def displayGroupList(reqId: Int, groups: String): Unit =
    event(DisplayGroupList(reqId, groups))

  override def managedAccounts(accountsList: String): Unit =
    event(ManagedAccounts(accountsList))

  override def contractDetailsEnd(reqId: Int): Unit =
    event(ContractDetailsEnd(reqId))

  override def accountUpdateMulti(
    reqId: Int,
    account: String,
    modelCode: String,
    key: String,
    value: String,
    currency: String
  ): Unit =
    event(AccountUpdateMulti(reqId, account, modelCode, key, value, currency))

  override def tickString(tickerId: Int, tickType: Int, value: String): Unit =
    event(TickString(tickerId, tickType, value))

  override def openOrder(orderId: Int, contract: Contract, order: Order, orderState: OrderState): Unit =
    event(OpenOrder(orderId, contract, order, orderState))

  override def updateMktDepth(
    tickerId: Int,
    position: Int,
    operation: Int,
    side: Int,
    price: Double,
    size: Int
  ): Unit =
    event(UpdateMktDepth(tickerId, position, operation, side, price, size))

  override def receiveFA(faDataType: Int, xml: String): Unit =
    event(ReceiveFA(faDataType, xml))

  override def openOrderEnd(): Unit =
    event(OpenOrderEnd)

  override def connectionClosed(): Unit =
    event(ConnectionClosed)

  override def connectAck(): Unit =
    event(ConnectionOK)

  override def securityDefinitionOptionalParameter(
    reqId: Int,
    exchange: String,
    underlyingConId: Int,
    tradingClass: String,
    multiplier: String,
    expirations: util.Set[String],
    strikes: util.Set[lang.Double]
  ): Unit =
    event(
      SecurityDefinitionOptionalParameter(
        reqId,
        exchange,
        underlyingConId,
        tradingClass,
        multiplier,
        expirations,
        strikes
      )
    )

  import com.ib.client

  override def bondContractDetails(reqId: Int, contractDetails: client.ContractDetails): Unit =
    event(BondContractDetails(reqId, contractDetails))

  override def scannerData(
    reqId: Int,
    rank: Int,
    contractDetails: client.ContractDetails,
    distance: String,
    benchmark: String,
    projection: String,
    legsStr: String
  ): Unit =
    event(ScannerData(reqId, rank, contractDetails, distance, benchmark, projection, legsStr))

  override def commissionReport(commissionReport: client.CommissionReport): Unit =
    event(Events.CommissionReport(commissionReport))

  override def contractDetails(reqId: Int, contractDetails: client.ContractDetails): Unit =
    event(Events.ContractDetails(reqId, contractDetails))

  override def symbolSamples(i: Int, contractDescriptions: Array[ContractDescription]): Unit =
    event(SymbolSamples(i, contractDescriptions))

  override def historicalNews(i: Int, s: String, s1: String, s2: String, s3: String): Unit =
    event(HistoricalNews(i, s, s1, s2, s3))

  override def tickPrice(i: Int, i1: Int, v: Double, tickAttrib: TickAttrib): Unit =
    event(TickPriceWithAttr(i, i1, v, tickAttrib))

  override def smartComponents(i: Int, map: util.Map[Integer, util.Map.Entry[String, Character]]): Unit =
    event(SmartComponents(i, map))

  override def newsArticle(i: Int, i1: Int, s: String): Unit =
    event(NewsArticle(i, i1, s))

  override def newsProviders(newsProviders: Array[NewsProvider]): Unit =
    event(NewsProviders(newsProviders))

  override def headTimestamp(i: Int, s: String): Unit =
    event(HeadTimestamp(i, s))

  override def tickReqParams(i: Int, v: Double, s: String, i1: Int): Unit =
    event(TickReqParams(i, v, s, i1))

  override def tickNews(i: Int, l: Long, s: String, s1: String, s2: String, s3: String): Unit =
    event(TickNews(i, l, s, s1, s2, s3))

  override def historicalDataEnd(i: Int, s: String, s1: String): Unit =
    event(HistoricalDataEnd(i, s, s1))

  override def histogramData(i: Int, list: util.List[HistogramEntry]): Unit =
    event(HistogramData(i, list))

  override def familyCodes(familyCodes: Array[FamilyCode]): Unit =
    event(FamilyCodes(familyCodes))

  override def historicalNewsEnd(i: Int, b: Boolean): Unit =
    event(HistoricalNewsEnd(i, b))

  override def mktDepthExchanges(depthMktDataDescriptions: Array[DepthMktDataDescription]): Unit =
    event(MktDepthExchanges(depthMktDataDescriptions))

  override def historicalDataUpdate(i: Int, bar: Bar): Unit = ???

  override def rerouteMktDataReq(i: Int, i1: Int, s: String): Unit = ???

  override def rerouteMktDepthReq(i: Int, i1: Int, s: String): Unit = ???

  override def marketRule(i: Int, priceIncrements: Array[PriceIncrement]): Unit = ???

  override def pnl(i: Int, v: Double, v1: Double, v2: Double): Unit = ???

  override def pnlSingle(i: Int, i1: Int, v: Double, v1: Double, v2: Double, v3: Double): Unit = ???

  override def historicalTicks(i: Int, list: util.List[HistoricalTick], b: Boolean): Unit = ???

  override def historicalTicksBidAsk(i: Int, list: util.List[HistoricalTickBidAsk], b: Boolean): Unit = ???

  override def historicalTicksLast(i: Int, list: util.List[HistoricalTickLast], b: Boolean): Unit = ???

  override def tickByTickAllLast(
    i: Int,
    i1: Int,
    l: Long,
    v: Double,
    i2: Int,
    tickAttribLast: TickAttribLast,
    s: String,
    s1: String
  ): Unit = ???

  override def tickByTickBidAsk(
    i: Int,
    l: Long,
    v: Double,
    v1: Double,
    i1: Int,
    i2: Int,
    tickAttribBidAsk: TickAttribBidAsk
  ): Unit = ???

  override def tickByTickMidPoint(i: Int, l: Long, v: Double): Unit = ???

  override def orderBound(l: Long, i: Int, i1: Int): Unit = ???

  override def completedOrder(contract: Contract, order: Order, orderState: OrderState): Unit = ???

  override def completedOrdersEnd(): Unit = ???

  override def replaceFAEnd(i: Int, s: String): Unit = ???

}
