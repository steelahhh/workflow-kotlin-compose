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

package com.squareup.sample.textinput

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.squareup.workflow.ui.compose.WorkflowContainer
import com.squareup.workflow1.SimpleLoggingWorkflowInterceptor
import com.squareup.workflow1.ui.ViewEnvironment
import com.squareup.workflow1.ui.ViewRegistry
import com.squareup.workflow1.ui.WorkflowUiExperimentalApi

private val viewRegistry = ViewRegistry(TextInputViewFactory)
private val viewEnvironment = ViewEnvironment(mapOf(ViewRegistry to viewRegistry))

@Composable fun TextInputApp() {
  MaterialTheme {
    WorkflowContainer(
        TextInputWorkflow, viewEnvironment,
        interceptors = listOf(SimpleLoggingWorkflowInterceptor())
    )
  }
}

@Preview(showBackground = true)
@Composable internal fun TextInputAppPreview() {
  TextInputApp()
}
