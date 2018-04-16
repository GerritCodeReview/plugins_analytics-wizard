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
package com.googlesource.gerrit.plugins.analytics.wizard

import java.io.PrintWriter

import com.google.gerrit.extensions.restapi._
import com.google.gerrit.server.project.ProjectResource
import com.google.inject.{Inject, TypeLiteral}
import com.googlesource.gerrit.plugins.analytics.wizard.common.GsonFormatter
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}

class AnalyticsWizard {}

class AnalyticsWizardResource(param: String) extends RestResource {}

object AnalyticsWizardResource {
  val ANALYTICS_KIND: TypeLiteral[RestView[AnalyticsWizardResource]] =
    new TypeLiteral[RestView[AnalyticsWizardResource]]() {}
}

class GetAnalyticsWizard @Inject()() extends RestReadView[ProjectResource] {
  val log = LoggerFactory.getLogger(classOf[GetAnalyticsWizard])
  override def apply(resource: ProjectResource): AnyRef = {
    log.error(s"Hitting the GET!! ")
    Response.ok("Oky! Works!!")
  }
}

class PutAnalyticsWizard @Inject()()
    extends RestModifyView[ProjectResource, String] {
  val log = LoggerFactory.getLogger(classOf[GetAnalyticsWizard])

  override def apply(resource: ProjectResource,
                     input: String): Response[String] = {
    log.error(s"Hitting PUT $input")

    //TODO Need to deserialise JSON here and build a proper input object

    Try {
      val p = new PrintWriter("/tmp/docker-file")
      p.write(AnalyticDashboardSetup.dockerComposeTemplate("aName"))
      p.close
    } match {
      case Success(_) => {
        val analyticsDashboard = AnalyticDashboardSetup(input)
        Response.created(GsonFormatter.gson.toJson(analyticsDashboard))
      }
      case Failure(t) => {
        Response.withStatusCode[String](
          500,
          GsonFormatter.gson.toJson(
            ErrorResponse(s"An error has occured: ${t.getMessage}")))
      }
    }
  }
}

case class ErrorResponse(error: String)
