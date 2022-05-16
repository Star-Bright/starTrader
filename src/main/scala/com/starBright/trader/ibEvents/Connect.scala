package com.starBright.trader.ibEvents

import com.ib.client._
import com.starBright.trader.config.{IBConfig, StarTraderConfigLoader}
import com.sun.org.slf4j.internal.{Logger, LoggerFactory}

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class Connect {

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
}
