package com.elementary.flashcards.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elementary.flashcards.R

val Paper = Color(0xFFF4E9D8)
val PaperDeep = Color(0xFFE7D7C3)
val CardFace = Color(0xFFFFFBF6)
val Ink = Color(0xFF3A2E26)
val InkSoft = Color(0xFF6E5E52)
val Clay = Color(0xFFC4785A)
val Sage = Color(0xFF5E8B6A)
val Gold = Color(0xFFE2A84A)

private val Nunito = FontFamily(
    Font(R.font.nunito_medium, FontWeight.Normal),
    Font(R.font.nunito_medium, FontWeight.Medium),
    Font(R.font.nunito_bold, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
)

private val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 56.sp,
        lineHeight = 60.sp,
        color = Ink,
    ),
    headlineLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 46.sp,
        color = Ink,
    ),
    headlineMedium = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        color = Ink,
    ),
    titleLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        color = Ink,
    ),
    titleMedium = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        color = Ink,
    ),
    bodyLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        color = Ink,
    ),
    bodyMedium = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        color = Ink,
    ),
    labelLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 22.sp,
        color = Ink,
    ),
)

private val AppColors = lightColorScheme(
    primary = Sage,
    onPrimary = Color.White,
    secondary = Clay,
    onSecondary = Color.White,
    tertiary = Gold,
    background = Paper,
    onBackground = Ink,
    surface = CardFace,
    onSurface = Ink,
    surfaceVariant = PaperDeep,
    onSurfaceVariant = InkSoft,
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun FlashCardsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
