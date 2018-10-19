// Copyright (C) 2017 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.googlesource.gerrit.plugins.analytics.wizard.model

import org.scalatest.{FlatSpec, Matchers}

class ETLConfigRawSpec extends FlatSpec with Matchers {

  behavior of "ETLConfigRaw"

  it should "pass validation with correct parameters" in {
    ETLConfigRaw(
      aggregate = "sd",
      projectPrefix = "prefix",
      since = "2018-10-10",
      until = "2018-10-13",
      url = "http",
      eventsPath = "events",
      emailAliasesPath = "path",
      writeNotProcessedEventsTo = "path",
      username = "dss",
      password = "dsd"
    ).isRight shouldBe true
  }

  it should "fail validation for invalid 'since' parameter" in {
    val invalidDate = "randomString"
    val eitherETLConfig = ETLConfigRaw(
      aggregate = "sd",
      projectPrefix = "prefix",
      since = invalidDate,
      until = "2018-10-13",
      url = "http",
      eventsPath = "events",
      emailAliasesPath = "path",
      writeNotProcessedEventsTo = "path",
      username = "dss",
      password = "dsd"
    )
    eitherETLConfig.isLeft shouldBe true
    eitherETLConfig.left.get shouldBe LocalDateValidationError("since", invalidDate)
  }

  it should "fail validation for invalid 'until' parameter" in {
    val invalidDate = "randomString"
    val eitherETLConfig = ETLConfigRaw(
      aggregate = "sd",
      projectPrefix = "prefix",
      since = "2018-10-13",
      until = invalidDate,
      url = "http",
      eventsPath = "events",
      emailAliasesPath = "path",
      writeNotProcessedEventsTo = "path",
      username = "dss",
      password = "dsd"
    )
    eitherETLConfig.isLeft shouldBe true
    eitherETLConfig.left.get shouldBe LocalDateValidationError("until", invalidDate)
  }

}
