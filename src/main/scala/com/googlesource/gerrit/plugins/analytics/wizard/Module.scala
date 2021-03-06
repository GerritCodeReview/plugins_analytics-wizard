// Copyright (C) 2018 The Android Open Source Project
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

import com.google.gerrit.extensions.registration.DynamicSet
import com.google.gerrit.extensions.restapi.RestApiModule
import com.google.gerrit.extensions.webui.TopMenu
import com.google.gerrit.server.project.ProjectResource.PROJECT_KIND
import com.google.inject.AbstractModule

class Module extends AbstractModule {

  override protected def configure() {
    install(new RestApiModule() {
      override protected def configure() = {

        DynamicSet.bind(binder, classOf[TopMenu]).to(classOf[AnalyticsWizardTopMenu])

        put(PROJECT_KIND, "stack").to(classOf[PutAnalyticsStack])

        post(PROJECT_KIND, "server").to(classOf[PostAnalyticsStack])

        get(PROJECT_KIND, "status").to(classOf[GetAnalyticsStackStatus])
      }
    })
  }
}
