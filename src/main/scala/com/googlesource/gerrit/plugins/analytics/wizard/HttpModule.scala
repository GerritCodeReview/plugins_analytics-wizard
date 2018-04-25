package com.googlesource.gerrit.plugins.analytics.wizard

import com.google.inject.servlet.ServletModule

class HttpModule extends ServletModule {

  override def configureServlets() {
    filter("/a/*").through(classOf[XAuthFilter])
  }
}
