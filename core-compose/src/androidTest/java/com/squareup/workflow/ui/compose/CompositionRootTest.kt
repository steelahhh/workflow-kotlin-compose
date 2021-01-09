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
package com.squareup.workflow.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.ui.test.createComposeRule
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertFailsWith

@RunWith(AndroidJUnit4::class)
class CompositionRootTest {

  @Rule @JvmField val composeRule = createComposeRule()

  @Test fun wrapWithRootIfNecessary_wrapsWhenNecessary() {
    val root: CompositionRoot = { content ->
      Column {
        BasicText("one")
        content()
      }
    }

    composeRule.setContent {
      wrapWithRootIfNecessary(root) {
        BasicText("two")
      }
    }

    // These semantics used to merge, but as of dev15, they don't, which seems to be a bug.
    // https://issuetracker.google.com/issues/161979921
    composeRule.onNodeWithText("one").assertIsDisplayed()
    composeRule.onNodeWithText("two").assertIsDisplayed()
  }

  @Test fun wrapWithRootIfNecessary_onlyWrapsOnce() {
    val root: CompositionRoot = { content ->
      Column {
        BasicText("one")
        content()
      }
    }

    composeRule.setContent {
      wrapWithRootIfNecessary(root) {
        BasicText("two")
        wrapWithRootIfNecessary(root) {
          BasicText("three")
        }
      }
    }

    composeRule.onNodeWithText("one").assertIsDisplayed()
    composeRule.onNodeWithText("two").assertIsDisplayed()
    composeRule.onNodeWithText("three").assertIsDisplayed()
  }

  @Test fun wrapWithRootIfNecessary_seesUpdatesFromRootWrapper() {
    val wrapperText = mutableStateOf("one")
    val root: CompositionRoot = { content ->
      Column {
        BasicText(wrapperText.value)
        content()
      }
    }

    composeRule.setContent {
      wrapWithRootIfNecessary(root) {
        BasicText("two")
      }
    }

    composeRule.onNodeWithText("one").assertIsDisplayed()
    composeRule.onNodeWithText("two").assertIsDisplayed()
    wrapperText.value = "ENO"
    composeRule.onNodeWithText("ENO").assertIsDisplayed()
    composeRule.onNodeWithText("two").assertIsDisplayed()
  }

  @Test fun wrapWithRootIfNecessary_rewrapsWhenDifferentRoot() {
    val root1: CompositionRoot = { content ->
      Column {
        BasicText("one")
        content()
      }
    }
    val root2: CompositionRoot = { content ->
      Column {
        BasicText("ENO")
        content()
      }
    }
    val viewEnvironment = mutableStateOf(root1)

    composeRule.setContent {
      wrapWithRootIfNecessary(viewEnvironment.value) {
        BasicText("two")
      }
    }

    composeRule.onNodeWithText("one").assertIsDisplayed()
    composeRule.onNodeWithText("two").assertIsDisplayed()
    viewEnvironment.value = root2
    composeRule.onNodeWithText("ENO").assertIsDisplayed()
    composeRule.onNodeWithText("two").assertIsDisplayed()
  }

  @Test fun safeComposeViewFactoryRoot_wraps_content() {
    val wrapped: CompositionRoot = { content ->
      Column {
        BasicText("Parent")
        content()
      }
    }
    val safeRoot = safeCompositionRoot(wrapped)

    composeRule.setContent {
      safeRoot {
        BasicText("Child")
      }
    }

    composeRule.onNodeWithText("Parent").assertIsDisplayed()
    composeRule.onNodeWithText("Child").assertIsDisplayed()
  }

  @Test fun safeComposeViewFactoryRoot_throws_whenChildrenNotInvoked() {
    val wrapped: CompositionRoot = { }
    val safeRoot = safeCompositionRoot(wrapped)

    val error = assertFailsWith<IllegalStateException> {
      composeRule.setContent {
        safeRoot {}
      }
    }

    assertThat(error).hasMessageThat()
        .isEqualTo(
            "Expected ComposableDecorator to invoke children exactly once, but was invoked 0 times."
        )
  }

  @Test fun safeComposeViewFactoryRoot_throws_whenChildrenInvokedMultipleTimes() {
    val wrapped: CompositionRoot = { children ->
      children()
      children()
    }
    val safeRoot = safeCompositionRoot(wrapped)

    val error = assertFailsWith<IllegalStateException> {
      composeRule.setContent {
        safeRoot {
          BasicText("Hello")
        }
      }
    }

    assertThat(error).hasMessageThat()
        .isEqualTo(
            "Expected ComposableDecorator to invoke children exactly once, but was invoked 2 times."
        )
  }
}
