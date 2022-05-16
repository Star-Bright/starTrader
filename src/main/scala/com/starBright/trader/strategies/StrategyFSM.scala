package com.starBright.trader.strategies

import akka.actor.{Actor, FSM}
import com.starBright.trader.strategies.StrategyFSM._

object StrategyFSM {

  sealed trait State

  case object Flat extends State

  sealed trait Data

  case object Empty extends Data

}

class StrategyFSM extends Actor with FSM[State, Data] {

  startWith(Flat, Empty)

  // TODO: your strategy here

  whenUnhandled { case Event(e, s) =>
    log.warning("Received unhandled request {} in state {}/{}", e, stateName, s)
    stay()
  }

  initialize()
}
