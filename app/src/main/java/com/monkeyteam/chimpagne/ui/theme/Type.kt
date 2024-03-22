package com.monkeyteam.chimpagne.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val ChimpagneTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = ChimpagneFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = ChimpagneFontFamily,
        fontSize = 24.sp,
        lineHeight = 28.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = ChimpagneFontFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodySmall = TextStyle(
        fontFamily = ChimpagneFontFamily,
        fontSize = 12.sp,
        lineHeight = 18.sp
    )
)
