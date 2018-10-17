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
package com.googlesource.gerrit.plugins.analytics.wizard

import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Path

import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser._
import com.google.common.io.ByteStreams
import com.google.gerrit.extensions.annotations.PluginData
import com.google.gerrit.extensions.restapi.{
  Response,
  RestApiException,
  RestModifyView,
  RestReadView
}
import com.google.gerrit.server.project.ProjectResource
import com.google.inject.Inject
import com.googlesource.gerrit.plugins.analytics.wizard.AnalyticDashboardSetup.writer

class Input(var dashboardName: String)

class PutAnalyticsStack @Inject()(@PluginData val dataPath: Path)
    extends RestModifyView[ProjectResource, Input] {
  override def apply(resource: ProjectResource,
                     input: Input): Response[String] = {

    val projectName = resource.getName
    val encodedName = AnalyticsWizardActions
      .encodedName(projectName)

    AnalyticDashboardSetup(
      projectName,
      dataPath.resolve(s"docker-compose.${encodedName}.yaml"))
      .createDashboardSetupFile()
    Response.created(s"Dashboard configuration created for $encodedName!")

  }
}

class DockerComposeCommand(var action: String)
class PostAnalyticsStack @Inject()(@PluginData val dataPath: Path)
    extends RestModifyView[ProjectResource, DockerComposeCommand] {
  override def apply(resource: ProjectResource,
                     input: DockerComposeCommand): Response[String] = {

    val projectName = resource.getName
    val encodedName = AnalyticsWizardActions
      .encodedName(projectName)

    val pb = new ProcessBuilder(
      "docker-compose",
      "-f",
      s"${dataPath.toFile.getAbsolutePath}/docker-compose.${encodedName}.yaml",
      input.action.toLowerCase)
    pb.redirectErrorStream(true)

    val ps: Process = pb.start
    ps.getOutputStream.close
    val output =
      new String(ByteStreams.toByteArray(ps.getInputStream), UTF_8)
    ps.waitFor

    ps.exitValue match {
      case 0 => Response.created(output)
      case _ =>
        throw new RestApiException(
          s"Failed with exit code: ${ps.exitValue} - $output")
    }
  }
}

class GetAnalyticsStackStatus @Inject()(@PluginData val dataPath: Path)
    extends RestReadView[ProjectResource] {
  override def apply(resource: ProjectResource): Response[String] = {
    val rawOutput: String = runDockerInspect

    parse(rawOutput) match {
      case Left(e) => throw new RestApiException(s"Parsing failure: $e")
      case Right(json) => {
        val result = json.as[List[DockerInspectResponse]]

        result match {
          case Left(e) => throw new RestApiException(s"Decoding failure: $e")
          case Right(value) => {
            responseFromDockerInspect(value)
          }
        }
      }
    }
  }

  private def runDockerInspect = {
    val containerName = "analytics-wizard_spark-gerrit-analytics-etl_1"

    val pb = new ProcessBuilder("docker", "inspect", containerName)

    val ps: Process = pb.start
    ps.getOutputStream.close
    val rawOutput =
      new String(ByteStreams.toByteArray(ps.getInputStream), UTF_8)
    rawOutput
  }

  private def responseFromDockerInspect(value: List[DockerInspectResponse]) = {
    value match {
      case _ if value.isEmpty =>
        throw new RestApiException(s"Cannot find docker container")
      case _ if value.head.State.ExitCode != 0 =>
        throw new RestApiException(s"Data import failed")
      case _ if value.head.State.Status == "exited" =>
        //Spark ETL job exited successfully
        Response.withStatusCode(204, "finished")
      case _ if value.head.State.Status == "running" =>
        //Spark ETL job is still running
        Response.withStatusCode(202, "processing")
    }
  }
}

object AnalyticsWizardActions {
  // URLEncoder could potentially throw UnsupportedEncodingException,
  // but UTF-8 will *always* be resolved, otherwise, Gerrit wouldn't work at all
  def encodedName(name: String) =
    try {
      URLEncoder.encode(name, "UTF-8")
    } catch {
      case e: Throwable => throw new RuntimeException(e)
    }
}
