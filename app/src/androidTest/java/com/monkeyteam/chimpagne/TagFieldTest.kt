package com.monkeyteam.chimpagne

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.monkeyteam.chimpagne.ui.components.TagField
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

class TagFieldTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun tagField_DisplaysTagsCorrectly() {
    val selectedTags = listOf("student", "disco")

    composeTestRule.setContent { TagField(selectedTags, {}, {}, Modifier) }

    // Check if all tags are displayed
    selectedTags.forEach { tag -> composeTestRule.onNodeWithText(tag).assertIsDisplayed() }
  }

  @Test
  fun tagField_AddsTagCorrectly() {
    var tags = listOf("student", "disco")
    val newTag = "vegan"

    composeTestRule.setContent { TagField(tags, { tags = it }, {}, Modifier) }

    // Simulate adding a new tag (adapt this to your actual UI logic)
    // This might involve performing a text input followed by a click on a suggestion or a 'done'
    // action
    tags += newTag

    assertTrue(tags.contains(newTag))
  }
}
