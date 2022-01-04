package com.jydev.composetest

import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun painterFavorite() = painterResource(R.drawable.ic_line_heart)

enum class AnimationState {
    START, NONE
}

@Composable
fun HeartAnimation(heart : Heart) {
    var animationState by remember { mutableStateOf(AnimationState.NONE) }
    val fraction: Float by animateFloatAsState(
        targetValue = if (animationState == AnimationState.START) 1f else 0f,
        animationSpec = tween(durationMillis = heart.speed, easing = LinearEasing)
    )
    LaunchedEffect(Unit) {
        animationState = AnimationState.START
    }
    HeartAnimationInternal(heart,fraction)
}

@Composable
private fun HeartAnimationInternal(
    heart: Heart,
    fraction: Float
) {

    val heartDrawable = rememberHeartDrawable()
    val heartHeight = heartDrawable.intrinsicHeight * heart.scale
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        translate(width / 2, (height - heartHeight / 2) * (1 - fraction)) {
            scale(heart.scale,heart.scale){
                drawIntoCanvas { canvas ->
                    heartDrawable.draw(canvas.nativeCanvas)
                }
            }
        }
    }
}

@Composable
private fun rememberHeartDrawable(): Drawable {
    val context = LocalContext.current
    val halfWidth = 24.dp.toPx() / 2
    val halfHeight = 20.dp.toPx() / 2
    return remember {
        ContextCompat.getDrawable(context, R.drawable.ic_opacity_heart)!!
            .apply {
                bounds = Rect(-halfWidth, -halfHeight, halfWidth, halfHeight)
            }
    }
}

data class Heart(
    val scale: Float = getRandomFloatRange(0.1f, 1f),
    val speed: Int = (100..3000).random()
)