package com.starbright.trade.config

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

object ConfigLoaderSpec extends AnyFunSpec with Matchers {

  describe("ConfigLoaderSpec") {
    it("should work") {
      val appConfig: AppConfig = ConfigLoader("application.conf")
      appConfig.asset.stock.contract shouldBe "aaa"
    }
  }

}
