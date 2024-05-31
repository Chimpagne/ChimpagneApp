package com.monkeyteam.chimpagne.model.utils

import java.util.Locale

fun getCurrentLanguage(): String {
  return Locale.getDefault().language
}
