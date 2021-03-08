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
@file:Suppress("TestFunctionName", "PrivatePropertyName")

package com.squareup.workflow.ui.compose.tooling

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.ui.test.createComposeRule
import androidx.compose.ui.tooling.preview.Preview
import com.squareup.workflow1.Workflow
import com.squareup.workflow1.ui.ViewEnvironmentKey
import com.squareup.workflow1.ui.compose.WorkflowRendering
import com.squareup.workflow1.ui.compose.composed
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Duplicate of [PreviewViewFactoryTest] but for [com.squareup.workflow1.ui.compose.ComposeWorkflow].
 */
@RunWith(AndroidJUnit4::class)
class PreviewComposeWorkflowTest {

  @Rule @JvmField val composeRule = createComposeRule()

  @Test fun singleChild() {
    composeRule.setContent {
      ParentWithOneChildPreview()
    }

    composeRule.onNodeWithText("one").assertIsDisplayed()
    composeRule.onNodeWithText("two").assertIsDisplayed()
  }

  @Test fun twoChildren() {
    composeRule.setContent {
      ParentWithTwoChildrenPreview()
    }

    composeRule.onNodeWithText("one").assertIsDisplayed()
    composeRule.onNodeWithText("two").assertIsDisplayed()
    composeRule.onNodeWithText("three").assertIsDisplayed()
  }

  @Test fun modifierIsApplied() {
    composeRule.setContent {
      ParentWithModifier()
    }

    // The view factory will be rendered with size (0,0), so it should be reported as not displayed.
    composeRule.onNodeWithText("one").assertIsNotDisplayed()
    composeRule.onNodeWithText("two").assertIsNotDisplayed()
  }

  @Test fun placeholderModifierIsApplied() {
    composeRule.setContent {
      ParentWithPlaceholderModifier()
    }

    // The child will be rendered with size (0,0), so it should be reported as not displayed.
    composeRule.onNodeWithText("one").assertIsDisplayed()
    composeRule.onNodeWithText("two").assertIsNotDisplayed()
  }

  @Test fun customViewEnvironment() {
    composeRule.setContent {
      ParentConsumesCustomKeyPreview()
    }

    composeRule.onNodeWithText("foo").assertIsDisplayed()
  }

  private val ParentWithOneChild =
    Workflow.composed<Pair<String, String>, Nothing> { props, _, environment ->
      Column {
        BasicText(props.first)
        WorkflowRendering(props.second, environment)
      }
    }

  @Preview @Composable private fun ParentWithOneChildPreview() {
    ParentWithOneChild.preview(Pair("one", "two"))
  }

  private val ParentWithTwoChildren =
    Workflow.composed<Triple<String, String, String>, Nothing> { props, _, environment ->
      Column {
        WorkflowRendering(rendering = props.first, viewEnvironment = environment)
        BasicText(props.second)
        WorkflowRendering(rendering = props.third, viewEnvironment = environment)
      }
    }

  @Preview @Composable private fun ParentWithTwoChildrenPreview() {
    ParentWithTwoChildren.preview(Triple("one", "two", "three"))
  }

  @Preview @Composable private fun ParentWithModifier() {
    ParentWithOneChild.preview(
        Pair("one", "two"),
        modifier = Modifier.requiredSize(0.dp)
    )
  }

  @Preview @Composable private fun ParentWithPlaceholderModifier() {
    ParentWithOneChild.preview(
        Pair("one", "two"),
        placeholderModifier = Modifier.requiredSize(0.dp)
    )
  }

  object TestEnvironmentKey : ViewEnvironmentKey<String>(String::class) {
    override val default: String get() = error("Not specified")
  }

  private val ParentConsumesCustomKey = Workflow.composed<Unit, Nothing> { _, _, environment ->
    BasicText(environment[TestEnvironmentKey])
  }

  @Preview @Composable private fun ParentConsumesCustomKeyPreview() {
    ParentConsumesCustomKey.preview(Unit) {
      it + (TestEnvironmentKey to "foo")
    }
  }
}
