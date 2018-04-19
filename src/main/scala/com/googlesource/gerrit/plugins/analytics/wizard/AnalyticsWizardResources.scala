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

import com.google.gerrit.extensions.restapi.{
  Response,
  RestModifyView,
  RestReadView
}
import com.google.gerrit.server.project.ProjectResource
import com.google.inject.Inject
import org.slf4j.LoggerFactory

class GetAnalyticsStack @Inject()() extends RestReadView[ProjectResource] {
  val log = LoggerFactory.getLogger(classOf[GetAnalyticsStack])
  override def apply(resource: ProjectResource): AnyRef = {
    Response.ok("Oky! Works!!")
  }
}

class Input {
  val dashboardName: String = null
}

class PutAnalyticsStack @Inject()()
    extends RestModifyView[ProjectResource, Input] {
  val log = LoggerFactory.getLogger(classOf[GetAnalyticsStack])
  override def apply(resource: ProjectResource,
                     input: Input): Response[String] = {
    Response.ok("Oky! Works!!")

//    Try {
//      val p = new PrintWriter("/tmp/docker-file")
//      p.write(AnalyticDashboardSetup.dockerComposeTemplate("aName"))
//      p.close
//    } match {
//      case Success(_) => {
//        val analyticsDashboard = AnalyticDashboardSetup(input)
//        Response.created(GsonFormatter.gson.toJson(analyticsDashboard))
//      }
//      case Failure(t) => {
//        Response.withStatusCode[String](
//          500,
//          GsonFormatter.gson.toJson(
//            ErrorResponse(s"An error has occured: ${t.getMessage}")))
//      }
//    }
  }
}
