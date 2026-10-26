package com.annu.ai.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.annu.ai.theme.NeonCyan
import com.annu.ai.theme.NeonPink
import com.annu.ai.theme.RadiantViolet
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AiCoreOrb(
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    isThinking: Boolean = false,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "AiCoreOrbTransition")

    // Rotation angle
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isThinking) 2500 else 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "coreRotation"
    )

    // Pulse animation
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isThinking) 600 else 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "corePulse"
    )

    // Outer glow pulse
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isThinking) 450 else 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = modifier
            .size(size)
            .clickable(onClick = onClick)
            .testTag("ai_core_orb"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = this.size.minDimension / 2.6f * pulse

            // 1. Ambient outer aura gradient
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = glowAlpha * 0.45f),
                        RadiantViolet.copy(alpha = glowAlpha * 0.25f),
                        NeonPink.copy(alpha = glowAlpha * 0.1f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 1.5f
                ),
                radius = baseRadius * 1.5f,
                center = center
            )

            // 2. Outer segmented orbital ring
            val outerRadius = baseRadius * 1.25f
            val segments = 12
            for (i in 0 until segments) {
                val startAngle = (i * (360f / segments)) + rotation
                drawArc(
                    color = if (i % 2 == 0) NeonCyan.copy(alpha = 0.7f) else RadiantViolet.copy(alpha = 0.5f),
                    startAngle = startAngle,
                    sweepAngle = 18f,
                    useCenter = false,
                    topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                    size = androidx.compose.ui.geometry.Size(outerRadius * 2, outerRadius * 2),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // 3. Counter-rotating inner energy ring
            val innerRingRadius = baseRadius * 0.95f
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(NeonCyan, RadiantViolet, NeonPink, NeonCyan),
                    center = center
                ),
                startAngle = -rotation * 1.6f,
                sweepAngle = 280f,
                useCenter = false,
                topLeft = Offset(center.x - innerRingRadius, center.y - innerRingRadius),
                size = androidx.compose.ui.geometry.Size(innerRingRadius * 2, innerRingRadius * 2),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            // 4. Central Core Sphere
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        NeonCyan,
                        RadiantViolet.copy(alpha = 0.85f),
                        Color(0xFF090D18)
                    ),
                    center = center,
                    radius = baseRadius * 0.65f
                ),
                radius = baseRadius * 0.65f,
                center = center
            )

            // 5. Constellation nodes
            val nodeCount = 6
            for (n in 0 until nodeCount) {
                val angleRad = Math.toRadians((n * (360.0 / nodeCount) + rotation).toDouble())
                val nodeX = center.x + (baseRadius * 0.78f * cos(angleRad)).toFloat()
                val nodeY = center.y + (baseRadius * 0.78f * sin(angleRad)).toFloat()
                drawCircle(
                    color = if (n % 2 == 0) Color.White else NeonCyan,
                    radius = 3.5.dp.toPx(),
                    center = Offset(nodeX, nodeY)
                )
            }
        }
    }
}
