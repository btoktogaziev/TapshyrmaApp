package com.example.tapshyrmaapp.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    headlineLarge = TextStyle(
        fontSize = 28.sp,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium
    ),
    bodyLarge = TextStyle(
        fontSize = 24.sp,
        fontFamily = FontFamily.SansSerif
    ),
    bodyMedium = TextStyle(
        fontSize = 20.sp,
        fontFamily = FontFamily.SansSerif,
    ),
    labelLarge = TextStyle(
        fontSize = 28.sp,
        fontFamily = FontFamily.SansSerif,
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.W200,
    )
)