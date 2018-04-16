package com.googlesource.gerrit.plugins.analytics.wizard.common

import com.google.gerrit.server.OutputFormat
import com.google.gson.{Gson, GsonBuilder}

object GsonFormatter {
  private val gsonBuilder: GsonBuilder =
    OutputFormat.JSON_COMPACT.newGsonBuilder
  val gson: Gson = gsonBuilder.create
}
