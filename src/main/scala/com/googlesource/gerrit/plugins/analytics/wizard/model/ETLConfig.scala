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

import java.net.URL
import java.time.LocalDate
import enumeratum._

case class ETLConfig(
    aggregate: AggregationType,
    projectPrefix: String,
    since: LocalDate,
    until: LocalDate,
    eventsUrl: URL,
    url: URL,
    writeNotProcessedEventsTo: URL,
    emailAliasesPath: String,
    username: String,
    password: String
)

sealed trait AggregationType extends EnumEntry
object AggregationType extends Enum[AggregationType] {
  val values = findValues

  case object Email      extends AggregationType
  case object EmailHour  extends AggregationType
  case object EmailDay   extends AggregationType
  case object EmailMonth extends AggregationType
  case object EmailYear  extends AggregationType
}