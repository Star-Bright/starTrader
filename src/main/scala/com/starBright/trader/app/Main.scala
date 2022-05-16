package com.starBright.trader.app

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{GraphDSL, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape}
import com.ib.client.{EClientSocket, EJavaSignal}
import com.starBright.trader.ibEvents.Events._
import com.starBright.trader.ibtws.IBTws
import com.starBright.trader.requests.Requests
import com.starBright.trader.requests.Requests._
import com.sun.org.slf4j.internal.LoggerFactory

import scala.concurrent.duration.DurationInt

object Main {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem("Main")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val log = LoggerFactory.getLogger(getClass)

    val readerSignal = new EJavaSignal()
    val (source, wrapper) = IBTws.source
    val socket: EClientSocket = new EClientSocket(wrapper, readerSignal)
    val sink = IBTws.sink(socket)

    val graph = RunnableGraph.fromGraph(GraphDSL.create() { implicit graph =>
      import GraphDSL.Implicits._

      socket.eConnect(IBTws.config.localhost, IBTws.config.port, IBTws.config.serverLogLevel)
      socket.startAPI()
      IBTws.connect(socket, readerSignal)

      val accountUpdates: Source[IBRequest, NotUsed] =
        Source
          .single(
            Requests.AccountUpdates(subscribe = true, IBTws.config.clientId)
          )
          .delay(20.seconds)

      val positions: Source[IBRequest, NotUsed] =
        Source
          .single(
            Requests.Positions
          )
          .delay(20.seconds)

      val ibEvents = graph.add(source)
      val ibCommands = graph.add(sink)

      // accountUpdates ~> ibCommands
      positions ~> ibCommands

      ibEvents ~> Sink.foreach[IBEvent] {
        case ConnectionOK =>
          log.warn(s"Connection Success")
        case ExceptionError(e) =>
          log.warn(s"IB Returned an Exception", e)
        case ErrorMessage(m) =>
          log.warn(s"IB Error Message Returned: $m")
        case Error(id, code, msg) =>
          log.warn(s"IB Error id: $id, code: $code, message: $msg")
        case m: UpdateAccountValue =>
          log.warn(s"Account Update: $m")
        case p: Position =>
          log.warn(s"Account Positions: $p")
        case _ =>
      }
      ClosedShape
    })

    graph.run()

  }

}
