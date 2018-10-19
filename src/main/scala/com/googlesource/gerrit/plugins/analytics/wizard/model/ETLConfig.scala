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
import java.time.format.DateTimeFormatter

import enumeratum._

import scala.util.Try

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

object ETLConfig {
  def fromRaw(raw: ETLConfigRaw): Either[ETLConfigValidationError, ETLConfig] = {
    for {
      s  <- validateLocalDate("since", raw.since).right
      u  <- validateLocalDate("until", raw.until).right
      ur <- validateUrl("url", raw.url).right
      w  <- validateUrl("writeNotProcessedEventsTo", raw.writeNotProcessedEventsTo).right
      eu <- validateUrl("eventsUrl", raw.eventsUrl).right
      a  <- validateAggregate(raw.aggregate).right
    } yield
      ETLConfig(
        aggregate = a,
        projectPrefix = raw.projectPrefix,
        since = s,
        until = u,
        url = ur,
        eventsUrl = eu,
        writeNotProcessedEventsTo = w,
        emailAliasesPath = raw.emailAliasesPath,
        username = raw.username,
        password = raw.password
      )
  }

  private def validateLocalDate(parameter: String,
                                value: String): Either[LocalDateValidationError, LocalDate] = {
    val formatter      = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val maybeLocalDate = Try(LocalDate.parse(value, formatter)).toOption
    Either.cond(maybeLocalDate.isDefined,
                maybeLocalDate.get,
                LocalDateValidationError(parameter, value))
  }
  private def validateUrl(parameter: String, value: String): Either[UrlValidationError, URL] = {
    val maybeUrl = Try(new URL(value)).toOption
    Either.cond(maybeUrl.isDefined, maybeUrl.get, UrlValidationError(parameter, value))
  }
  private def validateAggregate(
      value: String): Either[AggregateValidationError, AggregationType] = {
    val maybeAggregate = AggregationType.withNameInsensitiveOption(value.replace("_", ""))
    Either.cond(maybeAggregate.isDefined, maybeAggregate.get, AggregateValidationError(value))
  }
}

trait ETLConfigValidationError {
  def message: String = s"Error validating '$parameter' parameter: $value"
  def value: String
  def parameter: String
}
case class LocalDateValidationError(parameter: String, value: String)
    extends ETLConfigValidationError
case class UrlValidationError(parameter: String, value: String)  extends ETLConfigValidationError
case class FileValidationError(parameter: String, value: String) extends ETLConfigValidationError
case class AggregateValidationError(value: String) extends ETLConfigValidationError {
  val parameter = "aggregate"
}
