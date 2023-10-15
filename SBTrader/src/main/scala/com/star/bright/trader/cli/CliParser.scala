package com.starbright.trade.cli

import cats.implicits._
import com.monovore.decline._
import com.monovore.decline.time._
import com.starbright.trade.ibclient.contract.ContractOptions
import org.slf4j.{Logger, LoggerFactory}

import java.time.LocalDate

object CliParser {

  private val log: Logger = LoggerFactory.getLogger(this.getClass)

  private[this] val contractOpt: Opts[String] =
    Opts
      .option[String](
        "contract",
        "Contract to request.",
        short = "c"
      )

  private[this] val symbolOpt: Opts[String] =
    Opts
      .option[String](
        "symbol",
        "Symbol to request.",
        short = "s"
      )

  private[this] val securityTypeOpt: Opts[String] =
    Opts
      .option[String](
        "security",
        "SecurityType to request.",
        short = "w"
      )

  private[this] val exchangeOpt: Opts[String] =
    Opts
      .option[String](
        "exchange",
        "Exchange to request from.",
        short = "b"
      )

  private[this] val currencyOpt: Opts[String] =
    Opts
      .option[String](
        "currency",
        "currency of asset to request.",
        short = "x"
      )

  private[this] val expiryOpt: Opts[String] =
    Opts
      .option[String](
        "expiry",
        "Expiry of contract to request.",
        short = "e"
      )

  private[this] val barSizeOpt: Opts[String] =
    Opts
      .option[String](
        "barSize",
        "Bar size of historical data to request.",
        short = "b"
      )

  private[this] val startDateOpt: Opts[LocalDate] =
    Opts
      .option[LocalDate](
        "startDate",
        "Start date of historical data to request.",
        short = "a"
      )

  private[this] val endDateOpt: Opts[LocalDate] =
    Opts
      .option[LocalDate](
        "endDate",
        "End date of historical data to request.",
        short = "z"
      )

  val requestHistoricalDataArg: Opts[ContractOptions] =
    (
      contractOpt,
      symbolOpt.orNone,
      securityTypeOpt,
      exchangeOpt,
      currencyOpt,
      expiryOpt,
      barSizeOpt,
      startDateOpt,
      endDateOpt
    ).mapN(
      ContractOptions
    )

  private val requestHistoricalData: Opts[RequestHistoricalData] =
    Opts
      .subcommand("historical", help = "Request Historical Data")(
        requestHistoricalDataArg.map(RequestHistoricalData)
      )

  private[this] val cliCommand: Command[CliCommand] = Command[CliCommand](
    name = "dispatcher-analytics",
    header = "Transform data from the ingestion layer to the raw layer."
  )(
    requestHistoricalData
  )

  def parse(args: List[String]): CliCommand = cliCommand
    .parse(args)
    .getOrElse {
      log.error("Option syntax incorrect")
      log.error(s"Arguments given ${args.mkString("'", "' '", "'")}")
      log.error("Failure.")
      sys.exit(1)
    }

}
