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

package com.squareup.sample.hellocompose

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.squareup.workflow.ui.compose.WorkflowContainer
import com.squareup.workflow1.SimpleLoggingWorkflowInterceptor
import com.squareup.workflow1.ui.ViewEnvironment
import com.squareup.workflow1.ui.ViewRegistry
import com.squareup.workflow1.ui.WorkflowUiExperimentalApi

private val viewRegistry = ViewRegistry(HelloBinding)
private val viewEnvironment = ViewEnvironment(mapOf(ViewRegistry to viewRegistry))

@Composable fun App() {
  MaterialTheme {
    WorkflowContainer(
        HelloWorkflow, viewEnvironment,
        modifier = Modifier.border(
            shape = RoundedCornerShape(10.dp),
            width = 10.dp,
            color = Color.Magenta
        ),
        interceptors = listOf(SimpleLoggingWorkflowInterceptor())
    )
  }
}

@Preview(showBackground = true)
@Composable private fun AppPreview() {
  App()
}
