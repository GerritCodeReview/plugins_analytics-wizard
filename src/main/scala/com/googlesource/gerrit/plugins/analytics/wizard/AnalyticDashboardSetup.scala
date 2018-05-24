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

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.time.Instant

trait ConfigWriter {
  def write(outputPath: Path, out: String)
}

class ConfigWriterImpl extends ConfigWriter {
  def write(outputPath: Path, out: String) {
    Files.write(outputPath, out.getBytes(StandardCharsets.UTF_8))
  }
}

case class AnalyticDashboardSetup(name: String, dockerComposeYamlPath: Path)(
    implicit val writer: ConfigWriter) {

  // Docker doesn't like container names with /
  private val sanitisedName =
    s"${name.replace("/", "-")}-${Instant.now.getEpochSecond}"
  private val dockerComposeTemplate = {
    s"""
       |version: '3'
       |services:
       |
       |  dashboard-importer:
       |    image: gerritforge/analytics-dashboard-importer:latest
       |    networks:
       |      - ek
       |    links:
       |      - elasticsearch
       |      - kibana
       |
       |  kibana:
       |    image: gerritforge/analytics-kibana:latest
       |    container_name: "kibana-for-${sanitisedName}-project"
       |    networks:
       |      - ek
       |    depends_on:
       |      - elasticsearch
       |    ports:
       |      - "5601:5601"
       |
       |  elasticsearch:
       |    image: gerritforge/analytics-elasticsearch:latest
       |    container_name: "es-for-${sanitisedName}-project"
       |    networks:
       |      - ek
       |    environment:
       |      - ES_JAVA_OPTS=-Xmx4g -Xms4g
       |      - http.host=0.0.0.0
       |
       |networks:
       |  ek:
       |    driver: bridge
     """.stripMargin
  }

  def createDashboardSetupFile(): Unit = {
    writer.write(dockerComposeYamlPath, dockerComposeTemplate)
  }

}

object AnalyticDashboardSetup {
  implicit val writer = new ConfigWriterImpl()
}
