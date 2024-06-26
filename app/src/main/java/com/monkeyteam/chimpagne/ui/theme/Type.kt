package com.monkeyteam.chimpagne.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val ChimpagneTypography =
    Typography(
        displayLarge =
            TextStyle(
                fontFamily = ChimpagneFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                lineHeight = 40.sp),
        displayMedium =
            TextStyle(
                fontFamily = ChimpagneFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                lineHeight = 24.sp),
        displaySmall =
            TextStyle(
                fontFamily = ChimpagneFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 18.0.sp),
        headlineMedium =
            TextStyle(fontFamily = ChimpagneFontFamily, fontSize = 24.sp, lineHeight = 28.sp),
        bodyMedium =
            TextStyle(fontFamily = ChimpagneFontFamily, fontSize = 14.sp, lineHeight = 20.sp),
        bodyLarge =
            TextStyle(fontFamily = ChimpagneFontFamily, fontSize = 18.sp, lineHeight = 24.sp),
        bodySmall =
            TextStyle(fontFamily = ChimpagneFontFamily, fontSize = 12.sp, lineHeight = 18.sp),
        labelSmall =
            TextStyle(
                fontFamily = ChimpagneFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp),
        titleMedium =
            TextStyle(fontFamily = ChimpagneFontFamily, fontSize = 20.sp, lineHeight = 24.sp),
        titleSmall =
            TextStyle(fontFamily = ChimpagneFontFamily, fontSize = 16.sp, lineHeight = 24.sp),
        titleLarge =
            TextStyle(
                fontFamily = ChimpagneFontFamily,
                fontSize = 24.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold))
