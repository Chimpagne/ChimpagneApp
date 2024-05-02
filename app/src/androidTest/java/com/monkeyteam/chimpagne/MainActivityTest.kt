package com.monkeyteam.chimpagne

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

  @Test
  fun testActivityLaunches() {
    ActivityScenario.launch(MainActivity::class.java).use {}
  }
}
