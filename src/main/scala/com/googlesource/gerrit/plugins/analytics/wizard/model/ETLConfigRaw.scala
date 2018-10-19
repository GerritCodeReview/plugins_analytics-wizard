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

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.util.Try

class ETLConfigRaw private (aggregate: String,
                            projectPrefix: String,
                            since: String,
                            until: String,
                            url: String,
                            eventsPath: String,
                            emailAliasesPath: String,
                            writeNotProcessedEventsTo: String,
                            username: String,
                            password: String)

object ETLConfigRaw {
  def apply(aggregate: String,
            projectPrefix: String,
            since: String,
            until: String,
            url: String,
            eventsPath: String,
            emailAliasesPath: String,
            writeNotProcessedEventsTo: String,
            username: String,
            password: String): Either[ETLConfigValidationError, ETLConfig] = {
    for {
      s <- validateLocalDate("since", since).right
      u <- validateLocalDate("until", until).right
    } yield
      ETLConfig(projectPrefix = projectPrefix,
                since = s,
                until = u,
                username = username,
                password = password)
  }

  private def validateLocalDate(parameter: String,
                                value: String): Either[ETLConfigValidationError, LocalDate] = {
    val formatter      = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val maybeLocalDate = Try(LocalDate.parse(value, formatter)).toOption
    Either.cond(maybeLocalDate.isDefined,
                maybeLocalDate.get,
                LocalDateValidationError(parameter, value))
  }
}

trait ETLConfigValidationError {
  def message: String = s"Error validating '$parameter' parameter: $value"
  def value: String
  def parameter: String
}
case class LocalDateValidationError(parameter: String, value: String)
    extends ETLConfigValidationError