/*
 * Copyright 2020 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:OptIn(WorkflowUiExperimentalApi::class)

package com.squareup.sample.nestedrenderings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Providers
import androidx.compose.ui.graphics.Color
import com.squareup.workflow.ui.compose.withCompositionRoot
import com.squareup.workflow1.SimpleLoggingWorkflowInterceptor
import com.squareup.workflow1.ui.ViewEnvironment
import com.squareup.workflow1.ui.ViewRegistry
import com.squareup.workflow1.ui.WorkflowRunner
import com.squareup.workflow1.ui.WorkflowUiExperimentalApi
import com.squareup.workflow1.ui.setContentWorkflow

private val viewRegistry = ViewRegistry(
    RecursiveViewFactory,
    LegacyRunner
)

private val viewEnvironment = ViewEnvironment(mapOf(ViewRegistry to viewRegistry))
    .withCompositionRoot { content ->
      Providers(BackgroundColorAmbient provides Color.Green, content = content)
    }

class NestedRenderingsActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentWorkflow(viewEnvironment) {
      WorkflowRunner.Config(
          RecursiveWorkflow,
          interceptors = listOf(SimpleLoggingWorkflowInterceptor())
      )
    }
  }
}
