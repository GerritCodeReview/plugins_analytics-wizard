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

import scala.util.{Failure, Success, Try}

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
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    Try(LocalDate.parse(value, formatter)) match {
      case Success(date)      => Right(date)
      case Failure(exception) => Left(LocalDateValidationError(parameter, value, exception))
    }
  }
  private def validateUrl(parameter: String, value: String): Either[UrlValidationError, URL] = {
    Try(new URL(value)) match {
      case Success(url)       => Right(url)
      case Failure(exception) => Left(UrlValidationError(parameter, value, exception))
    }
  }
  private def validateAggregate(
      value: String): Either[AggregateValidationError, AggregationType] = {
    val maybeAggregate = AggregationType.withNameInsensitiveOption(value.replace("_", ""))
    Either.cond(maybeAggregate.isDefined, maybeAggregate.get, AggregateValidationError(value))
  }
}

sealed trait ETLConfigValidationError {
  def message: String = s"Error validating '$parameter' parameter: $value. Exception: $cause"
  def value: String
  def parameter: String
  def cause: Throwable
}
case class LocalDateValidationError(parameter: String, value: String, cause: Throwable)
    extends ETLConfigValidationError
case class UrlValidationError(parameter: String, value: String, cause: Throwable)
    extends ETLConfigValidationError
case class AggregateValidationError(value: String) extends ETLConfigValidationError {
  val parameter        = "aggregate"
  val cause: Throwable = new Throwable(s"Value $value is not a valid aggregation type")
}
