package com.monkeyteam.chimpagne

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.monkeyteam.chimpagne.ui.components.SimpleTagChip
import com.monkeyteam.chimpagne.ui.components.TagChip
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

class TagTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun tagChip_DisplaysTextAndCallsOnRemove() {
    var clicked = false
    val tagText = "ExampleTag"

    composeTestRule.setContent { TagChip(tag = tagText, onRemove = { clicked = true }) }

    // Check if the text is displayed
    composeTestRule.onNodeWithText(tagText).assertIsDisplayed()

    // Perform a click on the IconButton and check if the callback is triggered
    composeTestRule.onNodeWithContentDescription("Remove tag").performClick()
    assertTrue(clicked)
  }

  @Test
  fun simpleTagChip_DisplaysTextCorrectly() {
    val tagText = "ExampleTag"

    composeTestRule.setContent { SimpleTagChip(tag = tagText) }

    // Check if the text is displayed with '#' prefix
    composeTestRule.onNodeWithText("#$tagText").assertIsDisplayed()
  }
}
