package com.starBright.trader.ibtws

import com.starBright.trader.config.{IBConfig, StarTraderConfigLoader}
import com.starBright.trader.ibEvents.IBEventWrapper
import com.sun.org.slf4j.internal.{Logger, LoggerFactory}
import akka.stream.{Materializer, OverflowStrategy}
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.{Done, NotUsed}
import com.ib.client.{EClientSocket, EReader, EReaderSignal}
import com.starBright.trader.ibEvents.Events.IBEvent
import com.starBright.trader.requests.Requests._

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

object IBTws {

  val config: IBConfig = StarTraderConfigLoader.loadStarTraderConfig().ib

  val log: Logger = LoggerFactory.getLogger(getClass)

  def connect(socket: EClientSocket, readerSignal: EReaderSignal): Future[Unit] = Future {
    val reader = new EReader(socket, readerSignal)
    reader.start()
    while (true)
      if (socket.isConnected) {
        readerSignal.waitForSignal()
        try
          reader.processMsgs()
        catch {
          case NonFatal(e) =>
            log.warn("Error in reader thread", e)
        }
      } else {
        socket.eConnect(config.localhost, config.port, config.serverLogLevel)
        socket.startAPI()
      }
  }(ExecutionContext.fromExecutor(Executors.newFixedThreadPool(1)))

  def disconnect(socket: EClientSocket): Unit = if (socket.isConnected) socket.isConnected

  def source(implicit m: Materializer): (Source[IBEvent, NotUsed], IBEventWrapper) = {
    val (publisherActor, pub) = Source
      .actorRef[IBEvent](512, OverflowStrategy.dropHead)
      .toMat(Sink.asPublisher(true))(Keep.both)
      .run()
    val wrapper = new IBEventWrapper(publisherActor ! _)
    (Source.fromPublisher(pub), wrapper)
  }

  def sink(socket: EClientSocket): Sink[IBRequest, Future[Done]] = Sink.foreach[IBRequest] {
    case request: SoftDollarTiers => socket.reqSoftDollarTiers(request.reqId)
    case request: MarketDataType  => socket.reqMarketDataType(request.marketDataType)
    case request: FundamentalData =>
      socket.reqFundamentalData(request.reqId, request.contract, request.reportType, request.list)
    case request: HistoricalData =>
      socket.reqHistoricalData(
        request.tickerId,
        request.contract,
        request.endDateTime,
        request.durationStr,
        request.barSizeSetting,
        request.whatToShow,
        request.useRTH,
        request.formatDate,
        false,
        request.chartOptions
      )
    case request: AccountUpdates => socket.reqAccountUpdates(request.subscribe, request.acctCode)
    case request: MktDepth =>
      socket.reqMktDepth(request.tickerId, request.contract, request.numRows, false, request.mktDepthOptions)
    case request: SecDefOptParams =>
      socket.reqSecDefOptParams(
        request.reqId,
        request.underlyingSymbol,
        request.futFopExchange,
        request.underlyingSecType,
        request.underlyingConId
      )
    case request: NewsBulletins  => socket.reqNewsBulletins(request.allMsgs)
    case request: PositionsMulti => socket.reqPositionsMulti(request.reqId, request.account, request.modelCode)
    case request: RealTimeBars =>
      socket.reqRealTimeBars(
        request.tickerId,
        request.contract,
        request.barSize,
        request.whatToShow,
        request.useRTH,
        request.realTimeBarsOptions
      )
    case request: Ids => socket.reqIds(request.numIds)
    case request: CalculateImpliedVolatility =>
      socket.calculateImpliedVolatility(
        request.reqId,
        request.contract,
        request.optionPrice,
        request.underPrice,
        request.list
      )
    case request: MktData =>
      socket.reqMktData(request.i, request.contract, request.s, request.b, request.b1, request.list)
    case request: AccountSummary  => socket.reqAccountSummary(request.reqId, request.group, request.tags)
    case request: AutoOpenOrders  => socket.reqAutoOpenOrders(request.bAutoBind)
    case request: ContractDetails => socket.reqContractDetails(request.reqId, request.contract)
    case request: ReplaceFA       => socket.replaceFA(request.faDataType, 0, request.xml)
    case request: AccountUpdatesMulti =>
      socket.reqAccountUpdatesMulti(request.reqId, request.account, request.modelCode, request.ledgerAndNLV)
    case request: PlaceOrder => socket.placeOrder(request.id, request.contract, request.order)
    case request: Executions => socket.reqExecutions(request.reqId, request.filter)
    case request: RequestFA  => socket.requestFA(request.faDataType)
    case request: CalculateOptionPrice =>
      socket.calculateOptionPrice(request.reqId, request.contract, request.volatility, request.underPrice, request.list)
    case request: ScannerSubscription =>
      socket.reqScannerSubscription(
        request.tickerId,
        request.subscription,
        request.list,
        request.scannerSubscriptionOptions
      )
    case request: HistogramData        => socket.reqHistogramData(request.i, request.contract, request.b, request.s)
    case request: MatchingSymbols      => socket.reqMatchingSymbols(request.i, request.s)
    case request: CancelOrder          => socket.cancelOrder(request.i)
    case request: CancelAccountSummary => socket.cancelAccountSummary(request.i)
    case request: CancelAccountUpdatesMulti  => socket.cancelAccountUpdatesMulti(request.i)
    case request: UnsubscribeFromGroupEvents => socket.unsubscribeFromGroupEvents(request.i)
    case request: VerifyMessage              => socket.verifyMessage(request.s)
    case request: QueryDisplayGroups         => socket.queryDisplayGroups(request.i)
    case OpenOrders                          => socket.reqOpenOrders()
    case ScannerParameters                   => socket.reqScannerParameters()
    case CurrentTime                         => socket.reqCurrentTime()
    case AllOpenOrders                       => socket.reqAllOpenOrders()
    case GlobalCancel                        => socket.reqGlobalCancel()
    case Positions                           => socket.reqPositions()
    case StartAPI                            => socket.startAPI()
    case CancelPositions                     => socket.cancelPositions()
    case TwsConnectionTime                   => socket.getTwsConnectionTime()
    case ServerVersion                       => socket.serverVersion()
  }

}
