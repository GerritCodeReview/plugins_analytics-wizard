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
       |  depends_on:
       |    - elasticsearch
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
