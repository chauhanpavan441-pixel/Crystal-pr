package com.hhst.youtubelite.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.hhst.youtubelite.ui.theme.GlassConfig
import kotlin.math.sin
import kotlin.random.Random

data class Snowflake(
    var x: Float,
    var y: Float,
    val speed: Float,
    val radius: Float,
    val alpha: Float,
    val sway: Float
)

@Composable
fun SnowEffect(modifier: Modifier = Modifier) {
    if (!GlassConfig.isSnowEnabled) return

    val snowflakes = remember { mutableStateListOf<Snowflake>() }
    val random = Random(System.currentTimeMillis())

    // स्क्रीन पर बर्फ के कण (particles) बनाना
    LaunchedEffect(Unit) {
        for (i in 0..100) {
            snowflakes.add(
                Snowflake(
                    x = random.nextFloat(),
                    y = random.nextFloat(),
                    speed = random.nextFloat() * 0.002f + 0.001f,
                    radius = random.nextFloat() * 4f + 2f,
                    alpha = random.nextFloat() * 0.5f + 0.3f,
                    sway = random.nextFloat() * 0.005f
                )
            )
        }
    }

    // बर्फ को गिराने का एनिमेशन (Animation)
    val infiniteTransition = rememberInfiniteTransition(label = "snow")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val sizeMultiplier = GlassConfig.snowSize
        
        snowflakes.forEach { flake ->
            // बर्फ नीचे गिरेगी
            flake.y += flake.speed
            if (flake.y > 1f) {
                flake.y = 0f
                flake.x = random.nextFloat()
            }
            
            // हवा में हिलने का इफ़ेक्ट (Sway)
            val swayX = sin(time + flake.y * 10f) * flake.sway
            val drawX = (flake.x + swayX) * size.width
            val drawY = flake.y * size.height

            drawCircle(
                color = Color.White.copy(alpha = flake.alpha),
                radius = flake.radius * sizeMultiplier,
                center = Offset(drawX, drawY)
            )
        }
    }
}