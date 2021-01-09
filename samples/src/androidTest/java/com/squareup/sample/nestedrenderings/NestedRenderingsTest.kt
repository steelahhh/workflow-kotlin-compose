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
package com.squareup.sample.nestedrenderings

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val ADD_BUTTON_TEXT = "Add Child"

@RunWith(AndroidJUnit4::class)
class NestedRenderingsTest {

  // Launches the activity.
  @Rule @JvmField val composeRule = createAndroidComposeRule<NestedRenderingsActivity>()

  @Test fun childrenAreAddedAndRemoved() {
    composeRule.onNodeWithText(ADD_BUTTON_TEXT)
        .assertIsDisplayed()
        .performClick()

    composeRule.onAllNodesWithText(ADD_BUTTON_TEXT)
        .assertCountEquals(2)
        .forEach { it.performClick() }

    composeRule.onAllNodesWithText(ADD_BUTTON_TEXT)
        .assertCountEquals(4)

    resetAll()
    composeRule.onAllNodesWithText(ADD_BUTTON_TEXT).assertCountEquals(1)
  }

  /**
   * We can't rely on the order of nodes returned by [onAllNodesWithText], and the contents of the
   * collection will change as we remove nodes, so we have to double-loop over all reset buttons and
   * click them all until there is only one left.
   */
  private fun resetAll() {
    var foundNodes = Int.MAX_VALUE
    while (foundNodes > 1) {
      foundNodes = 0
      composeRule.onAllNodesWithText("Reset").forEach {
        try {
          it.assertExists()
        } catch (e: AssertionError) {
          // No more reset buttons, we're done.
          return@forEach
        }
        foundNodes++
        it.performClick()
      }
    }
  }

  private fun SemanticsNodeInteractionCollection.forEach(
    block: (SemanticsNodeInteraction) -> Unit
  ) {
    val count = fetchSemanticsNodes().size
    for (i in 0 until count) block(get(i))
  }
}
