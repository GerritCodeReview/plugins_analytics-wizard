package com.googlesource.gerrit.plugins.analytics.wizard

import org.scalatest.{FlatSpec, Matchers}

class AnalyticDashboardSetupSpec extends FlatSpec with Matchers {
  behavior of "AnalyticDashboardSetup"

  it should "create a config file with correct name" in {
    var gotFilename: String = null
    class MockWriter extends ConfigWriter {
      override def write(filename: String, out: String): Unit = {
        gotFilename = filename
      }
    }
    implicit val writer = new MockWriter()

    val ads = AnalyticDashboardSetup("aProject")
    ads.createDashboardSetupFile()
    assert(gotFilename === ads.configFileName)
  }
}
