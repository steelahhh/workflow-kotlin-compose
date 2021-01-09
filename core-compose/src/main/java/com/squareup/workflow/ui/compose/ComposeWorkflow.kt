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
@file:Suppress("RemoveEmptyParenthesesFromAnnotationEntry")

package com.squareup.workflow.ui.compose

import androidx.compose.runtime.Composable
import com.squareup.workflow1.Sink
import com.squareup.workflow1.StatefulWorkflow
import com.squareup.workflow1.Workflow
import com.squareup.workflow1.ui.ViewEnvironment
import com.squareup.workflow1.ui.WorkflowUiExperimentalApi
import com.squareup.workflow.ui.compose.internal.ComposeWorkflowImpl

/**
 * A stateless [Workflow][com.squareup.workflow1.Workflow] that [renders][render] itself as
 * [Composable] function. Effectively defines an inline
 * [composedViewFactory][com.squareup.workflow1.ui.compose.composedViewFactory].
 *
 * This workflow does not have access to a [RenderContext][com.squareup.workflow1.RenderContext]
 * since render contexts are only valid during render passes, and this workflow's [render] method
 * is invoked after the render pass, when view bindings are being shown.
 *
 * While this workflow is "stateless" in the usual workflow sense (it doesn't have a `StateT` type),
 * since [render] is a Composable function, it can use all the usual Compose facilities for state
 * management.
 */
abstract class ComposeWorkflow<in PropsT, out OutputT : Any> :
    Workflow<PropsT, OutputT, ComposeRendering> {

  /**
   * Renders [props] using Compose. This function will be called to update the UI whenever the
   * [props] or [viewEnvironment] changes.
   *
   * @param props The data to render.
   * @param outputSink A [Sink] that can be used from UI event handlers to send outputs to this
   * workflow's parent.
   * @param viewEnvironment The [ViewEnvironment] passed down through the `ViewBinding` pipeline.
   */
  @Composable abstract fun render(
    props: PropsT,
    outputSink: Sink<OutputT>,
    viewEnvironment: ViewEnvironment
  )

  override fun asStatefulWorkflow(): StatefulWorkflow<PropsT, *, OutputT, ComposeRendering> =
    ComposeWorkflowImpl(this)
}

/**
 * Returns a [ComposeWorkflow] that renders itself using the given [render] function.
 */
inline fun <PropsT, OutputT : Any> Workflow.Companion.composed(
  crossinline render: @Composable (
    props: PropsT,
    outputSink: Sink<OutputT>,
    environment: ViewEnvironment
  ) -> Unit
): ComposeWorkflow<PropsT, OutputT> = object : ComposeWorkflow<PropsT, OutputT>() {
  @Composable override fun render(
    props: PropsT,
    outputSink: Sink<OutputT>,
    viewEnvironment: ViewEnvironment
  ) {
    render(props, outputSink, viewEnvironment)
  }
}
