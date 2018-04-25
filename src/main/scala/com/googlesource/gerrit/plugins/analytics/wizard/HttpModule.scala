package com.googlesource.gerrit.plugins.analytics.wizard

import com.google.gerrit.extensions.registration.DynamicSet
import com.google.gerrit.httpd.AllRequestFilter
import com.google.inject.servlet.ServletModule

class HttpModule extends ServletModule {

  override def configureServlets() {
    filterRegex(".*").through(classOf[XAuthFilter])

    DynamicSet.bind(binder(), classOf[AllRequestFilter]).to(classOf[XAuthFilter])
  }
}
