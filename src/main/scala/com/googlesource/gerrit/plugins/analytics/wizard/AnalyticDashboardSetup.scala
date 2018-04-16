package com.googlesource.gerrit.plugins.analytics.wizard

case class AnalyticDashboardSetup(name: String)

object AnalyticDashboardSetup {

  val dockerComposeTemplate = { (name: String) =>
    s"""
       |version: '3'
       |services:
       |  kibana:
       |    build: kibana
       |    environment:
       |      SERVER_BASEPATH: "/kibana-${name}"
       |  depends_on:
       |    - elasticsearch
       |  elasticsearch:
       |    build: elasticsearch
       |    environment:
       |      - ES_JAVA_OPTS=-Xmx4g -Xms4g
       |      - http.host=0.0.0.0
       |    volumes:
       |      - es-indexes:/usr/share/elasticsearch/data
     """.stripMargin
  }
}
