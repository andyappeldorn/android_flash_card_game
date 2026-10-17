package com.elementary.flashcards.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.elementary.flashcards.ui.theme.Clay
import com.elementary.flashcards.ui.theme.Gold
import com.elementary.flashcards.ui.theme.Sage
import kotlin.random.Random

private data class Spark(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val gravity: Float,
    val radius: Float,
    val color: Color,
    val alpha: Float,
)

@Composable
fun Fireworks(modifier: Modifier = Modifier) {
    val sparks = remember { burst() }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(durationMillis = 1500, easing = LinearEasing))
    }
    Canvas(modifier) {
        val t = progress.value
        sparks.forEach { spark ->
            val x = spark.x + spark.vx * t
            val y = spark.y + spark.vy * t + spark.gravity * t * t
            val alpha = ((1f - t) * spark.alpha).coerceIn(0f, 1f)
            if (alpha <= 0.02f) return@forEach
            drawCircle(
                color = spark.color.copy(alpha = alpha),
                radius = spark.radius * size.minDimension,
                center = Offset(x * size.width, y * size.height),
            )
        }
    }
}

private fun burst(): List<Spark> {
    val random = Random(7)
    val colors = listOf(Gold, Clay, Sage, Color(0xFFFFF6E4), Color(0xFFE7B07A))
    val origins = listOf(0.28f to 0.22f, 0.72f to 0.18f, 0.50f to 0.30f)
    return origins.flatMap { (ox, oy) ->
        List(16) {
            Spark(
                x = ox,
                y = oy,
                vx = random.nextFloat() * 0.55f - 0.275f,
                vy = -0.15f - random.nextFloat() * 0.45f,
                gravity = 0.35f + random.nextFloat() * 0.25f,
                radius = 0.006f + random.nextFloat() * 0.008f,
                color = colors[random.nextInt(colors.size)],
                alpha = 0.55f + random.nextFloat() * 0.3f,
            )
        }
    }
}
