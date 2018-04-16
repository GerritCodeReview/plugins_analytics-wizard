package com.googlesource.gerrit.plugins.analytics.wizard

import com.google.gson.Gson
import org.scalatest.{FlatSpec, Matchers}

class AnalyticsWizardSpec extends FlatSpec with Matchers {
  "Gson" should
    "properly deserialise JSON into Input" in {
    val json = s"""{
       | "dashboardName": "anyDashboardName"
       |}""".stripMargin

    val input = new Gson().fromJson(json, classOf[Input])
    input.dashboardName shouldBe "anyDashboardName"
  }
}
