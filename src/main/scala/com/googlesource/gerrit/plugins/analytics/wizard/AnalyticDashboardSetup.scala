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

case class AnalyticDashboardSetup(name: String, config: String)

object AnalyticDashboardSetup {

  val dockerComposeTemplate = { (name: String) =>
    s"""
       |version: '3'
       |services:
       |  kibana:
       |    build: kibana
       |    container_name: "kibana-for-${name}-project"
       |    environment:
       |      SERVER_BASEPATH: "/kibana"
       |    depends_on:
       |      - elasticsearch
       |  elasticsearch:
       |    build: elasticsearch
       |    container_name: "es-for-${name}-project"
       |    environment:
       |      - ES_JAVA_OPTS=-Xmx4g -Xms4g
       |      - http.host=0.0.0.0
       |    volumes:
       |      - es-indexes:/usr/share/elasticsearch/data
     """.stripMargin
  }
}
