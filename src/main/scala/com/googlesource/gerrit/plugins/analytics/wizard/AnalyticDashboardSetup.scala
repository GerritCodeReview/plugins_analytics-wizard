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

import java.net.URL
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

trait ConfigWriter {
  def write(outputPath: Path, out: String)
}

class ConfigWriterImpl extends ConfigWriter {
  def write(outputPath: Path, out: String) {
    Files.write(outputPath, out.getBytes(StandardCharsets.UTF_8))
  }
}

case class AnalyticDashboardSetup(name: String, dockerComposeYamlPath: Path, gerritLocalUrl: URL)(
    implicit val writer: ConfigWriter) {

  // Docker doesn't like container names with '/', hence the replace with '-'
  // Furthermore timestamp has been added to avoid conflicts among container names, i.e.:
  // A project named 'foo/bar' would be encoded as 'foo-bar' and thus its container
  // would be potentially in conflict with another 'foo-bar' project's one
  private val sanitisedName =
    s"${name.replace("/", "-")}-${System.currentTimeMillis}"
  private val dockerComposeTemplate = {
    s"""
       |version: '3'
       |services:
       |
       |  spark-gerrit-analytics-etl:
       |    extra_hosts:
       |      - gerrit:${gerritLocalUrl.getHost}
       |    image: gerritforge/spark-gerrit-analytics-etl:latest
       |    environment:
       |      - ES_HOST=elasticsearch
       |      - GERRIT_URL=${gerritLocalUrl.getProtocol}://gerrit:${gerritLocalUrl.getPort}
       |      - ANALYTICS_ARGS=--since 2000-06-01 --aggregate email_hour --writeNotProcessedEventsTo file:///tmp/failed-events -e gerrit/analytics
       |    networks:
       |      - ek
       |    links:
       |      - elasticsearch
       |    depends_on:
       |      - elasticsearch
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
       |      - ES_JAVA_OPTS=-Xmx1g -Xms1g
       |      - http.host=0.0.0.0
       |      - network.host=_site_
       |      - http.publish_host=_site_
       |    ports:
       |      - "9200:9200"
       |      - "9300:9300"
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
