package com.elementary.flashcards.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

enum class TurtleMood { Curious, Pleased, Unbothered }

@Composable
fun Turtle(mood: TurtleMood, modifier: Modifier = Modifier) {
    val bobber = rememberInfiniteTransition(label = "turtle")
    val bob by bobber.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bob",
    )
    Canvas(modifier) {
        val w = size.minDimension
        val left = (size.width - w) / 2f
        val top = (size.height - w) / 2f + bob * w * 0.035f
        fun at(x: Float, y: Float) = Offset(left + x * w, top + y * w)

        val skin = Color(0xFF7EAF6E)
        val shell = Color(0xFF5E8B62)
        val shellDark = Color(0xFF3E6848)
        val belly = Color(0xFFF3D7A8)
        val ink = Color(0xFF2C241E)

        drawOval(skin, topLeft = at(0.16f, 0.50f), size = Size(w * 0.16f, w * 0.11f))
        drawOval(skin, topLeft = at(0.68f, 0.50f), size = Size(w * 0.16f, w * 0.11f))
        drawOval(skin, topLeft = at(0.22f, 0.72f), size = Size(w * 0.16f, w * 0.11f))
        drawOval(skin, topLeft = at(0.62f, 0.72f), size = Size(w * 0.16f, w * 0.11f))

        val tail = Path().apply {
            moveTo(at(0.46f, 0.78f).x, at(0.46f, 0.78f).y)
            lineTo(at(0.54f, 0.78f).x, at(0.54f, 0.78f).y)
            lineTo(at(0.50f, 0.90f).x, at(0.50f, 0.90f).y)
            close()
        }
        drawPath(tail, shellDark)

        drawOval(shell, topLeft = at(0.20f, 0.42f), size = Size(w * 0.60f, w * 0.40f))
        drawOval(belly, topLeft = at(0.34f, 0.52f), size = Size(w * 0.32f, w * 0.20f))
        drawArc(
            color = shellDark,
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = at(0.30f, 0.48f),
            size = Size(w * 0.40f, w * 0.26f),
            style = Stroke(width = w * 0.015f),
        )

        val headY = when (mood) {
            TurtleMood.Curious -> 0.28f
            TurtleMood.Pleased -> 0.30f
            TurtleMood.Unbothered -> 0.33f
        }
        val headX = if (mood == TurtleMood.Curious) 0.58f else 0.50f
        drawCircle(skin, radius = w * 0.14f, center = at(headX, headY))

        val eyeY = headY - 0.02f
        val leftEye = at(headX - 0.045f, eyeY)
        val rightEye = at(headX + 0.045f, eyeY)
        when (mood) {
            TurtleMood.Pleased -> {
                fun smileEye(center: Offset) {
                    drawArc(
                        color = ink,
                        startAngle = 200f,
                        sweepAngle = 140f,
                        useCenter = false,
                        topLeft = Offset(center.x - w * 0.03f, center.y - w * 0.015f),
                        size = Size(w * 0.06f, w * 0.045f),
                        style = Stroke(width = w * 0.012f, cap = StrokeCap.Round),
                    )
                }
                smileEye(leftEye)
                smileEye(rightEye)
            }
            TurtleMood.Unbothered -> {
                drawLine(
                    ink,
                    Offset(leftEye.x - w * 0.025f, leftEye.y),
                    Offset(leftEye.x + w * 0.025f, leftEye.y),
                    strokeWidth = w * 0.012f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    ink,
                    Offset(rightEye.x - w * 0.025f, rightEye.y),
                    Offset(rightEye.x + w * 0.025f, rightEye.y),
                    strokeWidth = w * 0.012f,
                    cap = StrokeCap.Round,
                )
            }
            TurtleMood.Curious -> {
                drawCircle(Color.White, w * 0.024f, leftEye)
                drawCircle(Color.White, w * 0.024f, rightEye)
                drawCircle(ink, w * 0.011f, Offset(leftEye.x - w * 0.006f, leftEye.y - w * 0.008f))
                drawCircle(ink, w * 0.011f, Offset(rightEye.x - w * 0.006f, rightEye.y - w * 0.008f))
            }
        }

        val mouth = at(headX, headY + 0.055f)
        when (mood) {
            TurtleMood.Pleased -> drawArc(
                color = ink,
                startAngle = 15f,
                sweepAngle = 150f,
                useCenter = false,
                topLeft = Offset(mouth.x - w * 0.04f, mouth.y - w * 0.02f),
                size = Size(w * 0.08f, w * 0.05f),
                style = Stroke(width = w * 0.012f, cap = StrokeCap.Round),
            )
            TurtleMood.Unbothered -> drawLine(
                ink,
                Offset(mouth.x - w * 0.03f, mouth.y),
                Offset(mouth.x + w * 0.03f, mouth.y),
                strokeWidth = w * 0.01f,
                cap = StrokeCap.Round,
            )
            TurtleMood.Curious -> drawArc(
                color = ink,
                startAngle = 20f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(mouth.x - w * 0.028f, mouth.y - w * 0.01f),
                size = Size(w * 0.056f, w * 0.04f),
                style = Stroke(width = w * 0.01f, cap = StrokeCap.Round),
            )
        }
    }
}
