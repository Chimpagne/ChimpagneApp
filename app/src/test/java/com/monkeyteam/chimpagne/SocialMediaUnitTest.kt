package com.monkeyteam.chimpagne

import com.monkeyteam.chimpagne.ui.event.createFullUrl
import org.junit.Assert.assertEquals
import org.junit.Test

class SocialMediaUnitTest {
  @Test
  fun testEmptyUrl() {
    val platformUrls = listOf("https://www.discord.com/", "https://discord.gg/")
    val url = ""
    val result = createFullUrl(platformUrls, url)
    assertEquals("", result)
  }

  @Test
  fun testUrlWithoutMatchingPlatform() {
    val platformUrls = listOf("https://www.discord.com/", "https://discord.gg/")
    val url = "https://www.linkedin.com/in/johndoe"
    val result = createFullUrl(platformUrls, url)
    assertEquals("https://www.linkedin.com/in/johndoe", result)
  }

  @Test
  fun testUrlWithMatchingPlatform() {
    val platformUrls = listOf("https://www.discord.com/", "https://discord.gg/")
    val url = "https://www.discord.com/123"
    val result = createFullUrl(platformUrls, url)
    assertEquals("https://www.discord.com/123", result)
  }
}
