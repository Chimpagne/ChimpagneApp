package com.monkeyteam.chimpagne

import android.content.Intent
import android.net.Uri
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(AndroidJUnit4::class)
class DeepLinkTests {

    @get:Rule
    val intentsTestRule = IntentsTestRule(MainActivity::class.java)

    @Test
    fun testDeepLink() {
        val deepLink = "https://www.manigo.ch/events/?uid=FIRST_EVENT"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
        intentsTestRule.launchActivity(intent)
        //Release intent
        intentsTestRule.finishActivity()
    }

}