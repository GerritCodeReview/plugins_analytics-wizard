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

import java.io
import java.io.PrintWriter

import com.google.gerrit.extensions.restapi.{
  Response,
  RestModifyView,
  RestReadView
}
import com.google.gerrit.server.project.ProjectResource
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.io.Source
import scala.util.{Failure, Success, Try}

class GetAnalyticsStack @Inject()() extends RestReadView[ProjectResource] {
  val log = LoggerFactory.getLogger(classOf[GetAnalyticsStack])
  override def apply(resource: ProjectResource)
    : Response[_ >: AnalyticDashboardSetup with String <: io.Serializable] = {

    val projectName = resource.getControl.getProject.getName

    Try {
      Source
        .fromFile(s"/tmp/docker-compose.${projectName}.yaml")
        .getLines
        .mkString
    } match {
      case Success(fileContent) =>
        Response.ok(AnalyticDashboardSetup(projectName, fileContent))
      case Failure(t) =>
        Response
          .withStatusCode[String](500,
                                  s"An error has occurred: ${t.getMessage}")
    }

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

    val projectName = resource.getControl.getProject.getName

    Try {
      //TODO: dont' push to tmp
      val p = new PrintWriter(s"/tmp/docker-compose.${projectName}.yaml")
      p.write(AnalyticDashboardSetup.dockerComposeTemplate(projectName))
      p.close
    } match {
      case Success(_) =>
        //XXX What shall we return here?
        Response.created(s"Dashboard configuration created for $projectName!")
      case Failure(t) =>
        Response
          .withStatusCode[String](500,
                                  s"An error has occurred: ${t.getMessage}")
    }
  }
}
